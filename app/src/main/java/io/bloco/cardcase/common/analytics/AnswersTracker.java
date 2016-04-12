package io.bloco.cardcase.common.analytics;

import android.content.Context;
import android.support.annotation.Nullable;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import java.util.Map;

public class AnswersTracker implements AnalyticsTracker {

  @Override public void init(Context context) {
  }

  @Override public void trackEvent(String eventName, @Nullable Map<String, String> eventParams) {
    CustomEvent event = new CustomEvent(eventName);

    if (eventParams != null) {
      for (Map.Entry<String, String> param : eventParams.entrySet()) {
        event.putCustomAttribute(param.getKey(), param.getValue());
      }
    }

    Answers.getInstance().logCustom(event);
  }

  @Override public void terminate() {
  }
}
