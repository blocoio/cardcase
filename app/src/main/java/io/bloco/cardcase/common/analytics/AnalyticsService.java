package io.bloco.cardcase.common.analytics;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnalyticsService {

  private boolean active;
  private List<AnalyticsTracker> trackers;

  @Inject
  public AnalyticsService() {
    active = false;
  }

  public void init(Context context, AnalyticsTracker... trackers) {
    active = true;
    this.trackers = Arrays.asList(trackers);
    for (AnalyticsTracker tracker : trackers) {
      tracker.init(context);
    }
  }

  public void trackEvent(String event) {
    if (active) {
      for (AnalyticsTracker tracker : trackers) {
        tracker.trackEvent(event, null);
      }
    }
  }
}
