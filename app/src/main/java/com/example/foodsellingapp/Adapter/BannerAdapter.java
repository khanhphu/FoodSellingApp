package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodsellingapp.Model.Banner;
import com.example.foodsellingapp.R;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private ArrayList<Banner> bannerArrayList;
    private ViewPager2 viewPager2;

    public BannerAdapter(Context context, ArrayList<Banner> bannerArrayList, ViewPager2 viewPager2) {
        this.context = context;
        this.bannerArrayList = bannerArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_banner,parent,false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            Banner banner=bannerArrayList.get(position);
            holder.title.setText(banner.getTitle());
            holder.actionButton.setText(banner.getAction());

        //    holder.image.setImageResource(R.drawable.banner4);
    }

    @Override
    public int getItemCount() {
        return bannerArrayList.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
     //   ImageView image;
        TextView title;
        Button actionButton;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
          //  image = itemView.findViewById(R.id.bannerImage);
            title = itemView.findViewById(R.id.bannerTitle);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}
