package com.example.foodsellingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.foodsellingapp.Adapter.Ad_AdapterMonAn;
import com.example.foodsellingapp.Adapter.BannerAdapter;
import com.example.foodsellingapp.Adapter.CategoryAdapter;
import com.example.foodsellingapp.Adapter.User_AdpaterMonAn;
import com.example.foodsellingapp.Model.Banner;
import com.example.foodsellingapp.Model.Category;
import com.example.foodsellingapp.Model.MonAn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class User_Home extends Fragment {
    private ViewFlipper viewFlipper;
    private ArrayList<MonAn> arrmon;
    private User_AdpaterMonAn adpaterMonAn;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categoryList;
    private RecyclerView categoryView;
    private BannerAdapter bannerAdapter;
    private ArrayList<Banner> bannerList;
    private RecyclerView bannerView;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;


    RecyclerView menuMon;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user__home, container, false);

        // Menu RecyclerView
        menuMon = view.findViewById(R.id.recommendedRecyclerView);
        menuMon.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Categories RecyclerView
        categoryView = view.findViewById(R.id.categoriesRecyclerView);
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Gà"));
        categoryList.add(new Category("Combo"));
        categoryList.add(new Category("Nước uống"));
        categoryList.add(new Category("Tráng miệng"));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList);
        categoryView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryView.setAdapter(categoryAdapter);

        // Banner ViewPager2
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        bannerList = new ArrayList<>();
        bannerList.add(new Banner("", "Get your 20% daily discount now!", "Order now"));
        bannerList.add(new Banner("", "Special Offer Today!", "Order now"));
        BannerAdapter bannerAdapter = new BannerAdapter(requireContext(), bannerList, bannerViewPager);
        bannerViewPager.setAdapter(bannerAdapter);
        new TabLayoutMediator(bannerIndicator, bannerViewPager, (tab, position) -> {}).attach();

        loadMon();
        return view;
    }
    private void loadMon() {
        //init arraylist
        arrmon = new ArrayList<>();
        //
        firestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firestore.collection("MonAn");
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrmon.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        Toast.makeText(getContext(), "Documents found: " + list.size(), Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot documentSnapshot : list) {
                            MonAn model = documentSnapshot.toObject(MonAn.class);
                            arrmon.add(model);
                        }
                        //set adapter
                        adpaterMonAn = new User_AdpaterMonAn(getActivity(), arrmon);
                        //set adapter for recycler view
                        menuMon.setAdapter(adpaterMonAn);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        menuMon.setLayoutManager(layoutManager);
                        adpaterMonAn.notifyDataSetChanged();
                        //Toast.makeText(getActivity(), "Show data!", Toast.LENGTH_LONG).show();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Cannot show data!", Toast.LENGTH_LONG).show();
                    }
                });

    }
}