package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.presentation.home.SimpleTextWatcher;

public class SearchToolbar extends android.support.v7.widget.Toolbar {

  @Bind(R.id.toolbar_search_field) EditText field;

  private SearchListener listener;

  public SearchToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);

    field.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(String newText) {
        if (listener != null) {
          listener.onSearchQuery(newText);
        }
      }
    });
  }

  @OnClick(R.id.toolbar_search_close) public void onCloseClicked() {
    clear();

    if (listener != null) {
      listener.onSearchClosed();
    }
  }

  public void setListener(SearchListener listener) {
    this.listener = listener;
  }

  public void clear() {
    field.setText("");
    field.postDelayed(new Runnable() {
      @Override
      public void run() {
        InputMethodManager keyboard = (InputMethodManager)
            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(field.getWindowToken(), 0);
      }
    }, 100);
  }

  public void focus() {
    field.requestFocus();
    field.postDelayed(new Runnable() {
      @Override
      public void run() {
        InputMethodManager keyboard = (InputMethodManager)
            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(field, 0);
      }
    }, 100);
  }

  public interface SearchListener {
    void onSearchClosed();
    void onSearchQuery(String query);
  }
}