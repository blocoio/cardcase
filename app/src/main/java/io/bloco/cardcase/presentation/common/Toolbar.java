package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.R;

public class Toolbar extends android.support.v7.widget.Toolbar {

  @Bind(R.id.toolbar_title) TextView title;
  @Bind(R.id.toolbar_icon_start) ImageButton iconStart;
  @Bind(R.id.toolbar_icon_end) ImageButton iconEnd;

  public Toolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  @Override public void setTitle(@StringRes int titleRes) {
    String title = getResources().getString(titleRes);
    setTitle(title);
  }

  @Override public void setTitle(CharSequence titleStr) {
    title.setText(titleStr);
  }

  public void setStartButton(@DrawableRes int drawableRes, @StringRes int stringRes,
      OnClickListener listener) {
    String description = getResources().getString(stringRes);
    iconStart.setContentDescription(description);
    iconStart.setImageResource(drawableRes);
    iconStart.setOnClickListener(listener);
    iconStart.setVisibility(View.VISIBLE);
  }

  public void removeStartButton() {
    iconStart.setVisibility(View.GONE);
  }

  public void setEndButton(@DrawableRes int drawableRes, @StringRes int stringRes,
      OnClickListener listener) {
    String description = getResources().getString(stringRes);
    iconEnd.setContentDescription(description);
    iconEnd.setImageResource(drawableRes);
    iconEnd.setOnClickListener(listener);
    iconEnd.setVisibility(View.VISIBLE);
  }

  public void removeEndButton() {
    iconEnd.setVisibility(View.GONE);
  }
}
