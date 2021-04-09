package com.example.twisterpm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    val userfb: FirebaseUser = FirebaseAuth.getInstance().currentUser;
    val user: String = userfb.getEmail()
    private var theComment: Comments? = null
    private var theMessage: Message? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbarToolbar)
        setSupportActionBar(toolbar)
        ProfileText.setText( getResources().getString(R.string.MyProfile) +" "+ user)
    }

    override fun onStart() {
        super.onStart()
        getAndShowMyMessages();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.messages_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent2 = Intent(this, MessageActivity::class.java)
                startActivity(intent2)
                true
            }
            R.id.action_profile -> {
                recreate()
                true
            }
            R.id.action_search -> {
                val intent3 = Intent(this, UsersActivity::class.java)
                startActivity(intent3)
                true
            }
            R.id.action_signin -> {
                val intent1 = Intent(this, MainActivity::class.java)
                startActivity(intent1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun getAndShowMyMessages() {

        val services = ApiUtils.getMessagesService()
        val getMyMessagesCall = services.getMessagesByUser(user)
            getMyMessagesCall.enqueue(object : Callback<List<Message>> {
                override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                    Log.d("message", response.raw().toString())
                    if (response.isSuccessful) {
                        val allMessages = response.body()!!
                        Log.d("message", allMessages.toString())
                        populateRecycleView(allMessages)
                    } else {
                        val message = "Problem " + response.code() + " " + response.message()
                        Log.d("message", "the problem is: $message")
                    }
                }
                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                    Log.e("message", t.message!!)
                }
            })
    }

    private fun populateRecycleView(allMessages: List<Message>) {

        ProfileMessageRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewProfileMessageAdapter(this, allMessages)
        ProfileMessageRecyclerView.setAdapter(adapter)
        adapter.setClickListener { view: View?, position: Int, item: Message ->
            //Log.d(AllMessagesActivity.MESSAGE, "item is: $item")

            Log.d("users", "item is: $item")
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra(CommentActivity.MESSAGE, item)
            startActivity(intent)
           /* adapter.setClickListener { view: View?, position: Int, item: Message ->
                theMessage = item
                if (position >= 0) {
                    ProfileLayoutComment.setVisibility(View.VISIBLE)
                }*/
           // };
        }
    }

   /* fun getAllComments(){
        val Id: Int = theMessage!!.getId()
        Log.d("comment", "the message id is: $Id")
        val services = ApiUtils.getMessagesService()
        val getAllCommentsCall = services.getCommentById(Id)
        Log.d("comment", "calling all the messge: $getAllCommentsCall")

        getAllCommentsCall.enqueue(object : Callback<List<Comments>> {
            override fun onResponse(call: Call<List<Comments>>, response: Response<List<Comments>>) {
                Log.d("comment", "the response is: " + response.raw().toString())
                if (response.isSuccessful) {
                    val allComments = response.body()!!
                    Log.d("comment", "list of all comments: $allComments")
                    populateRecycleViewComment(allComments)
                } else {
                    val message = "Problem " + response.code() + " " + response.message()
                    Log.d("comment", "problem showing: $message")
                }
            }

            override fun onFailure(call: Call<List<Comments>>, t: Throwable) {
                TODO("Not yet implemented")
                Log.e("comment", t.message!!)
            }
        })

    }

    fun seeAllComments() {
        ProfileLayoutComment.setVisibility(View.VISIBLE)
        getAllComments()

    }


    private fun populateRecycleViewComment(allComments: List<Comments>) {

        ProfileCommentRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewProfileCommentAdapter(this, allComments)
        ProfileCommentRecyclerView.setAdapter(adapter)

        /*adapter.setClickListener(RecyclerViewCommentAdapter.ItemClickListener<*> { view: View?, position: Int, item: Comments -> theComment = item
            if (position >= 0) {
            //DeleteComment(position)
            Log.d("delete", "position is: $position")
            Log.d("delete", "the comment to delete is:  $item")*/
        //}
        //})
    }*/
}
