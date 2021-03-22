package com.example.twisterpm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.MyViewHolder> {
    private static final String LOG_TAG = "Comments";
    private List<Comments> data;
    private final LayoutInflater mInflater;
    private RecyclerViewCommentAdapter.ItemClickListener mClickListener;
    private ImageButton button;
    private Context mContext;


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
       /* holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.button);
            }
        });*/
    }

   /* private void showPopupMenu(View view){
        CommentActivity ca = new CommentActivity();
        PopupMenu popup = new PopupMenu(this.mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.delete_comments_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Comment_delete:
                    // ca.commentDelete(getItemId(data.indexOf()));

                        return true;

                    case R.id.Comment_cancel:
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }*/

    @Override
    public int getItemCount() {
        int count = data.size();
        Log.d(LOG_TAG, "getItemCount called: " + count);
        return count;
    }
      Comments getItem(int id){

        return data.get(id);
      }


    void setClickListener(RecyclerViewCommentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener<T> {
        void onItemClick(View view, int position, Comments comment);
        //void onDeleteClick(View view, int position, Comments comment);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView UsertextView, ContenttextView;
        //final ImageView mDelete;
        final ImageButton button;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            UsertextView = itemView.findViewById(R.id.recyclerCommentUser);
            ContenttextView = itemView.findViewById(R.id.recyclerCommentContent);
           // mDelete = itemView.findViewById(R.id.commentImageDelete);
            button= (ImageButton) itemView.findViewById(R.id.commentImageDelete);

            button.setOnClickListener(this);
            //itemView.setOnClickListener(this);
           // mDelete.setOnClickListener(this);
           /*     @Override
                public void onClick(View v){
         if(listener !=null){
    int position = getAdapterPosition();
    if(position !=RecyclerView.NO_POSITION) {
        listener.onItemClick(position)

    }
}*/
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
          // mClickListener.onDeleteClick(getAdapterPosition());
            }
        }
    }
}





