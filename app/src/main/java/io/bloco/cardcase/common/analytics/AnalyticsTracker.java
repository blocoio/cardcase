package io.bloco.cardcase.common.analytics;

import android.content.Context;
import androidx.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public interface AnalyticsTracker {
  void init(Context context);

  void trackEvent(String event, @Nullable Map<String, String> eventParams);
}
