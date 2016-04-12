package io.bloco.cardcase.common.analytics;

import android.content.Context;
import android.support.annotation.Nullable;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import io.bloco.cardcase.R;
import java.util.Map;

public class GoogleAnalyticsTracker implements AnalyticsTracker {
  private static final String CATEGORY_KEY = "category";
  private static final String LABEL_KEY = "label";
  private static final String VALUE_KEY = "value";

  private static final String DEFAULT_CATEGORY = "All";

  private Tracker tracker;

  @Override public void init(Context context) {
    GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
    tracker = analytics.newTracker(R.xml.google_analytics_tracker);
  }

  @Override public void trackEvent(String eventName, @Nullable Map<String, String> eventParams) {
    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
    eventBuilder.setAction(eventName);

    if (eventParams != null && eventParams.containsKey(CATEGORY_KEY)) {
      eventBuilder.setCategory(eventParams.get(CATEGORY_KEY));
    } else {
      eventBuilder.setCategory(DEFAULT_CATEGORY);
    }

    if (eventParams != null && eventParams.containsKey(LABEL_KEY)) {
      eventBuilder.setLabel(eventParams.get(LABEL_KEY));
    }

    if (eventParams != null && eventParams.containsKey(VALUE_KEY)) {
      eventBuilder.setLabel(eventParams.get(VALUE_KEY));
    }

    tracker.send(eventBuilder.build());
  }

  @Override public void terminate() {
  }
}
