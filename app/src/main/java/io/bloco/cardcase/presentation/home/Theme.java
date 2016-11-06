package io.bloco.cardcase.presentation.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import io.bloco.cardcase.R;
import io.bloco.cardcase.presentation.Preferences;
import io.bloco.cardcase.presentation.common.Toolbar;

/**
 * Created by liana on 10/12/16.
 */

public class Theme {

    private static void updateColorsForCurrentTheme() {
        switch (currentTheme) {
            case Default:
                currentBackgroundColor = 0xff617d8a;
                accentColor = 0xff00BFA5;
                primaryTextColor = 0xff1c1c1c;
                textIconColor = 0xffFFFFFF;
                darkPrimaryColor = 0xff3a4b53;
                break;
            case Red:
                currentBackgroundColor = 0xffFF5722;
                accentColor = 0xffFFC107;
                primaryTextColor = 0xff212121;
                textIconColor = 0xffFFFFFF;
                darkPrimaryColor = 0xffE64A19;
                break;
            case Blue:
                currentBackgroundColor = 0xff3F51B5;
                accentColor = 0xff03A9F4;
                primaryTextColor = 0xff212121;
                textIconColor = 0xffFFFFFF;
                darkPrimaryColor = 0xff303F9F;
                break;
        }
    }

    public enum ThemeType {
        Default,
        Red,
        Blue;

        public static ThemeType fromInt(int num) {
            if (num == 0) {
                return Default;
            } else if (num == 1) {
                return Red;
            } else if (num == 2) {
                return Blue;
            }
            return Default;
        }

        public static String[] names() {
            ThemeType[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }

    }

    private static ThemeType currentTheme = ThemeType.Default;

    private static int currentBackgroundColor;
    private static int accentColor;
    private static int darkPrimaryColor;
    private static int primaryTextColor;
    private static int textIconColor;

    public static void setCurrentTheme(ThemeType currentTheme) {
        Theme.currentTheme = currentTheme;
        updateColorsForCurrentTheme();
    }

    public static void applyThemeFor(View view, Context ctx) {

        String themeStr = Preferences.getStringForKeyInContext(Preferences.THEME_KEY, ctx);
        if (themeStr != null) {
            currentTheme = ThemeType.valueOf(themeStr);
        }
        updateColorsForCurrentTheme();

        view.setBackgroundColor(currentBackgroundColor);

        for (Button butt : getViewsFromViewGroup(view.findViewById(android.R.id.content), Button.class)) {
            butt.setBackgroundColor(accentColor);
            butt.setTextColor(textIconColor);
        }

        for (FloatingActionButton butt : getViewsFromViewGroup(view.findViewById(android.R.id.content), FloatingActionButton.class)) {
            butt.setBackgroundColor(accentColor);
            butt.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }

        for (Toolbar toolbar : getViewsFromViewGroup(view.findViewById(android.R.id.content), Toolbar.class)) {
            toolbar.setBackgroundColor(darkPrimaryColor);
        }

        // Fix Search Toolbar on Home screen
        if (view.findViewById(R.id.toolbar_search) != null) {
            view.findViewById(R.id.toolbar_search).setBackgroundColor(darkPrimaryColor);
        }

        // All text fields
        for (EditText editText : getViewsFromViewGroup(view.findViewById(android.R.id.content), EditText.class)) {
            editText.setBackgroundColor(darkPrimaryColor);
            editText.getBackground().setColorFilter(darkPrimaryColor, PorterDuff.Mode.SRC_ATOP);
        }

        // Text fields on User Card screen
        for (EditText editText : getViewsFromViewGroup(view.findViewById(R.id.user_card), EditText.class)) {
            editText.setBackgroundColor(textIconColor);
            editText.getBackground().setColorFilter(textIconColor, PorterDuff.Mode.SRC_ATOP);
        }

        // Transition overlay view on Home screen
        if (view.findViewById(R.id.home_transition_overlay) != null) {
            view.findViewById(R.id.home_transition_overlay).setBackgroundColor(currentBackgroundColor);
        }

    }


    // Helpers

    private static <T> ArrayList<T> getViewsFromViewGroup(View root, Class<T> clazz) {
        ArrayList<T> result = new ArrayList<T>();
        for (View view : getAllViewsFromRoots(root))
            if (clazz.isInstance(view))
                result.add(clazz.cast(view));
        return result;
    }

    private static ArrayList<View> getAllViewsFromRoots(View...roots) {
        ArrayList<View> result = new ArrayList<View>();
        for (View root : roots)
            getAllViews(result, root);
        return result;
    }

    private static void getAllViews(ArrayList<View> allviews, View parent) {
        allviews.add(parent);
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                getAllViews(allviews, viewGroup.getChildAt(i));
        }
    }

}
