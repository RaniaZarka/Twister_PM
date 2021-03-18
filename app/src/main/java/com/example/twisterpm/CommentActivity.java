package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener
        // implements PopupMenu.OnMenuItemClickListener
{
    private TextView message;
    private TextView comment;
    private Message theMessage;
    private Comments theComment;
    private ImageButton imageButton;
    public static final String Email = "user";
    FirebaseAuth fAuth;
    public static final String MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        theMessage = (Message) intent.getSerializableExtra(MESSAGE);

        message = findViewById(R.id.commentOriginalMessage);
        message.setText(theMessage.getContent() + "");
        getAllComments();

//        imageButton = imageButton.findViewById(R.id.imageButton);
       // imageButton.setOnClickListener(this);

        //popupMenu.setOnMenuItemClickListener(this);
       /* FloatingActionButton fab = findViewById(R.id.commentFab);
        fab.setOnClickListener( view -> {
            new Intent(CommentActivity.this, AllMessagesActivity.class);
            startActivity(intent);*/
        // }
        // );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popupAdd:
                LinearLayout layout = findViewById(R.id.allCommentsAddLayout);
                layout.setVisibility(View.VISIBLE);
                return true;

            case R.id.popupDelete:
              LinearLayout layout1 = findViewById(R.id.commentsDeleteAMessageLayout);
                layout1.setVisibility(View.VISIBLE);
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
        RecyclerViewSimpleAdapter<Comments> adapter = new RecyclerViewSimpleAdapter<>(allComments);
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
        EditText input = findViewById(R.id.commentInput);
        String content = input.getText().toString().trim();
        Log.d("addMessage", " comment content is: " + content.toString());
        int messageId = theMessage.getId();
        Log.d("addMessage", "the message id is: " + messageId);

        fAuth = FirebaseAuth.getInstance();
        String user = fAuth.getCurrentUser().getEmail();
        Log.d("addMessage", "the user is: " + user.toString());

        ApiServices services = ApiUtils.getMessagesService();
        Comments comment = new Comments(messageId, content, user);
        Call<Comments> saveNewCommentCall = services.saveCommentBody(messageId, comment);
        Log.d("addMessage", "the whole Comment object is " + comment.toString());

        if(content.isEmpty())
        {  Toast.makeText(getApplicationContext(), "You didnt write a message!", Toast.LENGTH_SHORT).show();}
        else
        saveNewCommentCall.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                if (response.isSuccessful()) {
                    Comments newComment = response.body();
                    Log.d("addMessage", "the new comment is: " + newComment.toString());
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


        public void commentDelete() {

        ApiServices services = ApiUtils.getMessagesService();
         //theComment = new Comments();

            int commentId = theComment.getId();
            int messageId = theComment.getMessageId();
            Log.d("delete", "the comment id is: " + commentId);
            String CommentUser = theComment.getUser();
            Log.d("delete", "the comment user is: " + CommentUser);
            fAuth = FirebaseAuth.getInstance();
            String user = fAuth.getCurrentUser().getEmail();
            Log.d("delete", "the one deleting: " + user);

            Call<Comments> deleteCommentCall = services.deleteComment(commentId);

            if (CommentUser.equals(user)) {
                deleteCommentCall.enqueue(new Callback<Comments>() {
                    @Override
                    public void onResponse(Call<Comments> call, Response<Comments> response) {

                        if (response.isSuccessful()) {
                            String message = "C deleted, id: " + theMessage.getId();
                            Toast.makeText(getBaseContext(), "Comment is deleted: ", Toast.LENGTH_SHORT).show();
                            Log.d("delete", "the deleted comment is" + message);
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
                Toast.makeText(getBaseContext(), "you can only delete your own message", Toast.LENGTH_SHORT).show();
        }



    public void CommentBackBtn(View view){
        finish();
    }

    @Override
    public void onClick(View view) {

    }

    public void back(View view) {
        LinearLayout layout1 = findViewById(R.id.commentsDeleteAMessageLayout);
        layout1.setVisibility(View.GONE);
    }
}