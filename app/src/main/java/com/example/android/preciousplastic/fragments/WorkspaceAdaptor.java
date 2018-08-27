package com.example.android.preciousplastic.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.activities.MapConstants;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class WorkspaceAdaptor extends RecyclerView.Adapter<WorkspaceAdaptor.WorkspaceViewHolder> {

    LayoutInflater mInflator;
    ArrayList<LinkedTreeMap<String, Object>> workspacesData;

    public WorkspaceAdaptor(LayoutInflater inflater, ArrayList dataset){
        mInflator = inflater;
        workspacesData = dataset;
        Log.i("WorkspaceAdaptor", "WorkspaceAdaptor");

    }

    public static class WorkspaceViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public WorkspaceViewHolder(View v) {
            super(v);
            Log.i("WorkspaceAdaptor", "WorkspaceViewHolder");
            layout = (LinearLayout) v;
        }
    }

    /**
     * Replace Image, Name, Desc & Website link for new workspace.
     * @param holder place new properties in this view
     * @param pos new pos in dataset
     */
    @Override
    public void onBindViewHolder(WorkspaceViewHolder holder, int pos) {
        Log.i("WorkspaceAdaptor", "onBindViewHolder");
        LinkedTreeMap<String, Object> currItem = workspacesData.get(pos);

        // Name of workspace
        TextView nameView = holder.layout.findViewById(R.id.workspace_name);
        String name = (String) currItem.get(MapConstants.MapPinKeys.NAME);
        nameView.setText(name);

        // Description of workspace
        TextView descView = holder.layout.findViewById(R.id.workspace_desc);
        String desc = (String) currItem.get(MapConstants.MapPinKeys.DESC);
        descView.setText(desc);

        // Image and link to website
        ImageButton imgBtn = holder.layout.findViewById(R.id.workspace_img);
        final String website = (String) currItem.get(MapConstants.MapPinKeys.SITE);
        ArrayList<String> imgs = (ArrayList) currItem.get(MapConstants.MapPinKeys.IMGS);
        if (imgs.size() > 0) {
            imgBtn.setImageDrawable(getImage(imgs.get(0)));
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!website.equals("")) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                        PPSession.getContainerContext().startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("Website OnClick error", e.toString());
                    }
                }
            }
        });
    }

    private Drawable getImage(String url){
        return Drawable.createFromPath(url);
    }

    @Override
    public WorkspaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.i("WorkspaceAdaptor", "onCreateViewHolder");
        LinearLayout view = (LinearLayout) mInflator.inflate(R.layout.workspace_recycled_view, parent, false);
        return new WorkspaceViewHolder(view);
    }

    @Override
    public int getItemCount(){
        Log.i("WorkspaceAdaptor count", String.valueOf(workspacesData.size()));
        return workspacesData.size();
    }



}
