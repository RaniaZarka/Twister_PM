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

import com.google.android.material.snackbar.Snackbar;

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

 public static final String MESSAGE="message";
 public static final String EMAIL="email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
         theMessage =(Message) intent.getSerializableExtra(MESSAGE);

        message=findViewById(R.id.commentOriginalMessage);
        message.setText(theMessage.getContent()+"");
         getAllComments();

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
        RecyclerViewSimpleAdapter<Comments>adapter= new RecyclerViewSimpleAdapter<>(allComments);
        recyclerView.setAdapter(adapter);
        //adapter.setOnItemClickListener((view, position, item) -> {
           // Comments comments = (Comments) item;
        //Log.d("comment", item.toString());
        //});
    }


    public void commentAdd(View view) {
            EditText input= findViewById(R.id.commentInsertComment);
            String content = input.getText().toString().trim();
            Log.d("addMessage", " comment content is: " + content.toString());
            int Id= theMessage.getId();
            Log.d("addMessage" , "the message id is: "+ Id);
            //String user = getIntent().getStringExtra(Email);
            String user ="rania@hotmail.com";
            Log.d("addMessage", "the user is: " +user.toString());

            ApiServices services= ApiUtils.getMessagesService();
            Comments comment = new Comments(Id,content, user);
            Call<Comments> saveNewCommentCall = services.saveCommentBody(comment);
            Log.d("addMessage", "the whole Comment object is " +comment.toString());
            //progressBar.setVisibility(View.VISIBLE);
            saveNewCommentCall.enqueue(new Callback<Comments>() {
                @Override
                public void onResponse(Call<Comments> call, Response<Comments> response)
                {
                    //progressBar.setVisibility(View.INVISIBLE);
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
                    //progressBar.setVisibility(View.INVISIBLE);
                    // messageView.setText(t.getMessage());
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.e("addMessage", t.getMessage());
                }
            });

        }


    public void commentDeleteMessage(View view) {

        Intent intent = getIntent();
        ApiServices services = ApiUtils.getMessagesService();
        int messageId = theMessage.getId();
        String messageUser=theMessage.getUser();
        String theuser = intent.getStringExtra(EMAIL);
        Call<Message> deleteMessageCall = services.deleteMessage(messageId);

        deleteMessageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                if(messageUser== theuser) {
                    if (response.isSuccessful()) {
                        //Snackbar.make(view, "Book deleted, id: " + originalBook.getId(), Snackbar.LENGTH_LONG).show();
                        String message = "Message deleted, id: " + theMessage.getId();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        Log.d("delete", message);
                        recreate();

                    } else
                        Toast.makeText(getBaseContext(), "you can only delete your own message", Toast.LENGTH_SHORT).show();
                    }
                else {
                        //Snackbar.make(view, "Problem: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).show();
                        String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                        //messageView.setText(problem);
                        Toast.makeText(getBaseContext(), problem, Toast.LENGTH_SHORT).show();
                        Log.e("delete", problem);
                    }
                }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Snackbar.make(view, "Problem: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.e("delete", "Problem: " + t.getMessage());
            }
        });


    }

    public void commentDelete(View view) {
    }

    public void CommentBackBtn(View view){
        finish();
    }
}