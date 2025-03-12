package com.example.foodsellingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class User_SettingAccount extends AppCompatActivity {

    private static final String TAG = "UPDATE USER PROFILE";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String uid;
    private Uri imageUri; // To store the selected image URI
    private ShapeableImageView imgProfile;
    private ProgressBar progressBarImageUpload;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_account);

        // Initialize Firebase
        uid = firebaseAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextInputEditText etName = findViewById(R.id.etName);
        TextInputEditText etUsername = findViewById(R.id.etUsername);
        AutoCompleteTextView dropdownCountryCode = findViewById(R.id.dropdownCountryCode);
        TextInputEditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        AutoCompleteTextView dropdownGender = findViewById(R.id.dropdownGender);
        Button btnCompleteProfile = findViewById(R.id.btnCompleteProfile);
        ConstraintLayout profileImageContainer = findViewById(R.id.profileImageContainer);
        imgProfile = findViewById(R.id.imgProfilePlaceholder);
        progressBarImageUpload = findViewById(R.id.progressBarImageUpload);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                imgProfile.setImageURI(imageUri); // Display the selected image
            }
        });

        // Make profile image clickable to pick an image
        profileImageContainer.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Populate Country Code dropdown
        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"+84", "+1", "+44", "+91"}
        );
        dropdownCountryCode.setAdapter(countryCodeAdapter);

        // Populate Gender dropdown
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Male", "Female", "Other", "Prefer not to say"}
        );
        dropdownGender.setAdapter(genderAdapter);

        // Fetch user data from Firestore and pre-fill the form
        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String username = documentSnapshot.getString("username");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String gender = documentSnapshot.getString("gender");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        // Pre-fill the form fields
                        if (name != null) {
                            etName.setText(name);
                            Toast.makeText(this, "Name: " + name, Toast.LENGTH_SHORT).show();
                        }
                        if (username != null) etUsername.setText(username);
                        if (phoneNumber != null && phoneNumber.length() > 3) {
                            dropdownCountryCode.setText(phoneNumber.substring(0, 3));
                            etPhoneNumber.setText(phoneNumber.substring(3));
                        }
                        if (gender != null) dropdownGender.setText(gender);
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(imgProfile);
                        }
                    } else {
                        Toast.makeText(this, "User data not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching user data from Firestore", e);
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Complete Profile button click
        btnCompleteProfile.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String countryCode = dropdownCountryCode.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String gender = dropdownGender.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty() || username.isEmpty() || countryCode.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combine country code and phone number
            String fullPhoneNumber = countryCode + phoneNumber;

            // Create a user data map
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("username", username);
            userData.put("phoneNumber", fullPhoneNumber);
            userData.put("gender", gender);
            userData.put("updatedAt", System.currentTimeMillis());

            // Upload image if selected, then update Firestore
            if (imageUri != null) {
                uploadImageToStorage(imageUri, userData);
            } else {
                updateUserInFirestore(uid, userData);
            }
        });
    }

    private void uploadImageToStorage(Uri imageUri, Map<String, Object> userData) {
        progressBarImageUpload.setVisibility(View.VISIBLE);
        StorageReference imageRef = storage.getReference()
                .child("profile_images/" + uid + "/profile_image.jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userData.put("profileImageUrl", imageUrl);
                        updateUserInFirestore(uid, userData);
                        progressBarImageUpload.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        progressBarImageUpload.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    progressBarImageUpload.setVisibility(View.GONE);
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInFirestore(String uid, Map<String, Object> userData) {
        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, update it
                        db.collection("Users")
                                .document(uid)
                                .update(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User data successfully updated in Firestore!");
                                    Toast.makeText(User_SettingAccount.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error updating user data in Firestore", e);
                                    Toast.makeText(User_SettingAccount.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Document does not exist, create it
                        db.collection("Users")
                                .document(uid)
                                .set(userData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User data successfully created in Firestore!");
                                    Toast.makeText(User_SettingAccount.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error creating user data in Firestore", e);
                                    Toast.makeText(User_SettingAccount.this, "Failed to create profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error checking document existence", e);
                    Toast.makeText(User_SettingAccount.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}