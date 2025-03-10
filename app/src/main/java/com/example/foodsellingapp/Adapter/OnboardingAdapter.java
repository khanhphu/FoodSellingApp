package com.example.foodsellingapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.OnboardingItem;
import com.example.foodsellingapp.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    private static final String TAG = "OnboardingAdapter";
    private List<OnboardingItem> onboardingItems;
    private int[] collageImageResources = {
            R.drawable.imgmon1, R.drawable.imgmon2,
            R.drawable.imgmon1, R.drawable.imgmon2,
            R.drawable.imgmon2, R.drawable.imgmon1
    };

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
        Log.d(TAG, "Adapter initialized with " + (onboardingItems != null ? onboardingItems.size() : "null") + " items");
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating ViewHolder for viewType: " + viewType);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_onboarding_slide, parent, false);
        Log.d(TAG, "View inflated: " + (view != null));
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        Log.d(TAG, "Binding ViewHolder at position: " + position);
        if (onboardingItems == null || position >= onboardingItems.size()) {
            Log.e(TAG, "Invalid position or null items: " + position);
            return;
        }
        OnboardingItem item = onboardingItems.get(position);
        if (item == null) {
            Log.e(TAG, "OnboardingItem is null at position: " + position);
            return;
        }
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        int count = onboardingItems != null ? onboardingItems.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImage;
        ImageView collageImage1, collageImage2, collageImage3, collageImage4, collageImage5, collageImage6;
        LinearLayout imageCollage;
        TextView title;
        TextView description;


        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "Initializing ViewHolder");
            logoImage = itemView.findViewById(R.id.logoImage);
            collageImage1 = itemView.findViewById(R.id.collageImage1);
            collageImage2 = itemView.findViewById(R.id.collageImage2);
            collageImage3 = itemView.findViewById(R.id.collageImage3);
            collageImage4 = itemView.findViewById(R.id.collageImage4);
            collageImage5 = itemView.findViewById(R.id.collageImage5);
            collageImage6 = itemView.findViewById(R.id.collageImage6);
            imageCollage = itemView.findViewById(R.id.imageCollage);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);

            if (logoImage == null) Log.e(TAG, "logoImage not found in layout");
            if (imageCollage == null) Log.e(TAG, "imageCollage not found in layout");
            if (collageImage1 == null) Log.e(TAG, "collageImage1 not found in layout");
            if (title == null) Log.e(TAG, "title not found in layout");
            if (description == null) Log.e(TAG, "description not found in layout");
        }

        void bind(OnboardingItem item, int position) {
            Log.d(TAG, "Binding item at position: " + position + ", title: " + item.getTitle());
            if (title != null) {
                title.setText(item.getTitle());
            } else {
                Log.e(TAG, "title TextView is null");
            }
            if (description != null) {
                description.setText(item.getDescription());
            } else {
                Log.e(TAG, "description TextView is null");
            }

            if (position == 0) { // First slide (Splash)
                if (logoImage != null && imageCollage != null) {
                    logoImage.setVisibility(View.VISIBLE);
                    imageCollage.setVisibility(View.GONE);
                    try {
                        int imageResId = item.getImageRes();
                        logoImage.setImageResource(imageResId);
                        Log.d(TAG, "Logo image set to: " + imageResId);
                        // Verify visibility and drawable
                        logoImage.post(() -> {
                            Log.d(TAG, "Logo visibility after binding: " + (logoImage.getVisibility() == View.VISIBLE));
                            Log.d(TAG, "Logo has drawable: " + (logoImage.getDrawable() != null));
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to set logo image: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "logoImage or imageCollage is null during binding for position 0");
                }
            } else if (position == 1) { // Second slide (Collage)
                if (logoImage != null && imageCollage != null) {
                    logoImage.setVisibility(View.GONE);
                    imageCollage.setVisibility(View.VISIBLE);
                    try {
                        if (collageImage1 != null) collageImage1.setImageResource(collageImageResources[0]);
                        if (collageImage2 != null) collageImage2.setImageResource(collageImageResources[1]);
                        if (collageImage3 != null) collageImage3.setImageResource(collageImageResources[2]);
                        if (collageImage4 != null) collageImage4.setImageResource(collageImageResources[3]);
                        if (collageImage5 != null) collageImage5.setImageResource(collageImageResources[4]);
                        if (collageImage6 != null) collageImage6.setImageResource(collageImageResources[5]);
                        Log.d(TAG, "Collage images set");
                        // Verify visibility and drawables
                        imageCollage.post(() -> {
                            Log.d(TAG, "Collage visibility after binding: " + (imageCollage.getVisibility() == View.VISIBLE));
                            Log.d(TAG, "CollageImage1 has drawable: " + (collageImage1.getDrawable() != null));
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to set collage images: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "logoImage or imageCollage is null during binding for position 1");
                }
            } else if (position == 2) { // Third slide (Culinary Delights)
                if (logoImage != null && imageCollage != null) {
                    logoImage.setVisibility(View.GONE);
                    imageCollage.setVisibility(View.GONE);

                    Log.d(TAG, "Third slide, images hidden");
                } else {
                    Log.e(TAG, "logoImage or imageCollage is null during binding for position 2");
                }
            }
        }
    }
}