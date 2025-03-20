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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.OrderDetail_Adapter;
import com.example.foodsellingapp.Adapter.User_AdapterDSDonHang;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityAdMainPageBinding;
import com.example.foodsellingapp.databinding.ActivityOnboardingBinding;
import com.example.foodsellingapp.databinding.ActivityOrderDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    private ActivityOrderDetailBinding binding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DonHang donHang;
    private String ngayXacNhanDon;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String deleteReason, imgItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get intent and object from UserAdapter Dsdonhang
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
            if(donHang.getLyDo()!=null){
                binding.txtLyDo.setText("* ĐÃ HỦY: "+donHang.getLyDo());
                binding.txtLyDo.setVisibility(View.VISIBLE);
            }
        }
        // Hide buttons if the logged-in user is the order owner
        if (firebaseUser.getUid().equals(donHang.getMaKH())) {
            binding.btnXacNhanDon.setVisibility(View.GONE);
            binding.btnHuyDon.setVisibility(View.GONE);
        }
        else{
            // Show confirm button only for "choXacNhan" status
            if ("choXacNhan".equals(donHang.getTrangThai())) {
                binding.btnXacNhanDon.setVisibility(View.VISIBLE);
                binding.btnHuyDon.setVisibility(View.VISIBLE);
                binding.btnXacNhanDon.setOnClickListener(v -> confirmOrder());
            }

            // Always allow admin to cancel (if not hidden)
            binding.btnHuyDon.setOnClickListener(v -> showDeleteConfirmDialog(donHang));
        }

        binding.btnHuyDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmDialog(donHang);

            }
        });
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

                    for (DocumentSnapshot itemDoc : querySnapshot) {
                        if (itemDoc.exists()) {
                            // Chuyển double sang int cho gia, đảm bảo không mất dữ liệu quan trọng
                            double giaDouble = itemDoc.getDouble("gia") != null ? itemDoc.getDouble("gia") : 0.0;
                            int gia = (int) Math.round(giaDouble); // Làm tròn để chuyển sang int
                            int sl = itemDoc.getLong("sl") != null ? itemDoc.getLong("sl").intValue() : 0;
                             getImageForItems(itemDoc.getString("maMon"));
                            CTDonHang item = new CTDonHang(
                                    maDH, // maDH từ tham số
                                    gia, // Giá (int)
                                    itemDoc.getString("khuyenMai"), // Khuyến mãi
                                    sl, // Số lượng (int)
                                    itemDoc.getString("tenMon"), // Tên món
                                    itemDoc.getString("maMon"), // Mã món
                                    imgItem
                            );

                            allItems.add(item);
                        }
                    }

                    // Gán danh sách sản phẩm vào donHang
                    donHang.setItems(allItems);

                    // Hiển thị danh sách sản phẩm trong RecyclerView
                      binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
                      OrderDetail_Adapter orderDetailAdapter = new OrderDetail_Adapter(this, donHang.getItems());
                    binding.rvItems.setAdapter(orderDetailAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading items: " + e.getMessage(), Toast.LENGTH_SHORT).show());    }

    private void  getImageForItems(String maMon) {
        firestore.collection("MonAn").document(maMon)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            imgItem=documentSnapshot.getString("url");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,"Error to fetch image item"+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmOrder() {
        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "XacNhan");
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(OrderDetailActivity.this, "Xác nhận đơn!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });

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
        cancelOrder.setOnClickListener(v -> {
            dialog.dismiss();
        });
        deleteOrder.setOnClickListener(v -> {
            dialog.dismiss();
            showResonBottomSheet(donHang);
        });
        // Customize dialog appearance
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
            if (!edtReason.getText().toString().isEmpty()) {
                updateDeleteOrder(deleteReason, donHang);
                bottomSheetDialog.dismiss();
            } else {

            }
        });
        bottomSheetDialog.show();

    }

    public void updateDeleteOrder(String deleteReason, DonHang donHang) {
        //ngay huy
        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "Huy");
        //reason
        hashMap.put("lyDo", deleteReason);
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(OrderDetailActivity.this, "Hủy đơn!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        //show ly do huy text
        binding.txtLyDo.setText("* ĐÃ HỦY: "+donHang.getLyDo());
        binding.txtLyDo.setVisibility(View.VISIBLE);
    }

    private void getTenKH() {

        DocumentReference documentReference = firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                binding.txtCus.setText(task.getResult().getString("name"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void transferDateTime() {
        //xu ly ngay gio
        long timestampt = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestampt);
        ngayXacNhanDon = (String) DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar);
    }
}