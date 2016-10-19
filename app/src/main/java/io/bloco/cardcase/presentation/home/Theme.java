package io.bloco.cardcase.presentation.home;

import android.support.design.widget.CoordinatorLayout;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bloco.cardcase.R;
import io.bloco.cardcase.presentation.common.Toolbar;

/**
 * Created by liana on 10/12/16.
 */

public class Theme {


    enum ThemeTypes {DefaultTheme, Red, Green}

    /**
     * Created by liana on 10/12/16.
     */

    static private int backgroundColor;
    static int backImage;
    static int textColor;
    static private String textSize;
    static private View myView;

//        static private int buttonColor;


    static ThemeTypes currentTheme = ThemeTypes.DefaultTheme; /// default Green

    static int setTheme() {
        switch (currentTheme) {
            case Red: {
                backgroundColor = 0xff0080ff;
                textColor = 0xff00ff00;
//                textSize = "15sp";
                backImage = R.drawable.black_theme;
                break;
            }
            case Green: {
                break;
            }
            case DefaultTheme: {
                backgroundColor = 0xffe70f0f;
                textColor = 0xff000000;
                backgroundColor = 0xf607D8B;
                backImage = 0;
                break;
            }
        }
        return 0;
    }

    static public int getBackImage() {
        return backImage;
    }

    public int getViewBackgroundColor() {
        return backgroundColor;
        //        return 0xffff7f00;
    }

    public String getViewFontSize() {
        return textSize;
        //        return "12sp";
    }

    static public int getViewTextColor() {
        return textColor;
    }


    static public void viewLayTheme(View myView, FrameLayout lay) {
        setTheme();
        lay.setBackgroundResource(getBackImage());
        viewEditor(myView);
    }

    static public void setTypeTheme() {
        //CoordinatorLayout lay
        if (currentTheme == ThemeTypes.Red) {
            currentTheme = ThemeTypes.DefaultTheme;
        } else {
            currentTheme = ThemeTypes.Red;
        }
    }

    static public View viewEditor(View view) {

        myView = view;

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                viewEditor(child);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(getViewTextColor());
        } else if (view instanceof Button) {

        } else if (view instanceof CoordinatorLayout) {
        }
        return view;
    }


}
