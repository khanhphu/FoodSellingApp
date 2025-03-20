package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Format;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetail_Adapter extends RecyclerView.Adapter<OrderDetail_Adapter.ViewOrderDetail> {
    private Context context;
    private List<CTDonHang> ctDonHangList;
private FirebaseFirestore firestore=FirebaseFirestore.getInstance();

    public OrderDetail_Adapter(Context context, List<CTDonHang> ctDonHangList) {
        this.context = context;
        this.ctDonHangList = ctDonHangList;
    }

    @NonNull
    @Override
    public ViewOrderDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_orderitems, parent, false);
        return new ViewOrderDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOrderDetail holder, int position) {
        CTDonHang ctDonHang=ctDonHangList.get(position);
        holder.txtCTTenMon.setText(ctDonHang.getTenMon());
        holder.txtQuantity.setText(String.valueOf(ctDonHang.getSl()));
        holder.txtPrice.setText(Format.formatVND(ctDonHang.getGia()));
        fetchImageMon(holder.imgItem,ctDonHang);

    }

    private void fetchImageMon(ImageView imgItem, CTDonHang ctDonHang) {
        DocumentReference documentReference = firestore.collection("MonAn").document(ctDonHang.getMaMon());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if(documentSnapshot.exists()){
                            String url=documentSnapshot.getString("url");
                            Picasso.get().load(url).into(imgItem);

                        }
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ctDonHangList.size();
    }

    class ViewOrderDetail extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView txtCTTenMon,txtQuantity,txtPrice,txtNote;
        public ViewOrderDetail(@NonNull View itemView) {
            super(itemView);
            imgItem=itemView.findViewById(R.id.imgItem);
            txtCTTenMon=itemView.findViewById(R.id.txtCTTenMon);
            txtQuantity=itemView.findViewById(R.id.txtQuantity);
            txtPrice=itemView.findViewById(R.id.txtPrice);
            txtNote=itemView.findViewById(R.id.txtNote);
        }
    }
}
