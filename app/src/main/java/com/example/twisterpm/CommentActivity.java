package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity  implements
        View.OnClickListener,
        GestureDetector.OnGestureListener

{
    private TextView message;
    private TextView comment;
    private TextView user;
    private Message theMessage;
    private Comments theComment;
    private ImageButton imageButton;
    public static final String Email = "user";
    FirebaseAuth fAuth;
    public static final String MESSAGE = "message";
    RecyclerViewCommentAdapter adapter;
    private Layout layout;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbarToolbar);
        setSupportActionBar(toolbar);
        mDetector = new GestureDetector(this, this);

        Intent intent = getIntent();
        theMessage = (Message) intent.getSerializableExtra(MESSAGE);
        user = findViewById(R.id.commentoriginalMessageEmail);
        user.setText(theMessage.getUser() + "");

        message = findViewById(R.id.commentOriginalMessage);
        message.setText(theMessage.getContent() + "");

        imageButton = findViewById(R.id.MessageimageButton);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                PopupMenu popupMenu = new PopupMenu(CommentActivity.this, imageButton );
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                            LinearLayout layout = findViewById(R.id.allCommentsAddLayout);
                            LinearLayout layout1 = findViewById(R.id.commentsDeleteAMessageLayout);
                            switch (item.getItemId()) {
                                case R.id.popupAdd:
                                    layout.setVisibility(View.VISIBLE);
                                    return true;
                                case R.id.popupDelete:

                                    layout1.setVisibility(View.VISIBLE);
                                    layout.setVisibility(View.GONE);
                                    return true;
                                default:
                                    return true;
                            }
                        }
                });
                popupMenu.show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        getAllComments();
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
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getAllComments() {
        int Id = theMessage.getId();
        Log.d("addMessage", "the message id is: " + Id);
        ApiServices services = ApiUtils.getMessagesService();
        Call<List<Comments>> getAllCommentsCall = services.getCommentById(Id);
        Log.d("addMessage", "calling all the messge: " + getAllCommentsCall.toString());
        comment = findViewById(R.id.messageMessages);

        getAllCommentsCall.enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response) {
                Log.d("addMessage", "the response is: " + response.raw().toString());

                if (response.isSuccessful()) {
                    List<Comments> allComments = response.body();

                    Log.d("addMessage", "list of all comments: " + allComments.toString());
                    populateRecycleView(allComments);
                } else {
                    String message = getResources().getString(R.string.Problem)+" "+ response.code() + " " + response.message();
                    Log.d("addMessage", "problem showing: " + message);
                    comment.setText(message);
                }
            }
            @Override
            public void onFailure(Call<List<Comments>> call, Throwable t) {

                Log.e("AddMessage", "on failure showing " + t.getMessage());
                comment.setText(t.getMessage());
            }
        });
    }

    private void populateRecycleView(List<Comments> allComments) {
        RecyclerView recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewCommentAdapter(this, allComments);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener((view, position, item) -> {
                    theComment = item;
            if (position >= 0) {
             DeleteComment(position);
             Log.d("delete", "position is: "+ position);
                    Log.d("delete", "the comment to delete is:  " + item.toString());}
                });
            }

    public void commentAdd(View view) {
        EditText input = findViewById(R.id.commentInput);
        String content = input.getText().toString().trim();
        Log.d("addMessage", " comment content is: " + content.toString());
        int messageId = theMessage.getId();
        Log.d("addMessage", "the message id is: " + messageId);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser userfb = fAuth.getCurrentUser();
        Log.d("addMessage", "the user is: " + user.toString());
        if(userfb==null)
        {
          //Intent intent = new Intent(this,MainActivity.class);
          //startActivity(intent);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn), Toast.LENGTH_SHORT).show();
        }
        else{
            String user= fAuth.getCurrentUser().getEmail();
        ApiServices services = ApiUtils.getMessagesService();
        Comments comment = new Comments(messageId, content, user);
        Call<Comments> saveNewCommentCall = services.saveCommentBody(messageId, comment);
        Log.d("addMessage", "the whole Comment object is " + comment.toString());
       if(user!=null){
        if (content.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoComment), Toast.LENGTH_SHORT).show();
        } else
            saveNewCommentCall.enqueue(new Callback<Comments>() {
                @Override
                public void onResponse(Call<Comments> call, Response<Comments> response) {
                    if (response.isSuccessful()) {
                        Comments newComment = response.body();
                        Log.d("addMessage", "the new comment is: " + newComment.toString());
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Successfullyadded), Toast.LENGTH_SHORT).show();
                        // the following codes is to make an autorefresh so the added message shows right away
                        recreate();
                        input.setText("");
                    } else {
                        String problem = getResources().getString(R.string.Problem)+" "+ response.code() + " " + response.message();
                        Log.e("addMessage", problem);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Comments> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                    Log.e("addMessage", t.getMessage());
                }
            });}
    }}

    public void DeleteMessage(View view) {
        ApiServices services = ApiUtils.getMessagesService();
        int messageId = theMessage.getId();
        Log.d("delete", "the message id is: " + messageId);
        String messageUser = theMessage.getUser();
        Log.d("delete", "the message user is: " + messageUser);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser userfb = fAuth.getCurrentUser();
        Log.d("addMessage", "the user is: " + user.toString());
        if (userfb == null) {
            //Intent intent = new Intent(this,MainActivity.class);
            //startActivity(intent);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn), Toast.LENGTH_SHORT).show();
        } else {
            String user = fAuth.getCurrentUser().getEmail();
            Log.d("delete", "the email of the one deleting: " + user);
            Call<Message> deleteMessageCall = services.deleteMessage(messageId);

            if (messageUser.equals(user)) {
                deleteMessageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {

                        if (response.isSuccessful()) {
                            String message = "Message deleted, id: " + theMessage.getId();
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.MessageDeleted), Toast.LENGTH_SHORT).show();
                            Log.d("delete", "the deleted message is" + message);
                            TextView text = findViewById(R.id.commentoriginalMessageEmail);
                            text.setText("");
                            recreate();
                        } else {
                            String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                            Log.e("delete", "the problem is: " + problem);
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Snackbar.make(view, getResources().getString(R.string.Problem)+" " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                        Log.e("delete", "Problem: " + t.getMessage());
                    }
                });
            } else
                Toast.makeText(getBaseContext(), getResources().getString(R.string.OnlyOwnMessage), Toast.LENGTH_SHORT).show();
        }
    }

        public void DeleteComment(int position) {
            ApiServices services = ApiUtils.getMessagesService();
            int commentId = theComment.getId();
            Log.d("delete", "the comment id is: " + commentId);
            int messageId = theMessage.getId();
            Log.d("delete", "the message id is: " + messageId);
            String CommentUser = theComment.getUser();
            Log.d("delete", "the comment user is: " + CommentUser);
            fAuth = FirebaseAuth.getInstance();
            FirebaseUser userfb = fAuth.getCurrentUser();
            Log.d("addMessage", "the user is: " + user.toString());
            if (userfb == null) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn), Toast.LENGTH_SHORT).show();
            } else {
                String user = fAuth.getCurrentUser().getEmail();
                Log.d("delete", "the one deleting: " + user);
                Call<Comments> deleteCommentCall = services.deleteComment(messageId, adapter.getItem(position).getId());
                if (CommentUser.equals(user)) {
                    deleteCommentCall.enqueue(new Callback<Comments>() {
                        @Override
                        public void onResponse(Call<Comments> call, Response<Comments> response) {

                            if (response.isSuccessful()) {
                                String message = "" + theMessage.getId();
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.CommentDeleted), Toast.LENGTH_SHORT).show();
                                Log.d("delete", "the message id is " + message);
                                recreate();
                            } else {
                                String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                                Log.e("delete", "the problem is: " + problem);
                            }
                        }

                        @Override
                        public void onFailure(Call<Comments> call, Throwable t) {
                            //Snackbar.make(view, "Problem: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.Wrong) + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("delete", "Problem: " + t.getMessage());
                        }
                    });
                } else
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.OnlyOwnComment), Toast.LENGTH_SHORT).show();
            }
        }

    public void back(View view) {
        LinearLayout layout1 = findViewById(R.id.commentsDeleteAMessageLayout);
        layout1.setVisibility(View.GONE);
    }

@Override
public boolean onTouchEvent(MotionEvent event){
        return mDetector.onTouchEvent(event);
}
    @Override
    public void onClick(View view) {

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
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            //Bundle options = activityOptionsCompat.toBundle();
            //startActivity(intent, options);
        }

        return true; // done
    }


}