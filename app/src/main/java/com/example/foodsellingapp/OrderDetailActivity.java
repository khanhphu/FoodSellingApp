package com.example.foodsellingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodsellingapp.Adapter.OrderDetail_Adapter;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityOrderDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    private ActivityOrderDetailBinding binding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DonHang donHang;
    private String ngayXacNhanDon;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String deleteReason;
    private String customerEmail;
    private ProgressDialog progressDialog;
    private boolean isItemsLoaded = false; // Biến để kiểm tra xem items đã tải xong chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);

        // Vô hiệu hóa nút xác nhận và hủy ban đầu
        binding.btnXacNhanDon.setEnabled(false);
        binding.btnHuyDon.setEnabled(false);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        donHang = (DonHang) intent.getSerializableExtra("donHang");
        if (donHang != null) {
            binding.txtMaDH.setText(donHang.getMaDH());
            binding.txtNgayDat.setText(donHang.getNgayTao());
            binding.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
            binding.txtPhuongThucNhanHang.setText(donHang.getPtNhanHang());
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

        // Ẩn nút nếu người dùng hiện tại là chủ đơn hàng
        if (firebaseUser.getUid().equals(donHang.getMaKH())) {
            binding.btnXacNhanDon.setVisibility(View.GONE);
            binding.btnHuyDon.setVisibility(View.GONE);
        } else {
            if ("choXacNhan".equals(donHang.getTrangThai())) {
                binding.btnXacNhanDon.setVisibility(View.VISIBLE);
                binding.btnHuyDon.setVisibility(View.VISIBLE);
            }
            binding.btnHuyDon.setOnClickListener(v -> showDeleteConfirmDialog(donHang));
        }

        // Lấy thông tin khách hàng và tải chi tiết đơn hàng
        getTenKH();
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
                            this.donHang = donHang; // Cập nhật donHang toàn cục

                            // Tạo tên subcollection dựa trên maDH (ví dụ: CT1742280458318)
                            String ctCollectionName = "CT" + maDH;
                            fetchItemsFromSubcollection(maDH, ctCollectionName, donHang);
                        }
                    } else {
                        Toast.makeText(this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchItemsFromSubcollection(String maDH, String ctCollectionName, DonHang donHang) {
        progressDialog.setMessage("Đang tải hình ảnh sản phẩm...");
        progressDialog.show();

        firestore.collection("DonHang").document(maDH)
                .collection(ctCollectionName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<CTDonHang> allItems = new ArrayList<>();
                    List<Task<DocumentSnapshot>> imageTasks = new ArrayList<>();
                    int itemCount = querySnapshot.size();
                    Toast.makeText(this, "Tìm thấy " + itemCount + " sản phẩm", Toast.LENGTH_SHORT).show();

                    // Tạo danh sách sản phẩm và thu thập các task lấy hình ảnh
                    for (DocumentSnapshot itemDoc : querySnapshot) {
                        if (itemDoc.exists()) {
                            double giaDouble = itemDoc.getDouble("gia") != null ? itemDoc.getDouble("gia") : 0.0;
                            int gia = (int) Math.round(giaDouble);
                            int sl = itemDoc.getLong("sl") != null ? itemDoc.getLong("sl").intValue() : 0;
                            String maMon = itemDoc.getString("maMon");
                            String tenMon = itemDoc.getString("tenMon");
                            String khuyenMai = itemDoc.getString("khuyenMai");

                            // Tạo CTDonHang tạm thời (chưa có hình ảnh)
                            CTDonHang item = new CTDonHang(
                                    maDH,
                                    gia,
                                    khuyenMai,
                                    sl,
                                    tenMon,
                                    maMon,
                                    null // Hình ảnh sẽ được gán sau
                            );
                            allItems.add(item);

                            // Thêm task lấy hình ảnh vào danh sách
                            Task<DocumentSnapshot> imageTask = getImageForItems(maMon);
                            imageTasks.add(imageTask);
                        }
                    }

                    // Chờ tất cả hình ảnh được tải
                    if (!imageTasks.isEmpty()) {
                        Tasks.whenAllSuccess(imageTasks).addOnSuccessListener(results -> {
                            // Gán hình ảnh cho từng sản phẩm
                            for (int i = 0; i < results.size(); i++) {
                                DocumentSnapshot imageDoc = (DocumentSnapshot) results.get(i);
                                String imgUrl = imageDoc.getString("url");
                                allItems.get(i).setItemImage(imgUrl);
                            }

                            // Gán danh sách sản phẩm vào donHang
                            donHang.setItems(allItems);
                            isItemsLoaded = true; // Đánh dấu items đã tải xong

                            // Hiển thị danh sách sản phẩm trong RecyclerView
                            binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
                            OrderDetail_Adapter orderDetailAdapter = new OrderDetail_Adapter(this, donHang.getItems());
                            binding.rvItems.setAdapter(orderDetailAdapter);

                            // Kích hoạt nút xác nhận và hủy chỉ khi cả email và items đã tải
                            if ("choXacNhan".equals(donHang.getTrangThai()) && customerEmail != null && !customerEmail.isEmpty() && isItemsLoaded) {
                                binding.btnXacNhanDon.setEnabled(true);
                                binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
                            }
                            binding.btnHuyDon.setEnabled(true);

                            // Ẩn ProgressDialog
                            progressDialog.dismiss();

                            Log.d("ItemsDebug", "Items loaded: " + (donHang.getItems() != null ? donHang.getItems().size() : "null"));
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi khi tải hình ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            donHang.setItems(allItems);
                            isItemsLoaded = true; // Đánh dấu items đã tải, dù không có hình ảnh

                            binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
                            OrderDetail_Adapter orderDetailAdapter = new OrderDetail_Adapter(this, donHang.getItems());
                            binding.rvItems.setAdapter(orderDetailAdapter);

                            if ("choXacNhan".equals(donHang.getTrangThai()) && customerEmail != null && !customerEmail.isEmpty() && isItemsLoaded) {
                                binding.btnXacNhanDon.setEnabled(true);
                                binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
                            }
                            binding.btnHuyDon.setEnabled(true);
                            progressDialog.dismiss();
                        });
                    } else {
                        donHang.setItems(allItems);
                        isItemsLoaded = true; // Đánh dấu items đã tải (dù không có sản phẩm)

                        if ("choXacNhan".equals(donHang.getTrangThai()) && customerEmail != null && !customerEmail.isEmpty() && isItemsLoaded) {
                            binding.btnXacNhanDon.setEnabled(true);
                            binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
                        }
                        binding.btnHuyDon.setEnabled(true);
                        progressDialog.dismiss();
                        Log.d("ItemsDebug", "No items found for this order.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if ("choXacNhan".equals(donHang.getTrangThai()) && customerEmail != null && !customerEmail.isEmpty()) {
                        binding.btnXacNhanDon.setEnabled(true);
                        binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
                    }
                    binding.btnHuyDon.setEnabled(true);
                    progressDialog.dismiss();
                });
    }

    private Task<DocumentSnapshot> getImageForItems(String maMon) {
        return firestore.collection("MonAn").document(maMon)
                .get()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy hình ảnh sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmOrder() {
        // Kiểm tra xem items đã được tải chưa
        if (!isItemsLoaded || donHang.getItems() == null || donHang.getItems().isEmpty()) {
            Toast.makeText(this, "Danh sách sản phẩm chưa được tải. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
            return;
        }

        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "XacNhan");
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap).addOnSuccessListener(unused -> {
            Toast.makeText(OrderDetailActivity.this, "Xác nhận đơn!", Toast.LENGTH_SHORT).show();
            donHang.setNgayXacNhan(ngayXacNhanDon);

            progressDialog.setMessage("Đang gửi email xác nhận...");
            progressDialog.show();

            if (customerEmail != null && !customerEmail.isEmpty()) {
                Log.d("EmailDebug", "Sending email to: " + customerEmail);
                String htmlBody = generateOrderConfirmationEmail(donHang, donHang.getItems());
                EmailSender.sendEmail(customerEmail, "Xác nhận Đơn hàng #" + donHang.getMaDH(), htmlBody, new EmailSender.EmailCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(OrderDetailActivity.this, "Email xác nhận đã được gửi! Vui lòng kiểm tra hộp thư (bao gồm Spam/Junk)", Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(OrderDetailActivity.this, "Không thể gửi email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(OrderDetailActivity.this, "Không tìm thấy email khách hàng", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(OrderDetailActivity.this, "Lỗi khi xác nhận đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String generateOrderConfirmationEmail(DonHang donHang, List<CTDonHang> items) {
        StringBuilder itemsHtml = new StringBuilder();
        if (items != null && !items.isEmpty()) {
            for (CTDonHang item : items) {
                itemsHtml.append("<tr>")
                        .append("<td><img src='").append(item.getItemImage() != null ? item.getItemImage() : "").append("' alt='Hình ảnh sản phẩm' style='width: 50px; height: auto;'/></td>")
                        .append("<td>").append(item.getTenMon() != null ? item.getTenMon() : "Không xác định").append("</td>")
                        .append("<td>").append(item.getSl()).append("</td>")
                        .append("<td>").append(Format.formatVND(item.getGia())).append("</td>")
                        .append("</tr>");
            }
        } else {
            itemsHtml.append("<tr><td colspan='4' style='text-align: center;'>Không có sản phẩm nào trong đơn hàng.</td></tr>");
        }

        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 16px; margin: 0; }" +
                ".container { background-color: #ffffff; padding: 20px; max-width: 600px; margin: 0 auto; }" +
                ".header { text-align: center; margin-bottom: 16px; }" +
                ".header h1 { font-size: 24px; font-weight: bold; color: #333; }" +
                ".transaction-id { text-align: right; font-size: 14px; font-style: italic; color: #666; margin-bottom: 8px; }" +
                ".cancel-reason { font-size: 14px; font-style: italic; color: #ff0000; margin-bottom: 8px; display: " + (donHang.getLyDo() != null ? "block" : "none") + "; }" +
                ".info-table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }" +
                ".info-table td { padding: 4px; font-size: 14px; }" +
                ".info-label { color: #666; }" +
                ".info-value { font-weight: bold; text-align: right; }" +
                ".divider { height: 1px; background-color: #ccc; margin: 16px 0; }" +
                ".items-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }" +
                ".items-table th, .items-table td { border: 1px solid #ccc; padding: 8px; text-align: left; font-size: 14px; }" +
                ".items-table th { background-color: #f0f0f0; font-weight: bold; }" +
                ".total-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }" +
                ".total-table td { padding: 4px; font-size: 16px; font-weight: bold; }" +
                ".footer { font-size: 12px; color: #666; margin-top: 16px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='transaction-id'>#" + donHang.getMaDH() + "</div>" +
                "<div class='cancel-reason'>" + (donHang.getLyDo() != null ? "* ĐÃ HỦY: " + donHang.getLyDo() : "") + "</div>" +
                "<div class='header'>" +
                "<h1>Đơn hàng của bạn đã được xác nhận!</h1>" +
                "</div>" +
                "<table class='info-table'>" +
                "<tr>" +
                "<td class='info-label'>Ngày đặt hàng</td>" +
                "<td class='info-value'>" + donHang.getNgayTao() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class='info-label'>Ngày xác nhận</td>" +
                "<td class='info-value'>" + donHang.getNgayXacNhan() + "</td>" +
                "</tr>" +
                "</table>" +
                "<div class='divider'></div>" +
                "<table class='info-table'>" +
                "<tr>" +
                "<td class='info-label'>Tên khách hàng</td>" +
                "<td class='info-value'>" + binding.txtCus.getText().toString() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class='info-label'>Phương thức nhận hàng</td>" +
                "<td class='info-value'>" + donHang.getPtNhanHang() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class='info-label'>Phương thức thanh toán</td>" +
                "<td class='info-value'>" + (donHang.getCongThanhToan() == null ? donHang.getPtThanhToan() : donHang.getPtThanhToan() + " | " + donHang.getCongThanhToan()) + "</td>" +
                "</tr>" +
                "</table>" +
                "<div class='divider'></div>" +
                "<table class='items-table'>" +
                "<thead>" +
                "<tr>" +
                "<th>Hình ảnh</th>" +
                "<th>Sản phẩm</th>" +
                "<th>Số lượng</th>" +
                "<th>Giá</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                itemsHtml.toString() +
                "</tbody>" +
                "</table>" +
                "<div class='divider'></div>" +
                "<table class='total-table'>" +
                "<tr>" +
                "<td>TỔNG CỘNG</td>" +
                "<td style='text-align: right;'>" + Format.formatVND(donHang.getTongCong()) + "</td>" +
                "</tr>" +
                "</table>" +
                "<div class='footer'>" +
                "Liên hệ với chúng tôi: foodsellingapp@gmail.com | +84 796 989 108" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public void showDeleteConfirmDialog(DonHang donHang) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button cancelOrder = dialogView.findViewById(R.id.cancelButton);
        Button deleteOrder = dialogView.findViewById(R.id.deleteButton);

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
                Toast.makeText(this, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show();
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
        docRef.update(hashMap).addOnSuccessListener(unused -> {
            Toast.makeText(OrderDetailActivity.this, "Hủy đơn thành công!", Toast.LENGTH_SHORT).show();
            binding.txtLyDo.setText("* ĐÃ HỦY: " + deleteReason);
            binding.txtLyDo.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Toast.makeText(OrderDetailActivity.this, "Lỗi khi hủy đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getTenKH() {
        DocumentReference documentReference = firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                binding.txtCus.setText(task.getResult().getString("name"));
                customerEmail = task.getResult().getString("email");
                Log.d("EmailDebug", "Customer email loaded: " + customerEmail);

                // Kích hoạt nút xác nhận nếu cả email và items đã tải
                if ("choXacNhan".equals(donHang.getTrangThai()) && customerEmail != null && !customerEmail.isEmpty() && isItemsLoaded) {
                    binding.btnXacNhanDon.setEnabled(true);
                    binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
                }
            } else {
                Log.d("EmailDebug", "Failed to load customer email");
                Toast.makeText(OrderDetailActivity.this, "Không thể lấy email khách hàng", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d("EmailDebug", "Error loading customer email: " + e.getMessage());
            Toast.makeText(OrderDetailActivity.this, "Lỗi khi lấy email khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void transferDateTime() {
        long timestampt = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestampt);
        ngayXacNhanDon = (String) DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar);
    }
}