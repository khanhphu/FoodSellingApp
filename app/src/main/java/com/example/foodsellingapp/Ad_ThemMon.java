package com.example.foodsellingapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodsellingapp.Model.Category;
import com.example.foodsellingapp.databinding.ActivityAdThemMonBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ad_ThemMon extends AppCompatActivity {
    private ActivityAdThemMonBinding binding;
    private String maMon, tenMon;
    private int gia, phuThu, sl;
    private Uri imgUri;
    private ImageView imgMon;
    private ProgressDialog progressDialog;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("MonAn");
    // Category
    private List<Category> categoryList;
    private ArrayAdapter<Category> categoryArrayAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdThemMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize progress dialog
        progressDialog = new ProgressDialog(Ad_ThemMon.this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        imgMon = binding.imgMon;

        // Initialize category list
        categoryList = new ArrayList<>(); // Initialize to avoid NullPointerException

        // Back button
        binding.btnback.setOnClickListener(v -> onBackPressed());

        // Save button
        binding.btnSave.setOnClickListener(v -> {
            progressDialog.setMessage("Uploading new dishes...");
            progressDialog.show();
            checkMon();
        });

        // Pick image
        binding.imgMon.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up Spinner adapter
        categoryArrayAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList) {
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
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategory.setAdapter(categoryArrayAdapter);

        // Load categories
        loadCategories();
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            category.setCateId(document.getId()); // Set the document ID
                            categoryList.add(category);
                        }
                        categoryArrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private String uploadedImage = "";

    public void uploadImage(Uri uri) {
        StorageReference fileref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnSuccessListener(downloadUri -> {
                uploadedImage = downloadUri.toString();
                loadMon4FB(uploadedImage);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            imgMon.setImageURI(imgUri);
        }
    }

    private void checkMon() {
        if (isValidGia() && isValidTenMon() && isValidImage()) {
            uploadImage(imgUri);
        } else {
            progressDialog.dismiss();
        }
    }

    private boolean isValidGia() {
        String txtGia = binding.txtGia.getText().toString().trim();
        if (txtGia.isEmpty()) {
            showToastMessage("Giá không được để trống!");
            return false;
        }
        try {
            int giaValue = Integer.parseInt(txtGia);
            if (giaValue <= 0) {
                showToastMessage("Giá phải lớn hơn 0!");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showToastMessage("Giá phải là số hợp lệ!");
            return false;
        }
    }

    private boolean isValidTenMon() {
        String txtTenMon = binding.txtTenMon.getText().toString().trim();
        if (txtTenMon.isEmpty()) {
            showToastMessage("Tên món không được để trống!");
            return false;
        }
        return true;
    }

    private boolean isValidImage() {
        if (imgUri == null) {
            showToastMessage("Vui lòng chọn hình món ăn!");
            return false;
        }
        return true;
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void loadMon4FB(String uploadedImage) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        maMon = "M" + System.currentTimeMillis();
        tenMon = binding.txtTenMon.getText().toString().trim();
        gia = Integer.parseInt(binding.txtGia.getText().toString().trim());

        // Handle phuThu
        String txtPhuThu = binding.txtPhuThu.getText().toString().trim();
        phuThu = (txtPhuThu.isEmpty() || Integer.parseInt(txtPhuThu) <= 0) ? 0 : Integer.parseInt(txtPhuThu);

        // Handle soLuong
        String txtSL = binding.txtSL.getText().toString().trim();
        sl = (txtSL.isEmpty() || Integer.parseInt(txtSL) <= 0) ? 0 : Integer.parseInt(txtSL);

        CollectionReference ref = firestore.collection("MonAn");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("maMon", maMon);
        hashMap.put("tenMon", tenMon);
        hashMap.put("gia", gia);
        hashMap.put("phuThu", phuThu);
        hashMap.put("soLuong", sl);
        hashMap.put("gioiThieu", binding.txtGioiThieu.getText().toString());
        hashMap.put("url", uploadedImage);

        // Add category reference
        Category selectedCategory = (Category) binding.spCategory.getSelectedItem();
        if (selectedCategory != null) {
//            hashMap.put("categoryId", selectedCategory.getCateId());
            hashMap.put("categoryName", selectedCategory.getName());
        } else {
            progressDialog.dismiss();
            showToastMessage("Please select a category!");
            return;
        }

        ref.document(maMon).set(hashMap).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            Toast.makeText(Ad_ThemMon.this, "Create new dish successfully", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Ad_ThemMon.this, "Failed to create dish: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}