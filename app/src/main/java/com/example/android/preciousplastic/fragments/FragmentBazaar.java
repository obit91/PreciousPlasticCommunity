package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.adaptors.ImgurRecyclerAdaptor;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.example.android.preciousplastic.adaptors.ImgurRecyclerAdaptor.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentBazaar.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentBazaar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBazaar extends BaseFragment
{
    private static final String TAG = "FRAGMENT_BAZAR";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private ImgurRecyclerAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mButton;
    private List<ImgurBazarItem> mImages = new ArrayList<>();

    private List<ViewHolder> mLongClickedItems = new ArrayList<>();

    public FragmentBazaar()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentBazaar.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBazaar newInstance(String param1, String param2)
    {
        FragmentBazaar fragment = new FragmentBazaar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View inflate = inflater.inflate(R.layout.fragment_bazaar, container, false);

        // Inflate the layout for this fragment
        initRecyclerView(inflate);

//        ImgurAsyncGetAlbum request = new ImgurAsyncGetAlbum();
//        request.delegate = FragmentBazaar.this;
//        request.execute();

        retrieveMyItems();

        return inflate;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    private void initRecyclerView(View layoutView) {
        mRecyclerView = layoutView.findViewById(R.id.bazar_recycleview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ImgurRecyclerAdaptor(getContext(), mImages);
        mRecyclerView.setAdapter(mAdapter);

        initAdapterListeners();
    }

    private void initAdapterListeners() {
        mAdapter.setOnItemClickListener(new ImgurRecyclerAdaptor.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("short click", "onItemClick position: " + position);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Log.d("long click", "onItemLongClick pos = " + position);
                final ViewHolder itemViewHolder = (ViewHolder)mRecyclerView.findViewHolderForAdapterPosition(position);
                mLongClickedItems.add(itemViewHolder);
            }

            @Override
            public void removedItem(ViewHolder viewHolderRemoved) {
                mLongClickedItems.remove(viewHolderRemoved);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Retrieves all of the connected user's items from the bazar.
     */
    public void retrieveMyItems(){
        final List<ImgurBazarItem> items = new ArrayList<>();
        ValueEventListener itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImgurBazarItem imgurBazarItem;
                for (DataSnapshot itemBazarSnapShot : dataSnapshot.getChildren()) {
                    imgurBazarItem = itemBazarSnapShot.getValue(ImgurBazarItem.class);
                    String msg = "retrieveMyItems: <title, date> = <%s><%s>";
                    Log.i(TAG, String.format(msg, imgurBazarItem.getTitle(), imgurBazarItem.getDatetime()));
                    items.add(imgurBazarItem);
                    updateItems(items);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.e(TAG, "retrieveMyItems:onFirebaseCancelled", databaseError.toException());
            }
        };
        final String nickname = PPSession.getCurrentUser().getNickname();
        final DatabaseReference child = PPSession.getUsersTable().child(nickname).child("workspace").child("itemsOnSale");
        child.addListenerForSingleValueEvent(itemsListener);
    }

    /**
     * Builds the recycler view from scratch.
     * @param items a list of imgur items.
     */
    private void updateItems(List<ImgurBazarItem> items) {
        mImages.removeAll(mImages);
        mImages.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Pressing the back button when an item has been long-clicked will result in returning it to it's natural state.
     * Otherwise - we return to the home screen.
     * @return true if we have overridden the back press.
     */
    @Override
    public boolean onBackPressed() {
        if (mLongClickedItems.size() == 0) {
            return false;
        }
        for (ViewHolder itemViewHolder : mLongClickedItems) {
            itemViewHolder.setOptionsVisible(false);
        }
        mLongClickedItems.clear();
        return true;
    }
}
