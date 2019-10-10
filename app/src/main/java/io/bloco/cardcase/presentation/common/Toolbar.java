package io.bloco.cardcase.presentation.common;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bloco.cardcase.R;

public class Toolbar extends androidx.appcompat.widget.Toolbar {

  @BindView(R.id.toolbar_title) TextView title;
  @BindView(R.id.toolbar_icon_start) ImageButton iconStart;
  @BindView(R.id.toolbar_icon_end) ImageButton iconEnd;

  public Toolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.view_toolbar, this);
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
