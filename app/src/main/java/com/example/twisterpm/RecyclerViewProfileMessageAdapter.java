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

public class RecyclerViewProfileMessageAdapter extends RecyclerView.Adapter<RecyclerViewProfileMessageAdapter.MyViewHolder> {
    private static final String LOG_TAG = "Messages";
    private List<Message> data;
    private final LayoutInflater mInflater;
    private RecyclerViewProfileMessageAdapter.ItemClickListener mClickListener;


    public RecyclerViewProfileMessageAdapter(Context context, List<Message> data) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        Log.d(LOG_TAG, data.toString());
    }

    @NonNull
    @Override
    public RecyclerViewProfileMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_message_recycler, parent, false);
        Log.d(LOG_TAG, view.toString());
        return new RecyclerViewProfileMessageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewProfileMessageAdapter.MyViewHolder holder, int position) {
        Message message = data.get(position);
        Log.d(LOG_TAG, "onBindViewHolder " + data.toString());
        String comments = Integer.toString(message.getTotalComments());
        holder.ContenttextView.setText(message.getContent());
        holder.TotalTextView.setText(comments+ " comments");
        Log.d(LOG_TAG, "onBindViewHolder called " + position);
    }

    @Override
    public int getItemCount() {
        int count = data.size();
        Log.d(LOG_TAG, "getItemCount called: " + count);
        return count;
    }

    void setClickListener(RecyclerViewProfileMessageAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener<T> {
        void onItemClick(View view, int position, Message message);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView  ContenttextView, TotalTextView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ContenttextView = itemView.findViewById(R.id.profilerecyclerContent);
            TotalTextView = itemView.findViewById(R.id.profilerecyclerTotalComments);
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




