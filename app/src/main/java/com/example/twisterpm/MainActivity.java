package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    private static  final String TAG ="autoSignin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbarToolbar);
        setSupportActionBar(toolbar);

    }

    // we can have all these in the onStart method or we can create a login method
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("UserApple", "Current user " + currentUser);
        /*if(currentUser !=null){
            reload();
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
            Intent intent2= new Intent(this, MessageActivity.class);
            startActivity(intent2);
                return true;
            case R.id.action_profile:
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser userfb = mAuth.getCurrentUser();
                if (userfb == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);}
                return true;
            case R.id.action_search:
                Intent intent3 = new Intent(this, UsersActivity.class);
                startActivity(intent3);
                return true;
            case R.id.action_signin:
                this.recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void login(View view) {
        EditText emailView = findViewById(R.id.mainEmail);
        EditText paswordView = findViewById(R.id.mainPasswordInput);
        String email = emailView.getText().toString().trim();
        String password = paswordView.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),  getResources().getString(R.string.AllFields), Toast.LENGTH_LONG).show();
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
                                Intent intent = new Intent(getBaseContext(), MessageActivity.class);
                                intent.putExtra(MessageActivity.Email, email);
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.AllFields), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Welcome)+" "+  user.getEmail() +". "+ getResources().getString(R.string.PleaseSignIn), Toast.LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.YourAreSignedOut), Toast.LENGTH_LONG).show();
    }
}