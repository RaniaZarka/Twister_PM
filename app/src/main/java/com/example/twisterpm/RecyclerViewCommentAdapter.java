package com.example.twisterpm;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerViewCommentAdapter<T> extends RecyclerView.Adapter<RecyclerViewCommentAdapter.MyViewHolder> {
    private static final String LOG_TAG = "Messages";
    private final List<T> data;
    private RecyclerViewCommentAdapter.OnItemClickListener<T> onItemClickListener;
    private final int viewId = View.generateViewId();

    public RecyclerViewCommentAdapter(List<T> data) {
        this.data = data;
        Log.d(LOG_TAG, data.toString());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow, parent,false);
        Log.d(LOG_TAG, view.toString());
        //RecyclerViewCommentAdapter.MyViewHolder vh = new RecyclerViewCommentAdapter.MyViewHolder(v);
       // return vh;
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCommentAdapter.MyViewHolder holder, int position) {
        T dataItem = data.get(position);
        Log.d(LOG_TAG, "onBindViewHolder " + data.toString());
        holder.textView.setText(dataItem.toString());
        Log.d(LOG_TAG, "onBindViewHolder called " + position);
    }

    @Override
    public int getItemCount() {
        int count = data.size();
        Log.d(LOG_TAG, "getItemCount called: " + count);
        return count;
    }

    void setOnItemClickListener(RecyclerViewCommentAdapter.OnItemClickListener<T> itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //final TextView view;
        TextView textView;
        ImageButton imageButton;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            //view = itemView.findViewById(viewId);
            //view.setOnClickListener(this);
            textView = itemView.findViewById(R.id.textView);
            imageButton = itemView.findViewById(R.id.imageButton);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
            }
        }
    }
}


