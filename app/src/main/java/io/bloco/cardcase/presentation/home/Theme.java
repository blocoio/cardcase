package io.bloco.cardcase.presentation.home;

import android.support.design.widget.CoordinatorLayout;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        private int backgroundColor;
        private int textColor;
        private String textSize;
        private int buttonColor;



        static ThemeTypes currentTheme = ThemeTypes.DefaultTheme; /// default Green

        int setTheme() {
            switch (currentTheme) {
                case Red: {
                    this.backgroundColor = 0xff0080ff;
                    this.textColor = 0xffff7f00;
                    this.textSize = "12sp";
                    this.buttonColor = 0xffff7f00;
                }
                case Green:
                    //                return 0xf00ff000;
            }
            return 0;
        }
        public int getViewBackgroundColor(){
            return backgroundColor;
            //        return 0xffff7f00;
        }

        public String getViewFontSize(){
            return textSize;
            //        return "12sp";
        }

        public int getViewTextColor(){
            return textColor;
            //        return 0xffff7f00;
        }


    static public void setTypeTheme(){
        currentTheme = ThemeTypes.Red;
    }

        public View viewEditor(View view, CoordinatorLayout lay){
//            currentTheme = ThemeTypes.Red;
            setTheme();
            //view.setBackgroundColor(this.getViewBackgroundColor());
            lay.setBackgroundResource(R.drawable.black_theme);
//view.getLayoutParams().l/
//            CoordinatorLayout ll = (CoordinatorLayout)findViewById(R.id.coord);
//            ll.setBackgroundColor(0xffff7f00);

            if (view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++  ){
                    View child = vg.getChildAt(i);
                    viewEditor(child, lay);
                }
            }
            else if (view instanceof TextView){
                ((TextView) view).setTextColor(this.getViewTextColor());
            }
            else if (view instanceof Button){
                ;
            }
            else if (view instanceof CoordinatorLayout){
//                view.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.black_theme);
            }
            return view;
        }


}
