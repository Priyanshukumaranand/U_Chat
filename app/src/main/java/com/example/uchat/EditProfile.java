package com.example.uchat;

import android.os.Bundle;
import android.view.View; // Corrected import
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Import for Toast

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        EditText nameEditText = findViewById(R.id.nameEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() { // Using android.view.View
            @Override
            public void onClick(View v) { // Using android.view.View
                String newName = nameEditText.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateNameInDatabase(newName);
                } else {
                    Toast.makeText(EditProfile.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateNameInDatabase(String newName) {
        // 1. Get the current user's ID
        String userId = getCurrentUserId(); // Replace with your logic to get the user ID

        // 2. Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users"); // Assuming "users" is your database node

        // 3. Update the user's name
        usersRef.child(userId).child("name").setValue(newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Name updated successfully
                        Toast.makeText(EditProfile.this, "Name updated", Toast.LENGTH_SHORT).show();
                        // You might want to update the UI or navigate back here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                        Toast.makeText(EditProfile.this, "Error updating name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Handle case where user is not logged in
            return null;
        }
    }

    // ... your updateNameInDatabase() method ...
}