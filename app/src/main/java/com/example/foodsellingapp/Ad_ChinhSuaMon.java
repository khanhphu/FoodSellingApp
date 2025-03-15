package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.CategoryAdapter;
import com.example.foodsellingapp.Model.Category;
import com.example.foodsellingapp.databinding.ActivityAdChinhSuaMonBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ad_ChinhSuaMon extends AppCompatActivity {
    private ActivityAdChinhSuaMonBinding binding;
    String maMon, tenMon;
    Integer gia, phuThu, sl;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("MonAn");
    Uri imgUri;
    int number = 0;
    static String uploadedImage;
    private ArrayAdapter<Category> categoryArrayAdapter;
    private List<Category> categoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdChinhSuaMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        maMon = getIntent().getStringExtra("maMon");
        binding.edtMaMon.setText(getIntent().getStringExtra("maMon"));
        binding.edtTenMon.setText(getIntent().getStringExtra("tenMon"));
        binding.edtGia.setText(getIntent().getStringExtra("gia"));
        binding.edtPhuThu.setText(getIntent().getStringExtra("phuThu"));
        binding.edtSL.setText(getIntent().getStringExtra("soLuong"));
        binding.txtCate.setText(getIntent().getStringExtra("categoryName"));
        Picasso.get().load(getIntent().getStringExtra("url")).into(binding.imgMon);
        //handle categoryName
        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
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
        //handle cancel
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle save
        binding.btnSaveMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidate();
            }
        });
        //image
        binding.imgMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);

            }
        });
        //btnIncrease and btnDecrease
        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slCon = Integer.parseInt((binding.edtSL.getText().toString()));

                binding.edtSL.setText(String.valueOf(slCon + 1));


            }
        });
        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sl_tempt = Integer.parseInt(binding.edtSL.getText().toString());
                binding.edtSL.setText(String.valueOf(sl_tempt - 1));
            }
        });

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                imgUri = data.getData();
                Toast.makeText(Ad_ChinhSuaMon.this, imgUri.toString(), Toast.LENGTH_SHORT).show();
                binding.imgMon.setImageURI(imgUri);
                //  Picasso.get().load(imgUri).into(binding.imgMon);
            }
        } catch (Exception e) {
            Log.e("CSuaMon", e.getMessage());
        }
    }

    private void checkValidate() {
        String tenMon = binding.edtTenMon.getText().toString().trim();
        String giaMon = binding.edtGia.getText().toString().trim();
        String sl = binding.edtSL.getText().toString();
        // Check if fields are empty
        if (tenMon.isEmpty()) {
            binding.edtTenMon.setError("Tên món ăn không để trống!");
            binding.edtTenMon.requestFocus();
            return;
        }

        if (giaMon.isEmpty()) {
            binding.edtGia.setError("Giá món ăn không để trống!");
            binding.edtGia.requestFocus();
            return;
        }
        if (sl.isEmpty()) {
            binding.edtSL.setError("SL món ăn không để trống!");
            binding.edtSL.requestFocus();
            return;
        }

        // Proceed with uploading or editing
        if (imgUri != null) {
            uploadImage(imgUri);
        } else {
            chinhSuaMon(uploadedImage);
        }
    }

    public void uploadImage(Uri uri) {

        StorageReference fileref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        uploadedImage = "" + uriTask.getResult();
                        chinhSuaMon(uploadedImage);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void chinhSuaMon(String uploadedImage) {
        tenMon = binding.edtTenMon.getText().toString().trim();
        gia = Integer.valueOf(binding.edtGia.getText().toString().trim());
        sl = Integer.valueOf(binding.edtSL.getText().toString().trim());
        if (binding.edtPhuThu.getText().toString().isEmpty()) {
            phuThu = 0;
        } else {
            phuThu = Integer.valueOf(binding.edtGia.getText().toString().trim());
        }

        if (uploadedImage == null) {
            uploadedImage = getIntent().getStringExtra("url");
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("tenMon", tenMon);
        hashMap.put("gia", gia);
        hashMap.put("phuThu", phuThu);
        hashMap.put("url", uploadedImage);
        //tuy chinh so luong
        hashMap.put("soLuong", sl);
        //category
        // Add category reference
        Category selectedCategory = (Category) binding.spCategory.getSelectedItem();
        if (selectedCategory != null) {
            binding.txtCate.setText(selectedCategory.getName());
            hashMap.put("categoryName", selectedCategory.getName());
        } else {
            Toast.makeText(Ad_ChinhSuaMon.this, "Please select a category!", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("MonAn").document(maMon);
        docRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Ad_ChinhSuaMon.this, "Chỉnh sửa món thành công !", Toast.LENGTH_SHORT).show();
                        clearAllEditText();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ad_ChinhSuaMon.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void clearAllEditText() {
        binding.edtTenMon.getText().clear();
        binding.edtMaMon.getText().clear();
        binding.edtGia.getText().clear();
        binding.edtPhuThu.getText().clear();
        binding.edtGioiThieu.getText().clear();
    }


    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


}




