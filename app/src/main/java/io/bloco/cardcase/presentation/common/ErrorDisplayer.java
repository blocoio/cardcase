package io.bloco.cardcase.presentation.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ErrorDisplayer {

  private final Context context;
  private final Resources resources;

  @Inject public ErrorDisplayer(Context context, Resources resources) {
    this.context = context;
    this.resources = resources;
  }

  public void show(Activity activity, @StringRes int errorRes) {
    show(activity.findViewById(android.R.id.content), resources.getString(errorRes));
  }

  public void show(View view, @StringRes int errorRes) {
    show(view, resources.getString(errorRes));
  }

  public void show(View view, String errorMessage) {
    Snackbar snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG);
    setTextColor(snackbar);
    snackbar.show();
  }

  private void setTextColor(Snackbar snackbar) {
    View view = snackbar.getView();
    TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
  }
}
