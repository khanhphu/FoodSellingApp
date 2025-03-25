package com.example.foodsellingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodsellingapp.Adapter.User_AdapterDonHang;
import com.example.foodsellingapp.Api.CreateOrder;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityUserDonHangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class User_DonHang extends AppCompatActivity {
    private ActivityUserDonHangBinding binding;
    private String maDH;
    private ArrayList<CTDonHang> arrDH;
    private User_AdapterDonHang adapterDonHang;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout bottomSheetPayment;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private RadioButton rdPayPal, rdMoMo, rdVisa, rdZaloPay;
    private Button btnPay;
    private String pay = "Tiền mặt"; // Giá trị mặc định
    private String giaoHang = "Giao hàng"; // Giá trị mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cấu hình ZaloPay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        // Lấy intent
        String maDon = getIntent().getStringExtra("maDH");
        if (maDon == null) {
            binding.secEmpty.setVisibility(View.VISIBLE);
            binding.secOrder.setVisibility(View.GONE);
        } else {
            binding.secOrder.setVisibility(View.VISIBLE);
            binding.secEmpty.setVisibility(View.GONE);
            binding.btnHome.setVisibility(View.GONE);
            this.maDH = maDon;
            Toast.makeText(User_DonHang.this, maDH, Toast.LENGTH_SHORT).show();
            loadCTDH();
            updateDH();
            handleRadioButton();
        }

        // Thiết lập các nút điều hướng
        binding.btnDH.setOnClickListener(v -> {
            updateTabStyles(binding.btnDH, true);
            updateTabStyles(binding.btnMoreDH, false);
            updateTabStyles(binding.btnHome, false);
        });

        binding.btnMoreDH.setOnClickListener(v -> {
            Toast.makeText(this, "Navigate to Others", Toast.LENGTH_SHORT).show();
            updateTabStyles(binding.btnDH, false);
            updateTabStyles(binding.btnMoreDH, true);
            updateTabStyles(binding.btnHome, false);
            startActivity(new Intent(User_DonHang.this, User_DSDonHang.class));
        });

        binding.btnHome.setOnClickListener(v -> {
            Toast.makeText(this, "Navigate to Home", Toast.LENGTH_SHORT).show();
            updateTabStyles(binding.btnDH, false);
            updateTabStyles(binding.btnMoreDH, false);
            updateTabStyles(binding.btnHome, true);
            startActivity(new Intent(User_DonHang.this, User_MainPage.class));
        });

        // Thiết lập bottom sheet cho tùy chọn thanh toán
        bottomSheetPayment = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.bottom_sheet_paymentoptions, binding.coordinatorLayout, false);
        binding.coordinatorLayout.addView(bottomSheetPayment);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetPayment);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        rdPayPal = bottomSheetPayment.findViewById(R.id.rbPayPal);
        rdMoMo = bottomSheetPayment.findViewById(R.id.rbMomo);
        rdVisa = bottomSheetPayment.findViewById(R.id.rbVisa);
        rdZaloPay = bottomSheetPayment.findViewById(R.id.rbZaloPay);
        btnPay = bottomSheetPayment.findViewById(R.id.btnPay);

        // Chọn mặc định PayPal
        rdPayPal.setChecked(true);

        rdPayPal.setOnClickListener(v -> unCheckedOrthers(rdPayPal));
        rdMoMo.setOnClickListener(v -> unCheckedOrthers(rdMoMo));
        rdVisa.setOnClickListener(v -> unCheckedOrthers(rdVisa));
        rdZaloPay.setOnClickListener(v -> unCheckedOrthers(rdZaloPay));

        // Chọn mặc định "Tiền mặt" và "Giao hàng"
        binding.rdtTM.setChecked(true);
        binding.rdtGiao.setChecked(true);

        binding.grRdtPay.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdtCK) {
                pay = "Chuyển khoản";
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (checkedId == R.id.rdtTM) {
                pay = "Tiền mặt";
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                binding.txtSelectedPayment.setVisibility(View.GONE);
                binding.txtSelectedPayment.setText("");
            }
        });

        btnPay.setOnClickListener(v -> {
            String selectedMethod = getSelectedPaymentMethod();
            if (selectedMethod.equals("Unknown")) {
                rdPayPal.setChecked(true);
                selectedMethod = "PayPal";
            }
            binding.txtSelectedPayment.setText(selectedMethod);
            binding.txtSelectedPayment.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        // Xóa đơn hàng
        binding.btnDeleteDH.setOnClickListener(v -> deleteDH());

        // Lưu đơn hàng
        binding.btnSaveDH.setOnClickListener(v -> {
            if (validateForm()) {
                String phone = binding.txtSDT.getText().toString().trim();
                String delivery = binding.grRdt.getCheckedRadioButtonId() == R.id.rdtGiao ? "Giao hàng" : "Đến lấy";
                String payment = binding.grRdtPay.getCheckedRadioButtonId() == R.id.rdtTM ? "Tiền mặt" : "Chuyển khoản";
                Toast.makeText(this, "Order Saved\nPhone: " + phone + "\nDelivery: " + delivery + "\nPayment: " + payment, Toast.LENGTH_LONG).show();
                saveDH();

                if (payment.equals("Chuyển khoản")) {
                    CreateOrder orderApi = new CreateOrder();
                    try {
                        JSONObject data = orderApi.createOrder(binding.txtTongDH.getText().toString());
                        Log.d("Amount", binding.txtTongDH.getText().toString());
                        String code = data.getString("return_code");
                        Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                        if (code.equals("1")) {
                            String txtToken = data.getString("zp_trans_token");
                            Intent intent = new Intent(this, PaymentConfirmation.class);
                            intent.putExtra("maDH", maDH);
                            ZaloPaySDK.getInstance().payOrder(User_DonHang.this, txtToken, "demozpdk://app", new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(String s, String s1, String s2) {
                                    intent.putExtra("result", "Thanh toán thành công!");
                                    intent.putExtra("trangThaiThanhToan", "1");
                                    updatePaymentStatus("1"); // Cập nhật trạng thái thanh toán
                                    startActivity(intent);
                                }

                                @Override
                                public void onPaymentCanceled(String s, String s1) {
                                    intent.putExtra("result", "Thanh toán bị hủy!");
                                    intent.putExtra("trangThaiThanhToan", "0");
                                    updatePaymentStatus("0");
                                    startActivity(intent);
                                }

                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                    intent.putExtra("result", "Thanh toán thất bại: " + zaloPayError.toString());
                                    intent.putExtra("trangThaiThanhToan", "0");
                                    updatePaymentStatus("0");
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi thanh toán: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    updatePaymentStatus("0"); // Thanh toán tiền mặt, trạng thái mặc định là 0
                    showSuccessOrderDialog();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void unCheckedOrthers(RadioButton rdSelected) {
        rdPayPal.setChecked(rdSelected == rdPayPal);
        rdMoMo.setChecked(rdSelected == rdMoMo);
        rdZaloPay.setChecked(rdSelected == rdZaloPay);
        rdVisa.setChecked(rdSelected == rdVisa);
    }

    private String getSelectedPaymentMethod() {
        if (rdPayPal.isChecked()) {
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_paypal);
            return "PayPal";
        } else if (rdVisa.isChecked()) {
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_visa);
            return "Visa";
        } else if (rdMoMo.isChecked()) {
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_momo);
            return "MOMO";
        } else if (rdZaloPay.isChecked()) {
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_zalopay);
            return "ZaloPay";
        }
        return "Unknown";
    }

    private boolean validateForm() {
        boolean isValid = true;

        String phone = binding.txtSDT.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            binding.txtSDT.setError("Số điện thoại không được để trống");
            isValid = false;
        } else if (phone.length() < 10) {
            binding.txtSDT.setError("Số điện thoại phải có ít nhất 10 chữ số");
            isValid = false;
        } else {
            binding.txtSDT.setError(null);
        }

        if (binding.grRdt.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức nhận hàng", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (binding.grRdtPay.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void updateTabStyles(MaterialButton button, boolean isSelected) {
        button.setTextColor(isSelected ? getResources().getColor(R.color.cam) : getResources().getColor(android.R.color.black));
        button.setIconTintResource(isSelected ? R.color.cam : android.R.color.black);
    }

    private void deleteDH() {
        DocumentReference docRef = firestore.collection("DonHang").document(maDH);
        docRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(User_DonHang.this, "Xóa đơn hàng mã " + maDH + " thành công!", Toast.LENGTH_SHORT).show();
            binding.secOrder.setVisibility(View.GONE);
            binding.secEmpty.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Log.w("CKTAG", "Error deleting document", e);
            Toast.makeText(User_DonHang.this, "Lỗi khi xóa đơn!", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveDH() {
        String sdt = binding.txtSDT.getText().toString();
        String congThanhToan = binding.txtSelectedPayment.getText().toString();
        if (pay == null) {
            pay = "Tiền mặt"; // Đảm bảo pay không null
        }
        if (giaoHang == null) {
            giaoHang = "Giao hàng"; // Đảm bảo giaoHang không null
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lienHe", sdt);
        hashMap.put("ptThanhToan", pay);
        hashMap.put("ptNhanHang", giaoHang);
        hashMap.put("congThanhToan", congThanhToan.isEmpty() ? null : congThanhToan);
        hashMap.put("trangThaiThanhToan", 0); // 0 = pending payment
        hashMap.put("trangThai", "choXacNhan"); // Đặt trạng thái đơn hàng

        DocumentReference docRef = firestore.collection("DonHang").document(maDH);
        docRef.update(hashMap).addOnSuccessListener(unused -> {
            Log.d("SaveOrder", "Cập nhật đơn hàng thành công: " + maDH);
            // Không gọi showSuccessOrderDialog() ở đây vì sẽ gọi sau khi thanh toán
        }).addOnFailureListener(e -> {
            Toast.makeText(User_DonHang.this, "Lỗi khi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updatePaymentStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThaiThanhToan", Integer.parseInt(status));
        DocumentReference docRef = firestore.collection("DonHang").document(maDH);
        docRef.update(hashMap).addOnSuccessListener(unused -> {
            Log.d("PaymentStatus", "Cập nhật trạng thái thanh toán thành công: " + status);
            showSuccessOrderDialog();
        }).addOnFailureListener(e -> {
            Toast.makeText(User_DonHang.this, "Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void showSuccessOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.createorder_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button goToOrder = dialogView.findViewById(R.id.goToOrder);
        Button continueShopping = dialogView.findViewById(R.id.continueShopping);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        goToOrder.setOnClickListener(v -> {
            startActivity(new Intent(User_DonHang.this, User_DSDonHang.class));
            dialog.dismiss();
        });

        continueShopping.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(User_DonHang.this, User_Home.class));
        });

        dialog.show();
    }

    private void handleRadioButton() {
        binding.grRdt.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rdtLay.getId()) {
                giaoHang = "Đến lấy";
            } else if (checkedId == binding.rdtGiao.getId()) {
                giaoHang = "Giao hàng";
            }
        });

        binding.grRdtPay.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rdtTM.getId()) {
                pay = "Tiền mặt";
            } else if (checkedId == binding.rdtCK.getId()) {
                pay = "Chuyển khoản";
            }
        });
    }

    private void updateDH() {
        DocumentReference documentReference = firestore.collection("DonHang").document(maDH);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int tong = document.getDouble("tongCong").intValue();
                    binding.txtTongDH.setText("" + tong);
                    binding.txtTrangThai.setText("Chờ xác nhận");
                    String uid = document.getString("maKH");
                    loadUser(uid);
                }
            }
        });
    }

    private void loadUser(String uid) {
        DocumentReference documentReference = firestore.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.txtTenKHDH.setText(document.getString("name"));
                }
            }
        });
    }

    private void loadCTDH() {
        arrDH = new ArrayList<>();
        CollectionReference subReference = firestore.collection("DonHang").document(maDH).collection("CT" + maDH);
        subReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            arrDH.clear();
            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : list) {
                CTDonHang model = documentSnapshot.toObject(CTDonHang.class);
                arrDH.add(model);
            }
            adapterDonHang = new User_AdapterDonHang(User_DonHang.this, arrDH);
            binding.listOrder.setAdapter(adapterDonHang);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(User_DonHang.this, LinearLayoutManager.VERTICAL, false);
            binding.listOrder.setLayoutManager(layoutManager);
            adapterDonHang.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
        });
    }
}