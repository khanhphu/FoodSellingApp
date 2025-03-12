package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Format;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.TimKiemMonAn;
import com.example.foodsellingapp.User_ChiTietMon;
import com.example.foodsellingapp.databinding.ListUserMonBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class User_AdpaterMonAn extends RecyclerView.Adapter<User_AdpaterMonAn.HolderUserMon> {
    private Context context;
    public ArrayList<MonAn> arrMon, filterlist;
    private ListUserMonBinding binding;
    private TimKiemMonAn timKiemMonAn;
    @NonNull
    @Override
    public HolderUserMon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ListUserMonBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderUserMon(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserMon holder, int position) {
        MonAn monAn = arrMon.get(position);
        //ma mon ko show ra nhung van khai bao de intent qua detail mon hoac cac chuc nang khac
        String maMon = monAn.getMaMon();
        String tenMon = monAn.getTenMon();
        Integer gia = monAn.getGia();
        Integer phuThu = monAn.getPhuThu();
        //update them sl:
        Integer sl = monAn.getSoLuong();
        Integer giaBan = gia + phuThu;
        String url = monAn.getUrl();
        //get hinh
        if(sl==0){
            holder.menuHinhMon.setImageResource(R.drawable.out_of_stock);
        }
        else
        { Picasso.get().load(url).into(holder.menuHinhMon);}


        //get data
        holder.menuTenMon.setText(tenMon);
        //gui tu ma mon tu ma mon truy van cac thong tin mon


        holder.menuGia.setText(Format.formatVND(giaBan));
        //for Monan chi tiet


        // nhan vao se gui intent den chi tiet
       if(sl>0) {
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, User_ChiTietMon.class);
                   intent.putExtra("object", arrMon.get(position));
                   //     intent.putExtra("maMon",maMon);
                   context.startActivity(intent);

               }
           });

       }

    }

    @Override
    public int getItemCount() {
        return arrMon.size();
    }
    //tao contrustor

    public User_AdpaterMonAn(Context context, ArrayList<MonAn> arrMon) {
        this.context = context;
        this.arrMon = arrMon;
        this.filterlist=arrMon;
    }
public Filter getFilter(){
    if(timKiemMonAn==null){
        timKiemMonAn=new TimKiemMonAn(filterlist,this);
    }
    return  timKiemMonAn;
}

    class HolderUserMon  extends RecyclerView.ViewHolder{
     ImageView menuHinhMon;
     //CardView itemView;
     TextView menuTenMon, menuMaMon, menuGia;

        public HolderUserMon(@NonNull View itemView) {
            super(itemView);
            menuHinhMon=binding.menuHinhMon;
            menuTenMon=binding.menuTen;
         //   menuMaMon=binding.menuMaMon;
            menuGia=binding.menuGia;
          //  itemView=binding.itemView;
        }
    }
}
