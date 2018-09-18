package com.example.android.preciousplastic.adaptors;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
        import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.fragments.BaseFragment;
import com.example.android.preciousplastic.imgur.ImgurAccessResponse;
import com.example.android.preciousplastic.imgur.ImgurAsyncGenericTask;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.imgur.ImgurRequestsGenerator;
import com.example.android.preciousplastic.utils.PPSession;
import com.squareup.picasso.Picasso;

        import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by obit91 on 5/22/2018.
 */

public class ImgurRecyclerAdaptor extends RecyclerView.Adapter<ImgurRecyclerAdaptor.ViewHolder>{

    private static ClickListener clickListener;

    private List<ImgurBazarItem> mDataSource;
    private Context mContext;

    public ImgurRecyclerAdaptor(Context context, List<ImgurBazarItem> dataSources) {
        mDataSource = dataSources;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    @Override
    public ImgurRecyclerAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bazar_rv_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(mDataSource.get(position).getTitle());
        Picasso.with(mContext).load(mDataSource.get(position).getLink()).resize(120, 60).into(holder.image);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView text;
        ImageView image;
        Button deleteButton;
        Button shareButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            text = itemView.findViewById(R.id.bazar_ri_txt);
            image = itemView.findViewById(R.id.bazar_ri_img);

            deleteButton = itemView.findViewById(R.id.bazar_ri_delete);
            deleteButton.setOnClickListener(this);
            shareButton = itemView.findViewById(R.id.bazar_ri_share);
            shareButton.setOnClickListener(this);

            setOptionsVisible(false);
        }

        public void setOptionsVisible(boolean visible) {
            float alphaValue;
            int visibility;

            if (visible) {
                alphaValue = 0.2f;
                visibility = View.VISIBLE;
            } else {
                alphaValue = 1f;
                visibility = View.INVISIBLE;
            }

            text.setAlpha(alphaValue);
            image.setAlpha(alphaValue);

            deleteButton.setClickable(visible);
            deleteButton.setVisibility(visibility);

            shareButton.setClickable(visible);
            shareButton.setVisibility(visibility);
        }

        /**
         * Removes the item from the bazar view, the bazar itself (DB) and imgur.
         * @param position item to remove.
         */
        private void performItemRemoval(int position) {
            clickListener.removedItem(this);
            ImgurBazarItem bazarItem = mDataSource.get(position);
            mDataSource.remove(position);
            ImgurRecyclerAdaptor.this.notifyDataSetChanged();
            final User currentUser = PPSession.getCurrentUser();
            currentUser.removeBazarItem(bazarItem);
        }

        /**
         * Verifies that the user wants to remove the requested item.
         * @param position item to remove.
         */
        private void verifyItemRemoval(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Are you sure you want to remove the item from the bazar?");
//            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    performItemRemoval(position);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private void shareItem(View v) {

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
            switch (v.getId()) {
                case R.id.bazar_ri_delete:
                    verifyItemRemoval(getAdapterPosition());
                    break;
                case R.id.bazar_ri_share:
                    shareItem(v);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            setOptionsVisible(true);
            return false;
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
        void removedItem(ViewHolder viewHolderRemoved);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ImgurRecyclerAdaptor.clickListener = clickListener;
    }
}