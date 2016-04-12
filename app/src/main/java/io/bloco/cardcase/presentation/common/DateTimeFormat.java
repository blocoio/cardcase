package io.bloco.cardcase.presentation.common;

import android.content.res.Resources;
import android.text.format.DateUtils;
import io.bloco.cardcase.R;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DateTimeFormat {

  private static final int SECOND_MILLIS = 1000;
  private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
  private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

  private final Resources mResources;

  @Inject public DateTimeFormat(Resources resources) {
    mResources = resources;
  }

  public String getRelativeTimeSpanString(Date timestamp) {
    long time = timestamp.getTime();
    long now = System.currentTimeMillis();
    long diff = now - time;

    if (diff < MINUTE_MILLIS) {
      return mResources.getString(R.string.time_just_now);
    } else if (diff < 50 * MINUTE_MILLIS) {
      int minutes = Math.round(diff / (float) MINUTE_MILLIS);
      return mResources.getString(R.string.time_minutes, minutes);
    } else if (diff < 24 * HOUR_MILLIS) {
      int hours = Math.round(diff / (float) HOUR_MILLIS);
      return mResources.getString(R.string.time_hours, hours);
    } else {
      return DateUtils.getRelativeTimeSpanString(time).toString();
    }
  }
}

