package com.example.android.preciousplastic.imgur;

import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.squareup.picasso.Picasso;

        import java.util.List;

/**
 * Created by obit91 on 5/22/2018.
 */

public class ImgurRecyclerAdaptor extends RecyclerView.Adapter<ImgurRecyclerAdaptor.ViewHolder>{
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
        View view = inflater.inflate(R.layout.lo_bazar_rv_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(mDataSource.get(position).getTitle());
        Picasso.with(mContext).load(mDataSource.get(position).getLink()).resize(120, 60).into(holder.image);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;

        ViewHolder(View view) {
            super(view);
            text = (TextView)view.findViewById(R.id.bazar_ri_txt);
            image = (ImageView)view.findViewById(R.id.bazar_ri_img);
        }
    }
}