package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterDonHang;
import com.example.foodsellingapp.Api.CreateOrder;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.databinding.ActivityUserDonHangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    String maDH;
    private ArrayList<CTDonHang> arrDH;
    private User_AdapterDonHang adapterDonHang;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private LinearLayout bottomSheetPayment; // Changed to LinearLayout
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private RadioButton rdPayPal, rdMoMo, rdVisa, rdZaloPay;
    private Button btnPay;
    private String pay,  giaoHang;
    //to-do: for update firestore ptThanhToan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //important for ZaloPay
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        // Get intent
        String maDon = getIntent().getStringExtra("maDH");
        if (maDon == null) {
            binding.secEmpty.setVisibility(View.VISIBLE);
            binding.secOrder.setVisibility(View.GONE);
        } else {
            binding.secOrder.setVisibility(View.VISIBLE);
            binding.secEmpty.setVisibility(View.GONE);
            binding.btnHome.setVisibility(View.GONE);
            this.maDH = getIntent().getStringExtra("maDH");
            Toast.makeText(User_DonHang.this, maDH, Toast.LENGTH_SHORT).show();
            // Load order details
            loadCTDH();
            updateDH();
            handleRadioButton();
        }

        // Set up navigation buttons
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

        // Handle bottom sheet payment options
        // Inflate the bottom sheet layout
        bottomSheetPayment = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.bottom_sheet_paymentoptions, binding.coordinatorLayout, false);
        binding.coordinatorLayout.addView(bottomSheetPayment); // Add the bottom sheet to the CoordinatorLayout

        // Initialize BottomSheetBehavior and set initial state to hidden
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetPayment);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); // Hide the bottom sheet initially

        // Initialize bottom sheet views
        rdPayPal = bottomSheetPayment.findViewById(R.id.rbPayPal);
        rdMoMo = bottomSheetPayment.findViewById(R.id.rbMomo);
        rdVisa = bottomSheetPayment.findViewById(R.id.rbVisa);
        rdZaloPay = bottomSheetPayment.findViewById(R.id.rbZaloPay);
        btnPay = bottomSheetPayment.findViewById(R.id.btnPay);

        // Set PayPal as the default selection
        rdPayPal.setChecked(true);

        // Ensure single selection for radio buttons
        rdPayPal.setOnClickListener(v -> unCheckedOrthers(rdPayPal));
        rdMoMo.setOnClickListener(v -> unCheckedOrthers(rdMoMo));
        rdVisa.setOnClickListener(v -> unCheckedOrthers(rdVisa));
        rdZaloPay.setOnClickListener(v -> unCheckedOrthers(rdZaloPay));

        // Set up radio button listener for "Chuyển khoản"
        binding.grRdtPay.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdtCK) {
                pay="Chuyển khoản";
                // Show the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                // Hide the bottom sheet and reset the selected payment text
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                binding.txtSelectedPayment.setVisibility(View.GONE);
                binding.txtSelectedPayment.setText("");
            }
        });

        // Handle Pay button click in the bottom sheet
        btnPay.setOnClickListener(v -> {
            // Ensure a payment method is selected (default to PayPal if none selected)
            String selectedMethod = getSelectedPaymentMethod();
            if (selectedMethod.equals("Unknown")) {
                // If no method is selected, auto-select PayPal
                rdPayPal.setChecked(true);
                selectedMethod = "PayPal";
            }

            // Update the txtSelectedPayment TextView
            binding.txtSelectedPayment.setText(selectedMethod);
            binding.txtSelectedPayment.setVisibility(View.VISIBLE);

            // Hide the bottom sheet
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        // Delete donhang button
        binding.btnDeleteDH.setOnClickListener(v -> deleteDH());

        // Set up Save button
        binding.btnSaveDH.setOnClickListener(v -> {
            if (validateForm()) {
                String phone = binding.txtSDT.getText().toString().trim();
                String delivery = binding.grRdt.getCheckedRadioButtonId() == R.id.rdtGiao ? "Giao hàng" : "Đến lấy";
                String payment = binding.grRdtPay.getCheckedRadioButtonId() == R.id.rdtTM ? "Tiền mặt" : "Chuyển khoản";
                Toast.makeText(this, "Order Saved\nPhone: " + phone + "\nDelivery: " + delivery + "\nPayment: " + payment, Toast.LENGTH_LONG).show();
                saveDH();

                CreateOrder orderApi = new CreateOrder();


                try {
                    JSONObject data = orderApi.createOrder(binding.txtTongDH.getText().toString());
                    Log.d("Amount", binding.txtTongDH.getText().toString());
                    String code = data.getString("return_code");
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        String txtToken = data.getString("zp_trans_token");
                        Intent intent=new Intent(this,PaymentConfirmation.class);
                        intent.putExtra("maDH",maDH);
                        ZaloPaySDK.getInstance().payOrder(User_DonHang.this, txtToken, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                intent.putExtra("result","Thanh toán thành công!");
                                intent.putExtra("trangThaiThanhToan","1");

                                startActivity(intent);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        });
// xu ly

    }
    //khi thanh toan xong zalopay tra nguoc man hinh thanh toan thnah cong- (layout suceefull payment) - luu don hang
    // voi trang thai thanh toan: 1- thanh toan thanh cong
    // 0- thanh toan ko thanh cong

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
        if (rdPayPal.isChecked())
        {
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_paypal);
            return "PayPal";
        }
        else if (rdVisa.isChecked()){
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_visa);
            return "Visa";
        }
        else if (rdMoMo.isChecked()){
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_momo);

            return "MOMO";
        }
        else if (rdZaloPay.isChecked()){
            binding.imgSelectedPayment.setImageResource(R.drawable.ic_zalopay);

            return "ZaloPay";
        }
        return "Unknown";
    }

    private boolean validateForm() {
        boolean isValid = true;

        String phone = binding.txtSDT.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            binding.txtSDT.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            binding.txtSDT.setError("Phone number must be at least 10 digits");
            isValid = false;
        } else {
            binding.txtSDT.setError(null);
        }

        if (binding.grRdt.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a delivery option", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (binding.grRdtPay.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a payment option", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void updateTabStyles(MaterialButton button, boolean isSelected) {
        button.setTextColor(isSelected ? getResources().getColor(R.color.cam) : getResources().getColor(android.R.color.black));
        button.setIconTintResource(isSelected ? R.color.cam : android.R.color.black);
    }

    private void deleteDH() {
        CollectionReference collectionRef = firestore.collection("DonHang");
        DocumentReference docRef = collectionRef.document(maDH);
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
        String congThanhToan =binding.txtSelectedPayment.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lienHe", sdt);
        hashMap.put("ptThanhToan",pay);
        hashMap.put("ptNhanHang", giaoHang);
        hashMap.put("congThanhToan",congThanhToan ); // Updated to get text
        hashMap.put("trangThaiThanhToan", 0); // 0 = pending payment
        DocumentReference docRef = firestore.collection("DonHang").document(maDH);
        docRef.update(hashMap).addOnSuccessListener(unused -> {
            Toast.makeText(User_DonHang.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
            binding.secOrder.setVisibility(View.GONE);
            binding.secEmpty.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Toast.makeText(User_DonHang.this, e.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void handleRadioButton() {
        // Delivery
        binding.grRdt.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rdtLay.getId()) {
                giaoHang = "Đến lấy";
            } else if (checkedId == binding.rdtGiao.getId()) {
                giaoHang = "Giao hàng";
            }
        });

        // Payment
        binding.grRdtPay.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rdtTM.getId()) {
                pay = "Tiền mặt";
            } else if (checkedId == binding.rdtCK.getId()) {
                pay = "Chuyển khoản";
            }
        });
    }

    private void updateDH() {
        CollectionReference collectionReference = firestore.collection("DonHang");
        DocumentReference documentReference = collectionReference.document(maDH);
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
        CollectionReference collectionReference = firestore.collection("Users");
        DocumentReference documentReference = collectionReference.document(uid);
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
            binding.listOrder.setLayoutManager(layoutManager); // Set LayoutManager
            adapterDonHang.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
        });
    }
}