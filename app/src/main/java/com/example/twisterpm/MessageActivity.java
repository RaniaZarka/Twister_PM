package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        View.OnTouchListener{

    public static final String MESSAGE = "message";
    private TextView viewMessage;
    private View messagesLayout;
    public static final String Email = "user";
    private GestureDetector mDetector;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbarToolbar);
        setSupportActionBar(toolbar);
        mDetector = new GestureDetector(this, this);
        //messagesLayout = findViewById(R.id.messageLayout);

    }


    @Override
    protected void onStart() {
        super.onStart();
        TextView text = findViewById(R.id.welcome);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser userfb = fAuth.getCurrentUser();
        if (userfb == null) {
            text.setText("welcome ");
        } else {
            String user= fAuth.getCurrentUser().getEmail();

            text.setText("welcome, " + user);}
        getAndShowAllMessages();
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
                this.recreate();
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
                Intent intent3 = new Intent(this, UsersActivity.class);
                startActivity(intent3);
                return true;
            case R.id.action_signin:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getAndShowAllMessages() {
        ApiServices services = ApiUtils.getMessagesService();
        Call<List<Message>> getAllMessagesCall = services.getAllMessages();

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
                    Log.d(MESSAGE, "the problem is: " + message);
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

    private void populateRecycleView(List<Message> allMessages) {
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewMessageAdapter adapter= new RecyclerViewMessageAdapter(this, allMessages);
        recyclerView.setAdapter(adapter);

        adapter.setClickListener((view, position, item) -> {
            Message message = (Message) item;
            Log.d(MESSAGE, "item is: " + item.toString());
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra(CommentActivity.MESSAGE, message);
            startActivity(intent);
        });
    }

    public void addMessage(View view) {
        EditText input = findViewById(R.id.messageInput);
        String content = input.getText().toString().trim();

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser userfb = fAuth.getCurrentUser();
        if (userfb == null) {
            Toast.makeText(getApplicationContext(), "You need to sign in first", Toast.LENGTH_SHORT).show();
        } else {
            String user = fAuth.getCurrentUser().getEmail();

            ApiServices services = ApiUtils.getMessagesService();
            Message message = new Message(content, user);
            Call<Message> saveNewMessageCall = services.saveMessage(message);
            if (content.isEmpty()) {
                Toast.makeText(getApplicationContext(), "You didnt write a message!", Toast.LENGTH_SHORT).show();
            } else
                saveNewMessageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            Message newMessage = response.body();
                            Log.d(MESSAGE, "the new message is: " + newMessage.toString());
                            Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                            input.setText("");
                            LinearLayout layout = findViewById(R.id.allMessagesAddLayout);
                            layout.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(MESSAGE, t.getMessage());
                    }
                });
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){
        mDetector.onTouchEvent(motionEvent);
        return true;

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d("gesture", "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.d("gesture", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.d("gesture", "onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d("gesture", "onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent)
    {
        Log.d("gesture", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent me1, MotionEvent me2, float v1, float v2) {
        //Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
        Log.d("gesture", "onFling " + me1.toString() + "::::" + me2.toString());

        boolean leftSwipe = me1.getX() > me2.getX();
        //boolean rightSwipe = me1.getY() < me2.getY();
        Log.d("gesture", "onFling left: " + leftSwipe);
        if (leftSwipe) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            //Bundle options = activityOptionsCompat.toBundle();
            //startActivity(intent, options);
        }

        return true; // done
    }
}



