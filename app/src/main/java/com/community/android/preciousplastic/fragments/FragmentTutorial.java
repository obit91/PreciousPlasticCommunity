package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.utils.PPSession;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class FragmentTutorial extends BaseFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "FRAGMENT_TUTORIAL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button profileBtn = null;
    private Button tradeBtn = null;
    private Button rankBtn = null;
    private Button trackHistBtn = null;
    private Button bazaarBtn = null;
    private Button workspacesBtn = null;
    private Button mapBtn = null;
    private Button mNextBtn = null;
    private Button mBackToTutBtn = null;
    private ImageView mTutImage = null;
    TextView mIntroText = null;
    TextView mInspirationText = null;

    private int currentSlidePos = 0;

    private LinkedList<View> mainViews = new LinkedList<>();
    private List<Slide> tutorialSlides = new LinkedList<>();

    private static final Integer TWO_SECONDS_TO_MILLIS = 2 * 1000;
    private static final Integer FIVE_SECONDS_TO_MILLIS = 5 * 1000;

    /**
     * Contains a slide resource id and duration time (in milliseconds);
     */
    private static class Slide {
        private Integer slideDrawableId;
        private Integer showDurationMillis;

        public Slide(Integer slideDrawableId, Integer showDurationMillis) {
            this.slideDrawableId = slideDrawableId;
            this.showDurationMillis = showDurationMillis;
        }

        public Integer getSlideDrawableId() {
            return slideDrawableId;
        }

        public Integer getShowDurationMillis() {
            return showDurationMillis;
        }
    }

    /**
     * A runnable class which shows a slide.
     */
    private class SlideTask implements Runnable {

        private Slide innerSlide;

        private SlideTask(Slide innerSlide) {
            this.innerSlide = innerSlide;
        }

        @Override
        public void run() {
            if (innerSlide != null) {
                populateSlide(innerSlide);
            } else {
                setMainViewsVisible(true);
            }
        }
    }

    public FragmentTutorial() {
        // Required empty public constructor
    }

    /**
     * Adds all main views to a container.
     */
    private void collectMainViews() {
        mainViews.add(profileBtn);
        mainViews.add(tradeBtn);
        mainViews.add(rankBtn);
        mainViews.add(trackHistBtn);
        mainViews.add(bazaarBtn);
        mainViews.add(workspacesBtn);
        mainViews.add(mapBtn);
        mainViews.add(mNextBtn);
        mainViews.add(mIntroText);
        mainViews.add(mInspirationText);
    }

    /**
     * Generates a list of slides for the tutorial.
     */
    private void generateTutorialSlides() {
        Slide tempSlide;
        tempSlide = new Slide(R.drawable.tutorial_profile_select, TWO_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_profile_histo_plast, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_profile_purchase_points, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_profile_rank_explanation, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_bazaar_select, TWO_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_bazaar_info, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_trade_select, TWO_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_trade_info, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_workspaces_select, TWO_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        tempSlide = new Slide(R.drawable.tutorial_workspaces_info, FIVE_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        if (PPSession.getCurrentUser().isOwner()) {
            tempSlide = new Slide(R.drawable.tutorial_recycle_select, TWO_SECONDS_TO_MILLIS);
            tutorialSlides.add(tempSlide);
            tempSlide = new Slide(R.drawable.tutorial_recycle_info, FIVE_SECONDS_TO_MILLIS);
            tutorialSlides.add(tempSlide);
        }
        tempSlide = new Slide(R.drawable.tutorial_map_select, TWO_SECONDS_TO_MILLIS);
        tutorialSlides.add(tempSlide);
        //TODO: missing map, myworkspace
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTutorial newInstance(String param1, String param2) {
        FragmentTutorial fragment = new FragmentTutorial();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        mIntroText = view.findViewById(R.id.faq_tv_intro);
        mInspirationText = view.findViewById(R.id.faq_tv_inspiration);
        attachButtons(view);
        //todo fix this eventually.. commented out for now.
//        profileBtn.setOnClickListener(this);
//        tradeBtn.setOnClickListener(this);
//        rankBtn.setOnClickListener(this);
//        trackHistBtn.setOnClickListener(this);
//        bazaarBtn.setOnClickListener(this);
//        workspacesBtn.setOnClickListener(this);
//        mapBtn.setOnClickListener(this);
        mBackToTutBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        mBackToTutBtn.setVisibility(View.INVISIBLE);

        attachImageListener(view);

        generateTutorialSlides();
        collectMainViews();
        setMainViewsVisible(true);
        return view;
    }

    private void attachButtons(View view) {
        profileBtn = view.findViewById(R.id.faq_btn_recycle_plastic_for_points);
        tradeBtn = view.findViewById(R.id.faq_btn_trade_for_items);
        rankBtn = view.findViewById(R.id.faq_btn_rank_progression);
        trackHistBtn = view.findViewById(R.id.faq_btn_track_recycling);
        bazaarBtn =  view.findViewById(R.id.faq_btn_bazaar_search);
        workspacesBtn = view.findViewById(R.id.faq_btn_explore_workspaces);
        mapBtn = view.findViewById(R.id.faq_btn_map_long_click);
        mBackToTutBtn = view.findViewById(R.id.faq_btn_to_faq);
        mNextBtn = view.findViewById(R.id.faq_btn_next);
    }

    private void attachImageListener(View view) {
        mTutImage = view.findViewById(R.id.faq_iv_current_tutorial_image);

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        Log.i(TAG, "onFling has been called!");
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Right to Left");
                                if (currentSlidePos + 1 <= tutorialSlides.size() - 1) {
                                    currentSlidePos++;
                                    populateSlide(tutorialSlides.get(currentSlidePos));
                                }
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Left to Right");
                                if (currentSlidePos -1 >= 0) {
                                    currentSlidePos--;
                                    populateSlide(tutorialSlides.get(currentSlidePos));
                                }
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        mTutImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setMainViewsVisible(boolean visible) {
        int visibility;
        int reverseVisibility;
        if (visible) {
            visibility = View.VISIBLE;
            reverseVisibility = View.INVISIBLE;
        } else {
            visibility = View.INVISIBLE;
            reverseVisibility = View.VISIBLE;
        }
        for (View view : mainViews) {
            view.setVisibility(visibility);
            view.setClickable(visible);
        }

        mTutImage.setVisibility(reverseVisibility);
        mTutImage.setClickable(!visible);
        mBackToTutBtn.setVisibility(reverseVisibility);
        mBackToTutBtn.setClickable(!visible);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.faq_btn_recycle_plastic_for_points:
                startSlides();
                break;
            case R.id.faq_btn_trade_for_items:
                break;
            case R.id.faq_btn_rank_progression:
                break;
            case R.id.faq_btn_bazaar_search:
                break;
            case R.id.faq_btn_track_recycling:
                break;
            case R.id.faq_btn_explore_workspaces:
                break;
            case R.id.faq_btn_map_long_click:
                break;
            case R.id.faq_btn_next:
                startSlides();
                break;
            case R.id.faq_btn_to_faq:
                setMainViewsVisible(true);
                break;
            default:
                break;
        }
    }

    /**
     * Shows all tutorial slides.
     */
    private void startSlides() {
//                mTutImage.setImageResource(); //Todo aquire picture and set it
//                profileBtn.setTextColor(R.color.colorPrimaryClickedLink); //Todo change color to clicked

        /*SlideTask slideTask;
        if (tutorialSlides.size() == 0) {
            return;
        }

        setMainViewsVisible(false);

        Slide firstSlide = tutorialSlides.remove(0);
        populateSlide(firstSlide);
        Integer delayDuration = firstSlide.getShowDurationMillis();

        final Handler handler = new Handler();
        for (Slide slide : tutorialSlides) {
            slideTask = new SlideTask(slide);
            handler.postDelayed(slideTask, delayDuration);
            delayDuration += slide.getShowDurationMillis();
        }

        // set a null slide to let final slide end and run a callback to return to the main views.
        slideTask = new SlideTask(null);
        handler.postDelayed(slideTask, delayDuration);*/

//        Picasso.with(getActivity())
//                .load(R.drawable.transperent_black_phone)
//                .fit()
//                .centerCrop()
//                .into(mBlackPhone);

        setMainViewsVisible(false);
        populateSlide(tutorialSlides.get(0));
    }

    private void populateSlide(Slide slide) {

        if (slide == null) {
            return;
        }

        Picasso.with(getActivity())
                .load(slide.getSlideDrawableId())
//                .resize(100, 200)
                .fit()
                .centerCrop()
                .into(mTutImage);
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
