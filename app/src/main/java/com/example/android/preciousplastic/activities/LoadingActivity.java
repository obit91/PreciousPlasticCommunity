package com.example.android.preciousplastic.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.preciousplastic.R;

import java.util.ArrayList;
import java.util.Random;

public class LoadingActivity extends AppCompatActivity {
    private int MAX_NUM_OF_LOADING_IMAGES = 4;
    private int MAX_NUM_OF_QUOTES = 4;


    private ArrayList<Integer> loadingScreenImages;
    private Integer chosenImageIndex;
    private ImageView chosenImage;
    private TextView quote;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        random = new Random();
        randomize_load_image();
        randomize_quote();
    }

    private void randomize_quote() {
        quote = (TextView) findViewById(R.id.fragment_transition_tv_quote);
    }


    private void randomize_load_image() {
        loadingScreenImages = new ArrayList<>();
        chosenImage = (ImageView) findViewById(R.id.fragment_transition_iv);
        // add 4 loading screens.
        loadingScreenImages.add(R.drawable.extrusion_png);
        loadingScreenImages.add(R.drawable.compression_png);
        loadingScreenImages.add(R.drawable.injection_png);
        loadingScreenImages.add(R.drawable.shredder_png);

        chosenImageIndex = random.nextInt(MAX_NUM_OF_LOADING_IMAGES - 1);

        chosenImage.setImageResource(chosenImageIndex);
    }


}
