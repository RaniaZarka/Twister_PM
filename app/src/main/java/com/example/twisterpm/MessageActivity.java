package com.example.twisterpm;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements

        View.OnTouchListener{

    public static final String MESSAGE = "message";
    private TextView viewMessage;
    private View messagesLayout;
    public static final String Email = "user";

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LoadLocale();
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbarToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView text = findViewById(R.id.welcome);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser userfb = fAuth.getCurrentUser();
        if (userfb == null) {
            text.setText(getResources().getString(R.string.welcome));
        } else {
            String user= fAuth.getCurrentUser().getEmail();

            text.setText(getResources().getString(R.string.welcome) +" " +  user);}
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
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn) , Toast.LENGTH_SHORT).show();
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
                    String message = getResources().getString(R.string.Problem)+" " + response.code() + " " + response.message();
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotSignedIn), Toast.LENGTH_SHORT).show();
        } else {
            String user = fAuth.getCurrentUser().getEmail();

            ApiServices services = ApiUtils.getMessagesService();
            Message message = new Message(content, user);
            Call<Message> saveNewMessageCall = services.saveMessage(message);
            if (content.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoMessage), Toast.LENGTH_SHORT).show();
            } else
                saveNewMessageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            Message newMessage = response.body();
                            Log.d(MESSAGE, "the new message is: " + newMessage.toString());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Successfullyadded), Toast.LENGTH_SHORT).show();
                            input.setText("");
                            LinearLayout layout = findViewById(R.id.allMessagesAddLayout);
                            layout.setVisibility(View.INVISIBLE);
                            // the following codes is to make an autorefresh so the added message shows right away
                            recreate();
                        } else {
                            String problem = getResources().getString(R.string.Problem)+" " + response.code() + " " + response.message();
                            Log.e(MESSAGE, " the problem is: " + problem);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Wrong), Toast.LENGTH_SHORT).show();
                        Log.e(MESSAGE, t.getMessage());
                    }
                });
        }
    }

   /* private void showChangeLanguageDialog(){
        final String[] listItems= {"Francais", "English" ,  "Dansk"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("fr");
                    recreate();
                }
               else if(i==1){
                    setLocale("En");
                    recreate();
                }
              else  if(i==2){
                    setLocale("da-rDK");
                    recreate();
                }
              //dismiss alert dialog when language is selected 
              dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog =mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences(" settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    // load languages saved in shared preferences

    public void LoadLocale(){

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE );
        String language= prefs.getString("My_Lang", "");
        setLocale(language);
    }*/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){
        return true;

    }
}



