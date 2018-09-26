package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.preciousplastic.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.example.android.preciousplastic.utils.ImageUtils.decodeSampledBitmapFromResource;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAboutUs.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAboutUs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAboutUs extends BaseFragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView ohadIV = null;
    private ImageView yoniIV = null;
    private ImageView kerenIV = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentAboutUs()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAboutUs.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAboutUs newInstance(String param1, String param2)
    {
        FragmentAboutUs fragment = new FragmentAboutUs();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);

        ohadIV = v.findViewById(R.id.aboutUs_iv_ohad);
        yoniIV = v.findViewById(R.id.aboutUs_iv_yoni);
        kerenIV = v.findViewById(R.id.aboutUs_iv_keren);

        ohadIV.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.ohad_beltzer, 100, 150));

        yoniIV.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.yonatan_manor, 100, 150));

        kerenIV.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.keren_meron, 100, 150));
//
//        Bitmap yoniImage = BitmapFactory.decodeResource(getResources(), R.drawable.yonatan_manor);
//        compressBitmap(yoniImage);
//        yoniIV.setImageBitmap(yoniImage);
//
//        Bitmap kerenImage = BitmapFactory.decodeResource(getResources(), R.drawable.keren_meron);
//        compressBitmap(kerenImage);
//        kerenIV.setImageBitmap(kerenImage);

        return v;
    }

    private void compressBitmap(Bitmap bitmap) {
        int ratio = 25;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, ratio, out);
        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
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

    @Override
    public boolean onBackPressed() {
        return false;
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
}
