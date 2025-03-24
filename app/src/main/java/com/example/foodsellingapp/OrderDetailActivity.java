package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.foodsellingapp.Adapter.OrderDetail_Adapter;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityOrderDetailBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OrderDetailActivity extends AppCompatActivity {

    private ActivityOrderDetailBinding binding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DonHang donHang;
    private String ngayXacNhanDon;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String deleteReason, customerEmail;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private Gmail gmailService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(GmailScopes.GMAIL_SEND))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get intent and object from UserAdapter Dsdonhang
        Intent intent = getIntent();
        donHang = (DonHang) intent.getSerializableExtra("donHang");
        if (donHang != null) {
            binding.txtMaDH.setText(donHang.getMaDH());
            binding.txtNgayDat.setText(donHang.getNgayTao());
            binding.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
            getTenKH();
            binding.txtPhuongThucNhanHang.setText(donHang.getPtNhanHang());
            // Display payment method and gateway (if available)
            String paymentText = donHang.getCongThanhToan() == null
                    ? donHang.getPtThanhToan()
                    : donHang.getPtThanhToan() + " | " + donHang.getCongThanhToan();
            binding.txtCongThanhToan.setText(paymentText);
            binding.txtTotal.setText(Format.formatVND(donHang.getTongCong()));
            if (donHang.getLyDo() != null) {
                binding.txtLyDo.setText("* ĐÃ HỦY: " + donHang.getLyDo());
                binding.txtLyDo.setVisibility(View.VISIBLE);
            }
        }

        // Hide buttons if the logged-in user is the order owner
        if (firebaseUser.getUid().equals(donHang.getMaKH())) {
            binding.btnXacNhanDon.setVisibility(View.GONE);
            binding.btnHuyDon.setVisibility(View.GONE);
        } else {
            // Show confirm button only for "choXacNhan" status
            if ("choXacNhan".equals(donHang.getTrangThai())) {
                binding.btnXacNhanDon.setVisibility(View.VISIBLE);
                binding.btnHuyDon.setVisibility(View.VISIBLE);
                binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
            }

            // Always allow admin to cancel (if not hidden)
            binding.btnHuyDon.setOnClickListener(v -> showDeleteConfirmDialog(donHang));
        }

        loadOrderDetails(donHang.getMaDH());
    }

    private void loadOrderDetails(String maDH) {
        firestore.collection("DonHang").document(maDH)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DonHang donHang = documentSnapshot.toObject(DonHang.class);
                        if (donHang != null) {
                            donHang.setMaDH(documentSnapshot.getId());
                            binding.txtMaDH.setText("#" + donHang.getMaDH());
                            binding.txtTotal.setText(Format.formatVND(donHang.getTongCong()));

                            // Tạo tên subcollection dựa trên maDH (ví dụ: CT1742280458318)
                            String ctCollectionName = "CT" + maDH;
                            fetchItemsFromSubcollection(maDH, ctCollectionName, donHang);
                        }
                    } else {
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchItemsFromSubcollection(String maDH, String ctCollectionName, DonHang donHang) {
        firestore.collection("DonHang").document(maDH)
                .collection(ctCollectionName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<CTDonHang> allItems = new ArrayList<>();
                    int itemCount = querySnapshot.size(); // Số lượng document "maCTDH"
                    Toast.makeText(this, "Found " + itemCount + " product details", Toast.LENGTH_SHORT).show();

                    // Use a counter to ensure all images are fetched before proceeding
                    final int[] pendingFetches = {itemCount};
                    if (itemCount == 0) {
                        // If there are no items, set the adapter immediately
                        donHang.setItems(allItems);
                        binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
                        OrderDetail_Adapter orderDetailAdapter = new OrderDetail_Adapter(this, donHang.getItems());
                        binding.rvItems.setAdapter(orderDetailAdapter);
                        return;
                    }

                    for (DocumentSnapshot itemDoc : querySnapshot) {
                        if (itemDoc.exists()) {
                            double giaDouble = itemDoc.getDouble("gia") != null ? itemDoc.getDouble("gia") : 0.0;
                            int gia = (int) Math.round(giaDouble);
                            int sl = itemDoc.getLong("sl") != null ? itemDoc.getLong("sl").intValue() : 0;
                            String maMon = itemDoc.getString("maMon");

                            // Fetch the image for this item
                            getImageForItems(maMon, (imgUrl) -> {
                                CTDonHang item = new CTDonHang(
                                        maDH,
                                        gia,
                                        itemDoc.getString("khuyenMai"),
                                        sl,
                                        itemDoc.getString("tenMon"),
                                        maMon,
                                        imgUrl
                                );
                                allItems.add(item);

                                // Decrease the counter
                                pendingFetches[0]--;
                                if (pendingFetches[0] == 0) {
                                    // All images have been fetched, set the adapter
                                    donHang.setItems(allItems);
                                    binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
                                    OrderDetail_Adapter orderDetailAdapter = new OrderDetail_Adapter(this, donHang.getItems());
                                    binding.rvItems.setAdapter(orderDetailAdapter);
                                }
                            });
                        } else {
                            pendingFetches[0]--;
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private interface ImageFetchCallback {
        void onImageFetched(String imgUrl);
    }

    private void getImageForItems(String maMon, ImageFetchCallback callback) {
        firestore.collection("MonAn").document(maMon)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String imgUrl = documentSnapshot.getString("url");
                    callback.onImageFetched(imgUrl != null ? imgUrl : "https://via.placeholder.com/100");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching image item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onImageFetched("https://via.placeholder.com/100"); // Fallback image
                });
    }

    private void confirmOrder() {
        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "XacNhan");
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(OrderDetailActivity.this, "Xác nhận đơn!", Toast.LENGTH_SHORT).show();
                    // Update UI
                    donHang.setTrangThai("XacNhan");
                    donHang.setNgayXacNhan(ngayXacNhanDon);
                    binding.txtNgayXacNhan.setText(ngayXacNhanDon);
                    binding.btnXacNhanDon.setVisibility(View.GONE);
                    binding.btnHuyDon.setVisibility(View.GONE);

                    // Start Google Sign-In flow to send email
                    signIn();
                })
                .addOnFailureListener(e -> Toast.makeText(OrderDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d("SendEmail", "Signed in as: " + account.getEmail());

                // Get the access token
                String accessToken = account.getIdToken();
                if (accessToken != null) {
                    GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null))
                            .createScoped(GmailScopes.GMAIL_SEND);

                    GoogleNetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                    GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    gmailService = new Gmail.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                            .setApplicationName("FoodSellingApp")
                            .build();

                    // Send email
                    new Thread(() -> {
                        try {
                            String subject = "Đơn hàng #" + donHang.getMaDH() + " của bạn đã được giao thành công ngày " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date());
                            String body = createEmailBody(donHang);
                            MimeMessage email = createEmail(customerEmail, "me", subject, body);
                            Message message = createMessageWithEmail(email);
                            gmailService.users().messages().send("me", message).execute();
                            runOnUiThread(() -> Toast.makeText(this, "Confirmation email sent to " + customerEmail, Toast.LENGTH_SHORT).show());
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(this, "Error sending email: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            Log.e("SendEmail", "Error sending email", e);
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "Failed to get access token", Toast.LENGTH_LONG).show();
                }
            } catch (ApiException e) {
                Log.e("SendEmail", "Sign-in failed", e);
                Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("SendEmail", "Error initializing Gmail service", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showDeleteConfirmDialog(DonHang donHang) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button cancelOrder = dialogView.findViewById(R.id.cancelButton);
        Button deleteOrder = dialogView.findViewById(R.id.deleteButton);

        // Adjust the dialog window to prevent clipping
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        cancelOrder.setOnClickListener(v -> dialog.dismiss());
        deleteOrder.setOnClickListener(v -> {
            dialog.dismiss();
            showResonBottomSheet(donHang);
        });

        dialog.show();
    }

    public void showResonBottomSheet(DonHang donHang) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_delete_order, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        EditText edtReason = bottomSheetView.findViewById(R.id.reasonEditText);
        Button saveReason = bottomSheetView.findViewById(R.id.saveButton);

        saveReason.setOnClickListener(v -> {
            deleteReason = edtReason.getText().toString().trim();
            if (!deleteReason.isEmpty()) {
                updateDeleteOrder(deleteReason, donHang);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a reason for cancellation", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    public void updateDeleteOrder(String deleteReason, DonHang donHang) {
        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "Huy");
        hashMap.put("lyDo", deleteReason);
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(OrderDetailActivity.this, "Hủy đơn!", Toast.LENGTH_SHORT).show();
                    // Update UI
                    donHang.setTrangThai("Huy");
                    donHang.setLyDo(deleteReason);
                    donHang.setNgayXacNhan(ngayXacNhanDon);
                    binding.txtLyDo.setText("* ĐÃ HỦY: " + deleteReason);
                    binding.txtLyDo.setVisibility(View.VISIBLE);
                    binding.btnXacNhanDon.setVisibility(View.GONE);
                    binding.btnHuyDon.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(OrderDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void getTenKH() {
        DocumentReference documentReference = firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        binding.txtCus.setText(task.getResult().getString("name"));
                        customerEmail = task.getResult().getString("email");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching customer: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void transferDateTime() {
        long timestampt = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestampt);
        ngayXacNhanDon = (String) DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar);
    }

    private String createEmailBody(DonHang donHang) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }")
                .append("h1 { color: #ff6200; font-size: 24px; }")
                .append("p { font-size: 16px; color: #333; }")
                .append(".button { background-color: #ff6200; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 20px 0; border-radius: 5px; }")
                .append(".info { font-size: 16px; margin-top: 20px; }")
                .append(".info-label { font-weight: bold; }")
                .append(".product { display: flex; align-items: center; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px; }")
                .append(".product img { width: 100px; height: auto; margin-right: 20px; }")
                .append(".product-details { flex: 1; }")
                .append(".footer { margin-top: 20px; color: #777; font-size: 14px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h1>Đơn hàng #" + donHang.getMaDH() + " của bạn đã được giao thành công ngày " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date()) + "</h1>")
                .append("<p>Vui lòng đánh giá FoodSellingApp để xác nhận bạn đã nhận hàng và hài lòng với sản phẩm trong vòng 3 ngày. Sau khi bạn xác nhận, chúng tôi sẽ thanh toán cho Người bán.</p>")
                .append("<p>Nếu không xác nhận trong khoảng thời gian này, FoodSellingApp sẽ tự động xác nhận cho Người bán.</p>")
                .append("<a href='#' class='button'>Đã nhận hàng</a>")
                .append("<div class='info'>")
                .append("<p><span class='info-label'>Mã đơn hàng:</span> #" + donHang.getMaDH() + "</p>")
                .append("<p><span class='info-label'>Ngày đặt hàng:</span> " + (donHang.getNgayTao() != null ? donHang.getNgayTao() : "N/A") + "</p>")
                .append("<p><span class='info-label'>Nguồn bán:</span> FoodSellingApp</p>")
                .append("</div>");

        // Check if items are available
        if (donHang.getItems() != null && !donHang.getItems().isEmpty()) {
            for (CTDonHang item : donHang.getItems()) {
                int thanhTien = item.getGia() * item.getSl();
                body.append("<div class='product'>")
                        .append("<img src='").append(item.getItemImage() != null ? item.getItemImage() : "https://via.placeholder.com/100").append("' alt='").append(item.getTenMon()).append("' />")
                        .append("<div class='product-details'>")
                        .append("<p><strong>").append(item.getTenMon()).append("</strong></p>")
                        .append("<p>Số lượng: ").append(item.getSl()).append("</p>")
                        .append("<p>Giá: ").append(item.getGia()).append(" VNĐ</p>")
                        .append("<p>Thành tiền: ").append(thanhTien).append(" VNĐ</p>")
                        .append("</div>")
                        .append("</div>");
            }
        } else {
            body.append("<p>Không có sản phẩm nào trong đơn hàng.</p>");
        }

        body.append("<p class='footer'>Cảm ơn bạn đã mua sắm tại FoodSellingApp! Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>")
                .append("</body>")
                .append("</html>");

        return body.toString();
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setContent(bodyText, "text/html; charset=utf-8");
        return email;
    }

    private Message createMessageWithEmail(MimeMessage email) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}