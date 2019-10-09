package io.bloco.cardcase.helpers;

import android.app.Activity;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class AssertCurrentActivity {
  public static void assertCurrentActivity(Class<? extends Activity> activityClass) {
    assertEquals(activityClass.getName(), getCurrentActivity().getComponentName().getClassName());
  }

  private static Activity getCurrentActivity() {
    getInstrumentation().waitForIdleSync();
    final Activity[] activity = new Activity[1];
    getInstrumentation().runOnMainSync(() -> {
      Collection<Activity> activities =
          ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
      activity[0] = Iterables.getOnlyElement(activities);
    });
    return activity[0];
  }
}
