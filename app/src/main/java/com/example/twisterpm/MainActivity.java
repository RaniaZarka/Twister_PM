package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // we can have all these in the onStart method or we can create a login method
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("UserApple", "Current user " + currentUser);
    }

    public void login(View view) {

        EditText emailView = findViewById(R.id.mainEmail);
        EditText paswordView = findViewById(R.id.mainPasswordInput);
        String email = emailView.getText().toString().trim();
        String password = paswordView.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("UserApple", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("userApple", mAuth.getCurrentUser().toString());
                                //updateUI(user);
                                TextView text=findViewById(R.id.mainWelcome);
                                text.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getBaseContext(), AllMessagesActivity.class);
                                intent.putExtra(AllMessagesActivity.Email, email);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("UserApple", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getBaseContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void register(View view) {
        EditText emailView = findViewById(R.id.mainEmail);
        EditText paswordView = findViewById(R.id.mainPasswordInput);
        String email = emailView.getText().toString().trim();
        String password = paswordView.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_LONG).show();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("UserApple", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                TextView text=findViewById(R.id.mainWelcome);
                                //text.setText(user.getEmail());
                               // text.setVisibility(View.VISIBLE);
                                //updateUI(user);
                                Toast.makeText(getApplicationContext(), "Welcome to Twister PM" + user.getEmail() + ". Please sign in", Toast.LENGTH_LONG).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("UserApple", "createUserWithEmail:failure", task.getException());
                                // we can use toast to show a message for the user, but the Toast is a message that disappear quickly
                                Toast.makeText(getBaseContext(), task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void logout(View view) {

        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), " You are signe out!", Toast.LENGTH_LONG).show();
    }
}