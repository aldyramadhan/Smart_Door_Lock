package org.d3ifcool.smart.Family;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomAnimation implements ViewPager.PageTransformer {
    private final float MIN_SCALE = 0.70f;
    private final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();
        int pageHight = view.getHeight();

        if (position < -1){
            view.setAlpha(0f);

        }

        else if (position <= 1){
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            if (position < 0){
                view.setTranslationX(horzMargin - vertMargin / 2);
            }else {
                view.setTranslationY(-horzMargin + vertMargin / 2);
            }

            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) *
                            (1 - MIN_ALPHA));
        }

        else {

            view.setAlpha(0f);
        }

    }
}
