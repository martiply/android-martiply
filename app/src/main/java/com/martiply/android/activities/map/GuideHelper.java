package com.martiply.android.activities.map;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import com.andrognito.flashbar.Flashbar;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.martiply.android.R;
import com.martiply.android.view.PassTouchEventToolbar;
import jonathanfinerty.once.Once;

public class GuideHelper {


    private static final String GUIDE_HOW_TO_SEARCH  = "GUIDE_HOW_TO_SEARCH";
    private static final String GUIDE_ITEM_TEXT_IMAGE = "GUIDE_ITEM_TEXT_IMAGE";

    private static void build(TapTarget t) {
        t.outerCircleColor(android.R.color.black)          // Specify a color for the outer circle
            .outerCircleAlpha(0.95f)                        // Specify the alpha amount for the outer circle
            .titleTextSize(25)                             // Specify the size (in sp) of the title text
            .titleTextColor(android.R.color.white)         // Specify the color of the title text
            .descriptionTextSize(18)                       // Specify the size (in sp) of the description text
            .textColor(android.R.color.white)              // Specify a color for both the title and description text
            .textTypeface(Typeface.SANS_SERIF)             // Specify a typeface for the text
            .dimColor(android.R.color.darker_gray)         // If set, will dim behind the view with 30% opacity of the given color
            .drawShadow(true)                              // Whether to draw a drop shadow or not
            .cancelable(false)                             // Whether tapping outside the outer circle dismisses the view
            .tintTarget(true)                              // Whether to tint the target view's color
            .transparentTarget(true);                      // Specify whether the target is transparent (displays the content underneath)
//          .icon(Drawable)                                // Specify a custom drawable to draw as the target
//            .targetRadius(90);                            // Specify the target radius (in dp)
    }

     static void showHowToSearch(Activity activity, View mapAnchor, PassTouchEventToolbar toolbar, DrawerLayout drawerLayout){
        if (Once.beenDone(Once.THIS_APP_VERSION, GUIDE_HOW_TO_SEARCH)){return;}
        Once.markDone(GUIDE_HOW_TO_SEARCH);
        TapTarget target1 = TapTarget.forView(mapAnchor, activity.getString(R.string.step1),  activity.getString(R.string.guide_map_area));
        build(target1.targetRadius(90));
        TapTarget target2 = TapTarget.forToolbarNavigationIcon(toolbar, activity.getString(R.string.step2),  activity.getString(R.string.guide_map_search));
        build(target2.targetRadius(60));
        TapTargetView.showFor(activity, target1,
            new TapTargetView.Listener(){

                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    TapTargetView.showFor(activity, target2 ,
                        new TapTargetView.Listener(){
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                }
            }
        );
    }

    static void showItemTextImage(Activity activity){
        if (Once.beenDone(Once.THIS_APP_VERSION, GUIDE_ITEM_TEXT_IMAGE)){return;}
        Once.markDone(GUIDE_ITEM_TEXT_IMAGE);
        new Flashbar.Builder(activity)
            .gravity(Flashbar.Gravity.BOTTOM)
            .title(R.string.one_more_thing)
            .titleSizeInSp(25)
            .messageTypeface(Typeface.SANS_SERIF)
            .message(R.string.guide_item_text_image)
            .messageSizeInSp(15)
            .backgroundColorRes(android.R.color.black)
            .showOverlay()
            .primaryActionText(android.R.string.ok)
            .primaryActionTapListener(Flashbar::dismiss)
            .build()
            .show();
    }

}
