package com.example.foodsellingapp;

import android.content.Intent;
import android.icu.util.EthiopicCalendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterGioHang;
import com.example.foodsellingapp.Model.MonAn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class User_ShoppingCartFragment extends Fragment {
private  ManagementCart managementCart;
private TextView sumSL,subTotal,khuyenMai,total;
private RecyclerView listGioHang;
private Button btnCheckout;
ArrayList<MonAn> arrayList=new ArrayList<>();
User_AdapterGioHang userAdapterGioHang;
FirebaseFirestore firestore=FirebaseFirestore.getInstance();
FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user__shopping_cart,container,false);
        //

        listGioHang=view.findViewById(R.id.listGioHang);
        sumSL=view.findViewById(R.id.sumSL);
        subTotal=view.findViewById(R.id.subTotal);
        khuyenMai=view.findViewById(R.id.khuyenmai);
        total=view.findViewById(R.id.total);
        btnCheckout=view.findViewById(R.id.btnCheckout);
        managementCart=new ManagementCart(getActivity());

if(checkUser()==true) {
    initCart();
    calculate();
    arrayList = managementCart.getListCart();
    btnCheckout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (arrayList.size() == 0) {
                Toast.makeText(getActivity(), "Empty!", Toast.LENGTH_SHORT).show();

            } else {
                setUpOrder();
                //them don hang thi phai xoa khoi gio hang
                managementCart.clearAll();
                initCart();
                clearAllText();
                Intent intent = new Intent(getActivity(), User_DonHang.class);
                intent.putExtra("maDH", maDH);
                startActivity(intent);
            }
        }
    });
}
        return view;
    }
    public boolean checkUser(){
        if(firebaseAuth.getUid()==null)
            return false;
        return true;
    }

    private void clearAllText() {
        sumSL.setText("");
        subTotal.setText("");
        total.setText("");
    }

    private void setUpOrder() {
        //tao don hang truoc (document)
        createOrder();
        for(int i=0;i<arrayList.size();i++){
            createDetailOrder(i);
        }
    }
String maDH;
    private void createOrder() {
        Long timestamp = System.currentTimeMillis();
        String maDH = timestamp.toString();
        this.maDH=maDH;
        String uid = firebaseAuth.getCurrentUser().getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        int tongsl = managementCart.totalMon();
        Long tong = Long.valueOf(managementCart.getTotalFee());
        hashMap.put("tongCong", this.tamTinh);
        hashMap.put("tongSL", tongsl);
        hashMap.put("maKH", uid);
        hashMap.put("maDH", maDH);
        hashMap.put("trangThai","choXacNhan");
        // Replace with your timestamp
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String ngayTao= (String) DateFormat.format("dd/MM/yyyy",calendar);
        hashMap.put("ngayTao", ngayTao);
        CollectionReference ref = firestore.collection("DonHang");

        DocumentReference docref = ref.document(maDH);
        docref.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void initCart() {
        if(managementCart.getListCart().isEmpty()){
            listGioHang.setVisibility(View.GONE);
            Toast.makeText(getActivity(),"Empty!",Toast.LENGTH_SHORT).show();

        }
        else {
            listGioHang.setVisibility(View.VISIBLE);

            Toast.makeText(getActivity()," Not Empty!",Toast.LENGTH_SHORT).show();

        }


      listGioHang.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listGioHang.setAdapter(new User_AdapterGioHang(managementCart,managementCart.getListCart(), new ChangeNumberItemsListerner() {
            @Override
        public void change() {
               // Toast.makeText(getActivity(),listGioHang.getSourceLayoutResId(R.id.))
            calculate();
        }
    }));
    }
    long tamTinh;
    private void calculate() {
     sumSL.setText( ""+ managementCart.totalMon());
      this.tamTinh=  managementCart.getTotalFee();
     subTotal.setText(String.valueOf(tamTinh));
     total.setText("$"+subTotal.getText().toString());
    }
    private void createDetailOrder(int i) {

        String maCTDH = "CT"+this.maDH;
        HashMap<String, Object> hashMapCT = new HashMap<>();
        hashMapCT.put("maMon", arrayList.get(i).getMaMon());
        hashMapCT.put("tenMon", arrayList.get(i).getTenMon());
        hashMapCT.put("gia", arrayList.get(i).getGia());
        hashMapCT.put("sl",arrayList.get(i).getNumberinCart());
        hashMapCT.put("khuyeMai", "");
        CollectionReference reference=firestore.collection("DonHang");
        DocumentReference doc= reference.document(maDH);
        CollectionReference subColl = doc.collection(maCTDH);

        subColl.add(hashMapCT).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getActivity(), "Create new order successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}