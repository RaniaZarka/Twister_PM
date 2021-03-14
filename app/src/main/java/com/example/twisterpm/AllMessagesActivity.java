package com.example.twisterpm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllMessagesActivity extends AppCompatActivity {
public static final String MESSAGE ="message";
    private TextView viewMessage;
    private View messagesLayout;
    private Message messageToDelete;
    public static final String Email="user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getAndShowAllMessages();

        messagesLayout = findViewById(R.id.messageLayout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent(AllMessagesActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add:
                LinearLayout layout= findViewById(R.id.allMessagesAddLayout);
                layout.setVisibility(View.VISIBLE) ;
                return true;
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "no settings added yet",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getAndShowAllMessages() {
        ApiServices services = ApiUtils.getMessagesService();
        Call<List<Message>> getAllMessagesCall = services.getAllMessages();
        viewMessage = findViewById(R.id.messageMessages);
        viewMessage.setText("");

        getAllMessagesCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                Log.d(MESSAGE, response.raw().toString());

                if (response.isSuccessful()) {
                    List<Message> allMessages = response.body();
                    Log.d(MESSAGE, allMessages.toString());
                    populateRecycleView(allMessages);
                } else {
                    String message = "Problem " + response.code() + " " + response.message();
                    Log.d(MESSAGE, "the problem is: " +message);
                    viewMessage.setText(message);
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e(MESSAGE, t.getMessage());
                viewMessage.setText(t.getMessage());
            }
        });
    }

        private void populateRecycleView(List<Message>allMessages){
            RecyclerView recyclerView =findViewById(R.id.messageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            RecyclerViewSimpleAdapter<Message>adapter= new RecyclerViewSimpleAdapter<>(allMessages);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position, item) -> {
                Message message = (Message) item;
                Log.d(MESSAGE, "item is: " +item.toString());
                Intent intent = new Intent(this, CommentActivity.class);
                intent.putExtra(CommentActivity.MESSAGE, message );
                startActivity(intent);
            });
        }

    public void addMessage(View view ){
        EditText input= findViewById(R.id.messageInput);
        String content = input.getText().toString().trim();
        String user = getIntent().getStringExtra(Email);

        ApiServices services= ApiUtils.getMessagesService();
        Message message = new Message(content, user);
        Call<Message> saveNewMessageCall = services.saveMessage(message);
        saveNewMessageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Message newMessage = response.body();
                    Log.d(MESSAGE, "the new message is: " +newMessage.toString());
                    Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                    // the following codes is to make an autorefresh so the added message shows right away
                    recreate();
                } else {
                    String problem = "Problem: " + response.code() + " " + response.message();
                    Log.e(MESSAGE, " the problem is: " + problem);
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                //progressBar.setVisibility(View.INVISIBLE);
               // messageView.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e(MESSAGE, t.getMessage());
            }
        });
    }
}
