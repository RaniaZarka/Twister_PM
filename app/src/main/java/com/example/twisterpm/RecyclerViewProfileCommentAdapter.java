package com.example.twisterpm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewProfileCommentAdapter extends RecyclerView.Adapter<RecyclerViewProfileCommentAdapter.MyViewHolder> {
    private static final String LOG_TAG = "Comments";
    private List<Comments> data;
    private final LayoutInflater mInflater;
   // private RecyclerViewProfileCommentAdapter.ItemClickListener mClickListener;

    public RecyclerViewProfileCommentAdapter(Context context, List<Comments> data) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        Log.d(LOG_TAG, data.toString());
    }

    @NonNull
    @Override
    public RecyclerViewProfileCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_comments_recycler, parent, false);
        Log.d(LOG_TAG, view.toString());
        return new RecyclerViewProfileCommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewProfileCommentAdapter.MyViewHolder holder, int position) {
        Comments comment = data.get(position);
        Log.d(LOG_TAG, "onBindViewHolder " + data.toString());
        holder.UsertextView.setText(comment.getUser());
        holder.ContenttextView.setText(comment.getContent());
        Log.d(LOG_TAG, "onBindViewHolder called " + position);
    }

    @Override
    public int getItemCount() {
        int count = data.size();
        Log.d(LOG_TAG, "getItemCount called: " + count);
        return count;
    }
    Comments getItem(int id){

        return data.get(id);
    }


   /* void setClickListener(RecyclerViewProfileCommentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener<T> {
        void onItemClick(View view, int position, Comments comment);
        //void onDeleteClick(View view, int position, Comments comment);
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView  ContenttextView, UsertextView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ContenttextView = itemView.findViewById(R.id.profileCommentContent);
            UsertextView=itemView.findViewById(R.id.profileCommentUser);

        }

    }
}






