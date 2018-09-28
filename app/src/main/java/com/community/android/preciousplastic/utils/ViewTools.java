package com.community.android.preciousplastic.utils;

import android.widget.Spinner;
import android.widget.TextView;

public class ViewTools {

    /**
     * Returns true if the textView was not updated with any input.
     * @param textView textView to check.
     * @return true if the textView was not updated.
     */
    public static boolean isTextViewNull(TextView textView) {
        return textView.getText().toString().matches("");
    }
}
