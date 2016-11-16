package io.bloco.cardcase.presentation.common;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MyEditText extends EditText {

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void activate(){
        setCursorVisible(true);
        setFocusableInTouchMode(true);
        setInputType(InputType.TYPE_CLASS_TEXT);
        requestFocus();
    }

    public void deactivate(){
        setInputType(InputType.TYPE_NULL);
        clearFocus();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            deactivate();
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
