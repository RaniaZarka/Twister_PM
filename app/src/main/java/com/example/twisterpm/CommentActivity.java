package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
 private TextView message;
 private TextView comment;
 private Message theMessage;
 private Comments messageId;
 public static final String Email="user";
    FirebaseAuth fAuth;
    public static final String MESSAGE="message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
         theMessage =(Message) intent.getSerializableExtra(MESSAGE);

        message=findViewById(R.id.commentOriginalMessage);
        message.setText(theMessage.getContent()+"");
         getAllComments();

       /* FloatingActionButton fab = findViewById(R.id.commentFab);
        fab.setOnClickListener( view -> {
            new Intent(CommentActivity.this, AllMessagesActivity.class);
            startActivity(intent);*/
       // }
       // );

    }

    public void getAllComments()
    {
        int Id =  theMessage.getId();
         Log.d("addMessage", "the message id is: " +Id);
        ApiServices services = ApiUtils.getMessagesService();
        Call<List<Comments>> getAllCommentsCall = services.getCommentById(Id);
        Log.d("addMessage", "calling all the messge: " + getAllCommentsCall.toString());
        comment = findViewById(R.id.messageMessages);

        getAllCommentsCall.enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response) {
                Log.d("addMessage","the response is: " +response.raw().toString());

                if (response.isSuccessful()) {
                    List<Comments> allComments = response.body();

                    Log.d("addMessage","list of all comments: " +allComments.toString());
                    populateRecycleView(allComments);
                } else {
                    String message = "Problem " + response.code() + " " + response.message();
                    Log.d("addMessage","problem showing: " +message);
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
    private void populateRecycleView(List<Comments>allComments){
        RecyclerView recyclerView =findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewCommentAdapter<Comments>adapter= new RecyclerViewCommentAdapter<>(allComments);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            Comments comments = (Comments) item;
        Log.d("comment", item.toString());
           /*   Intent intent = new Intent(this, CommentActivity.class);
                intent.putExtra(CommentActivity.MESSAGE, message);
                startActivity(intent);*/
        });
    }
    public void commentAdd(View view) {
            EditText input= findViewById(R.id.commentInsertComment);
            String content = input.getText().toString().trim();
            Log.d("addMessage", " comment content is: " + content.toString());
            int messageId= theMessage.getId();
            Log.d("addMessage" , "the message id is: "+ messageId);

             fAuth = FirebaseAuth.getInstance();
            String user = fAuth.getCurrentUser().getEmail();
            Log.d("addMessage", "the user is: " +user.toString());

            ApiServices services= ApiUtils.getMessagesService();
            Comments comment = new Comments(messageId,content, user);
            Call<Comments> saveNewCommentCall = services.saveCommentBody(messageId,comment);
            Log.d("addMessage", "the whole Comment object is " +comment.toString());

            saveNewCommentCall.enqueue(new Callback<Comments>() {
                @Override
                public void onResponse(Call<Comments> call, Response<Comments> response)
                {
                    if (response.isSuccessful()) {
                        Comments newComment = response.body();
                        Log.d("addMessage","the new comment is: " +newComment.toString());
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                        // the following codes is to make an autorefresh so the added message shows right away
                        recreate();
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
            });
    }

    public void commentDeleteMessage(View view) {
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
        }
        else
            Toast.makeText(getBaseContext(), "you can only delete your own message", Toast.LENGTH_SHORT).show();
    }

        public void commentDelete(View view) {
    }
        public void CommentBackBtn(View view){
        finish();
    }
}