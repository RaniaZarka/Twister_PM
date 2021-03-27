package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class CommentActivity extends AppCompatActivity implements View.OnClickListener
        // implements PopupMenu.OnMenuItemClickListener
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

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
                    String message = "Problem " + response.code() + " " + response.message();
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
          Intent intent = new Intent(this,MainActivity.class);
          startActivity(intent);
        }
        else{
            String user= fAuth.getCurrentUser().getEmail();
        ApiServices services = ApiUtils.getMessagesService();
        Comments comment = new Comments(messageId, content, user);
        Call<Comments> saveNewCommentCall = services.saveCommentBody(messageId, comment);
        Log.d("addMessage", "the whole Comment object is " + comment.toString());
       if(user!=null){
        if (content.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You didnt write a message!", Toast.LENGTH_SHORT).show();
        } else
            saveNewCommentCall.enqueue(new Callback<Comments>() {
                @Override
                public void onResponse(Call<Comments> call, Response<Comments> response) {
                    if (response.isSuccessful()) {
                        Comments newComment = response.body();
                        Log.d("addMessage", "the new comment is: " + newComment.toString());
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                        // the following codes is to make an autorefresh so the added message shows right away
                        recreate();
                        input.setText("");
                    } else {
                        String problem = "Problem: " + response.code() + " " + response.message();
                        Log.e("addMessage", problem);
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Comments> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.e("addMessage", t.getMessage());
                }
            });}

        //else
        //{  Toast.makeText(getApplicationContext(), "You ned to sign in", Toast.LENGTH_SHORT).show();}
    }}

    public void DeleteMessage(View view) {
        ApiServices services = ApiUtils.getMessagesService();
        int messageId = theMessage.getId();
        Log.d("delete", "the message id is: " + messageId);
        String messageUser = theMessage.getUser();
        Log.d("delete", "the message user is: " + messageUser);
        fAuth = FirebaseAuth.getInstance();
        String user = fAuth.getCurrentUser().getEmail();
        Log.d("delete", "the email of the one deleting: " + user);
        Call<Message> deleteMessageCall = services.deleteMessage(messageId);

        if (messageUser.equals(user)) {
            deleteMessageCall.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {

                    if (response.isSuccessful()) {
                        String message = "Message deleted, id: " + theMessage.getId();
                        Toast.makeText(getBaseContext(), "Message is deleted: ", Toast.LENGTH_SHORT).show();
                        Log.d("delete", "the deleted message is" + message);
                        TextView text=findViewById(R.id.commentOriginalMessage);
                        text.setText("");
                        recreate();
                    } else {
                        String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                        Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("delete", "the problem is: " + problem);
                    }
                }
                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Snackbar.make(view, "Problem: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    Log.e("delete", "Problem: " + t.getMessage());
                }
            });
        } else
            Toast.makeText(getBaseContext(), "you can only delete your own message", Toast.LENGTH_SHORT).show();
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
            String user = fAuth.getCurrentUser().getEmail();
            Log.d("delete", "the one deleting: " + user);

            Call<Comments> deleteCommentCall = services.deleteComment(adapter.getItem(position).getId(), messageId);

            if (CommentUser.equals(user)) {
                deleteCommentCall.enqueue(new Callback<Comments>() {
                    @Override
                    public void onResponse(Call<Comments> call, Response<Comments> response) {

                        if (response.isSuccessful()) {
                            String message =  ""  +theMessage.getId();
                            Toast.makeText(getBaseContext(), "Comment is deleted: ", Toast.LENGTH_SHORT).show();
                            Log.d("delete", "the message id is " + message);
                            recreate();
                        } else {
                            String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                            Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.e("delete", "the problem is: " + problem);
                        }
                    }
                    @Override
                    public void onFailure(Call<Comments> call, Throwable t) {
                        //Snackbar.make(view, "Problem: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("delete", "Problem: " + t.getMessage());
                    }
                });
            }
            else
                Toast.makeText(getBaseContext(), "you can only delete your own comment", Toast.LENGTH_SHORT).show();
        }


    @Override
    public void onClick(View view) {

    }

    public void back(View view) {
        LinearLayout layout1 = findViewById(R.id.commentsDeleteAMessageLayout);
        layout1.setVisibility(View.GONE);
    }
}