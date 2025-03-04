package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Ad_ChinhSuaMon;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.databinding.ListAdminMonBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Ad_AdapterMonAn extends RecyclerView.Adapter<Ad_AdapterMonAn.HolderAdMonAn> {
    private Context context;
    private ArrayList<MonAn> arrMonAn;
    //tao constructor :
  //view binding for list_admin_mon.xml
    private ListAdminMonBinding binding;
    public Ad_AdapterMonAn(Context context, ArrayList<MonAn> arrMonAn) {
        this.context = context;
        this.arrMonAn = arrMonAn;
    }

    @NonNull
    @Override
    public HolderAdMonAn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //binding view
        binding=ListAdminMonBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderAdMonAn(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdMonAn holder, int position) {
        //Xu ly du lieu

        //get data from model
        MonAn model=arrMonAn.get(position);
        String maMon=model.getMaMon();
        String tenMon=model.getTenMon();
        Integer gia=model.getGia();
        Integer phuThu=model.getPhuThu();
        Integer sl=model.getSoLuong();
        String gioiThieu= model.getGioiThieu();
        //duoc se them timestamp de xu ly ngay gio tao moi

        //set data for recycler view
        holder.txtMaMon.setText(maMon);
        holder.txtTenMon.setText(tenMon);
        holder.txtGia.setText(gia.toString());
        holder.txtPhuThu.setText(phuThu.toString());
//        holder.txtGioiThieu.setTextSize(4);
//        holder.txtGioiThieu.setText(gioiThieu.toString());
        holder.txtSL.setText(sl.toString());
        //Get Image:
        String url=model.getUrl();
        if(sl==0){
           holder.imgMon.setImageResource(R.drawable.out_of_stock);
        }
        else
        { Picasso.get().load(url).into(holder.imgMon);}
  // edit MonAN
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get intent
                Intent intent=new Intent(context, Ad_ChinhSuaMon.class);
                intent.putExtra("maMon",maMon);
                intent.putExtra("tenMon",tenMon);
                ///////////////////////// //neu la so put extra phai nhu nay:
                String st_gia= String.valueOf(gia);
                String st_phuThu= String.valueOf(phuThu);
                String st_sl=String.valueOf(sl);
                intent.putExtra("gia",st_gia);
                intent.putExtra("phuThu",st_phuThu);
                intent.putExtra("url",url);
                intent.putExtra("soLuong",st_sl);
                context.startActivity(intent);
            }
        });
 //chinh sua mon an
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                //lay ma khoa tu model chuyen khoa
                String maMon=model.getMaMon();
                CollectionReference collectionRef = db.collection("MonAn");
                DocumentReference docRef = collectionRef.document(maMon);
                docRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                               // Log.d("CKTAG", "Mon an da xoa !");
                                Toast.makeText(context,"Xóa món ăn thành công!",Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("CKTAG", "Error deleting document", e);
                                Toast.makeText(context,"Lỗi khi xóa monan !",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrMonAn.size();
    }

    class HolderAdMonAn extends RecyclerView.ViewHolder{
        //khai bao cac item co trong list_admin_mon.xml
        ImageView imgMon;
        TextView txtMaMon, txtTenMon,txtGia,txtPhuThu, txtGioiThieu;
        //cai nay se truy van sau
        TextView txtSL;
        ImageView btnEdit, btnDelete;
        public HolderAdMonAn(@NonNull View itemView) {
            super(itemView);
            imgMon=binding.imgMon;
            txtMaMon=binding.txtMaMon;
            txtTenMon=binding.txtTenMon;
            txtGia=binding.txtGia;
            txtPhuThu=binding.txtPhuThu;
            btnEdit=binding.btnEdit;
            btnDelete=binding.btnDelete;
            txtGioiThieu=binding.txtGioiThieu;
            txtSL=binding.txtSL;
            //truy van sau

        }
    }
}
