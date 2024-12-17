package com.example.uchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText;
    private Button saveButton;
    private Button deleteAccountButton;
    private ProgressDialog progressDialog;
    private final int CAMERA_REQ_CODE=100;
    ImageView imgCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        //elements

        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);


        //Camera
        imgCamera = findViewById(R.id.profileImageView);
        Button btncamera = findViewById(R.id.btncamera);
        Button btnlocal = findViewById(R.id.btnlocal);

        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera,CAMERA_REQ_CODE);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateNameInDatabase(newName);
                } else {
                    Toast.makeText(EditProfile.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
          if(resultCode==CAMERA_REQ_CODE){
              // camera
              Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
              imgCamera.setImageBitmap((Bitmap) data.getExtras().get("data"));
          }
        }
        else{

        }
    }

    private void updateNameInDatabase(String newName) {
        String userId = getCurrentUserId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userId).child("name").setValue(newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfile.this, "Name updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Error updating name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    private void deleteAccount() {
        String userId = getCurrentUserId();

        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showProgressDialog("Deleting account...");
                    deleteAccountInBackground(userId);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccountInBackground(String userId) {
        // 1. Get Firebase instances
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // 2. Delete user data from Realtime Database
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // 3. Delete user from Firebase Authentication
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        user.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    // 4. Account deletion successful
                                    runOnUiThread(() -> {
                                        hideProgressDialog();
                                        Toast.makeText(EditProfile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                        // Navigate to login screen or perform other actions
                                        finish();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // 5. Handle Authentication deletion failure
                                    runOnUiThread(() -> {
                                        hideProgressDialog();
                                        showErrorDialog("Failed to delete account. Please try again later.");
                                    });
                                });
                    } else {
                        // 6. Handle case where user is not logged in
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            showErrorDialog("User not logged in.");
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // 7. Handle Realtime Database deletion failure
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        showErrorDialog("Failed to delete account data. Please try again later.");
                    });
                });
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}