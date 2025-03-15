package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.CategoryAdapter;
import com.example.foodsellingapp.Model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Ad_Categories extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    private Spinner spCategory;
    private RecyclerView rvFoodItems;
    private ImageView btnManageCategories, btnCheckout;
    private TextView tvMessage;
    private FirebaseFirestore db;
    private List<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_categories);
        rvFoodItems=findViewById(R.id.listCate);
        btnManageCategories=findViewById(R.id.btnManageCaterogy);
        spCategory=findViewById(R.id.spCategory);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        // Set up Spinner adapter
        categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList) {
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(categoryList.get(position).getName());
                return view;
            }

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(categoryList.get(position).getName());
                return view;
            }
        };
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
// Load categories
        loadCategories();

        // Button listeners
        btnManageCategories.setOnClickListener(v -> showCategoryManagementBottomSheet());
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            category.setCateId(document.getId()); // Explicitly set the document ID
                            categoryList.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showCategoryManagementBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_category_management, null);
        bottomSheetDialog.setContentView(sheetView);

        RecyclerView rvCategories = sheetView.findViewById(R.id.rvCategories);
        Button btnAddCategory = sheetView.findViewById(R.id.btnAddCategory);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter adapter = new CategoryAdapter(categoryList, this); // Pass 'this' as listener
        rvCategories.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v -> showAddEditCategoryBottomSheet(null, adapter));
        bottomSheetDialog.show();
    }

    private void showAddEditCategoryBottomSheet(Category category, CategoryAdapter adapter) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_category, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvTitle = sheetView.findViewById(R.id.tvTitle);
        EditText etCategoryName = sheetView.findViewById(R.id.etCategoryName);
        Button btnSave = sheetView.findViewById(R.id.btnSave);

        if (category != null) {
            tvTitle.setText("Edit Category");
            etCategoryName.setText(category.getName());
        } else {
            tvTitle.setText("Add Category");
        }

        btnSave.setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category == null) {
                Category newCategory = new Category();
                newCategory.setName(name);
                db.collection("categories")
                        .add(newCategory)
                        .addOnSuccessListener(documentReference -> {
                            newCategory.setCateId(documentReference.getId());
                            categoryList.add(newCategory);
                            adapter.updateList(categoryList);
                            loadCategories();
                            bottomSheetDialog.dismiss();
                        });
            } else {
                category.setName(name);
                db.collection("categories").document(category.getCateId())
                        .update("name", name)
                        .addOnSuccessListener(aVoid -> {
                            adapter.updateList(categoryList);
                            loadCategories();
                            bottomSheetDialog.dismiss();
                        });
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onEditClick(Category category) {
        showAddEditCategoryBottomSheet(category, (CategoryAdapter) rvFoodItems.getAdapter());
    }

    @Override
    public void onDeleteClick(Category category) {
        db.collection("categories").document(category.getCateId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    categoryList.remove(category);
                    loadCategories();
                    if (rvFoodItems.getAdapter() instanceof CategoryAdapter) {
                        ((CategoryAdapter) rvFoodItems.getAdapter()).updateList(categoryList);
                    }
                });

    }
}