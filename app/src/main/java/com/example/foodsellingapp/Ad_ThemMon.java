package com.example.foodsellingapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityAdThemMonBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Ad_ThemMon extends AppCompatActivity {
    private ActivityAdThemMonBinding binding;
    private String maMon, tenMon;
    private int gia, phuThu, sl;
    private Uri imgUri;
    private ImageView imgMon;
    private ProgressDialog progressDialog;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("MonAn");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdThemMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //progressDialog
        progressDialog = new ProgressDialog(Ad_ThemMon.this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        imgMon = binding.imgMon;
        //back
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //validate

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading new dishes...");
                progressDialog.show();
                checkMon();

            }
        });


        //pick image
        binding.imgMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
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
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        uploadedImage = "" + uriTask.getResult();
                        loadMon4FB(uploadedImage);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
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
        if (isValidGia() && isValidTenMon() && isValidImage()) uploadImage(imgUri);
    }

    private boolean isValidGia() {
        String txtGia = binding.txtGia.getText().toString();
        if (txtGia.isEmpty()) {
            showToastMessage("Giá không được để trống!");
            return false;
        }
        try {
            int gia = Integer.parseInt(txtGia);
            if (gia <= 0) {
                showToastMessage("Giá phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showToastMessage("Giá phải là số hợp lệ!"); //textfield has required input format is number
        }
        return true;

    }

    private boolean isValidTenMon() {
        String txtTenMon = binding.txtTenMon.getText().toString();
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
        //  maMon = binding.txtMaMon.getText().toString().trim();
        maMon = "M" + System.currentTimeMillis();
        tenMon = binding.txtTenMon.getText().toString().trim();
        gia = Integer.parseInt(binding.txtGia.getText().toString().trim());
        if (binding.txtPhuThu.getText().toString().equals("") || Integer.parseInt(binding.txtPhuThu.getText().toString()) <= 0) {
            phuThu = 0;

        } else {
            phuThu = Integer.parseInt(binding.txtPhuThu.getText().toString().trim());
        }
        //sl
        if (binding.txtSL.getText().toString().equals("") || Integer.parseInt(binding.txtSL.getText().toString()) <= 0) {
            sl = 0;

        } else {
            sl = Integer.parseInt(binding.txtSL.getText().toString().trim());
        }

        CollectionReference ref = firestore.collection("MonAn");

        DocumentReference docref = ref.document(maMon);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("maMon", maMon);
        hashMap.put("tenMon", tenMon);
        hashMap.put("gia", gia);
        hashMap.put("phuThu", phuThu);
        hashMap.put("soLuong", sl);
        hashMap.put("gioiThieu", binding.txtGioiThieu.getText().toString());
        //image
        hashMap.put("url", uploadedImage);
        docref.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(Ad_ThemMon.this, "Create new dishes successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Ad_ThemMon.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


}


