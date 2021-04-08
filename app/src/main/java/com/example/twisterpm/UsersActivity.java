package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = findViewById(R.id.toolbarToolbar);
        setSupportActionBar(toolbar);
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
                fAuth = FirebaseAuth.getInstance();
                FirebaseUser userfb = fAuth.getCurrentUser();
                if (userfb == null) {
                    Toast.makeText(getApplicationContext(), "You need to sign in first", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);}
                return true;
            case R.id.action_search:
                this.recreate();
                return true;
            case R.id.action_signin:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void findUser(View view) {

        EditText input = findViewById(R.id.userName);
        String user = input.getText().toString().trim();

        if (user.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter a user", Toast.LENGTH_SHORT).show();
        } else if(user==null) {
            Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
        }
        else{
            ApiServices services = ApiUtils.getMessagesService();
            Call<List<Message>> getAllMessagesCall = services.getMessagesByUser(user);

            getAllMessagesCall.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    Log.d("users", response.raw().toString());

                    if (response.isSuccessful()) {
                        List<Message> allMessages = response.body();
                        Log.d("users", allMessages.toString());
                        populateRecycleView(allMessages);
                    } else {
                        String message = "Problem " + response.code() + " " + response.message();
                        Log.d("users", "the problem is: " + message);

                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Log.e("users", t.getMessage());

                }
            });
        }
    }

    private void populateRecycleView(List<Message> allMessages) {
        RecyclerView recyclerView = findViewById(R.id.UserMessageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewProfileMessageAdapter adapter= new RecyclerViewProfileMessageAdapter(this, allMessages);
        recyclerView.setAdapter(adapter);

        adapter.setClickListener((view, position, item) -> {
            Message message = (Message) item;
            Log.d("users", "item is: " + item.toString());
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra(CommentActivity.MESSAGE, message);
            startActivity(intent);
        });
    }
}