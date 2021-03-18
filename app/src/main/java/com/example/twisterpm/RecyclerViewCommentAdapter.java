package com.example.twisterpm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.MyViewHolder> {
    private static final String LOG_TAG = "Comments";
    private List<Comments> data;
    private final LayoutInflater mInflater;
    private RecyclerViewCommentAdapter.ItemClickListener mClickListener;


    public RecyclerViewCommentAdapter(Context context, List<Comments> data) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        Log.d(LOG_TAG, data.toString());
    }

    @NonNull
    @Override
    public RecyclerViewCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comments_recycler, parent, false);
        Log.d(LOG_TAG, view.toString());
        return new RecyclerViewCommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCommentAdapter.MyViewHolder holder, int position) {
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

    void setClickListener(RecyclerViewCommentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener<T> {
        void onItemClick(View view, int position, Comments comment);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView UsertextView, ContenttextView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            UsertextView = itemView.findViewById(R.id.recyclerCommentUser);
            ContenttextView = itemView.findViewById(R.id.recyclerCommentContent);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));

            }
        }
    }
}





