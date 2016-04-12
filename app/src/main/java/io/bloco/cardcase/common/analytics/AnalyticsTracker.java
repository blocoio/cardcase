package io.bloco.cardcase.common.analytics;

import android.content.Context;
import android.support.annotation.Nullable;
import java.util.Map;

public interface AnalyticsTracker {
  void init(Context context);

  void trackEvent(String event, @Nullable Map<String, String> eventParams);

  void terminate();
}
