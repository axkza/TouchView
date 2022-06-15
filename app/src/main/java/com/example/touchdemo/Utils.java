package com.example.touchdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;

public class Utils {
    public static float dp2px(int dpValue) {
        return (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize.y;
    }
}
