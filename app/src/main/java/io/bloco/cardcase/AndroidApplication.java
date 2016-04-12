package io.bloco.cardcase;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.bloco.cardcase.common.analytics.AnswersTracker;
import io.bloco.cardcase.common.analytics.GoogleAnalyticsTracker;
import io.bloco.cardcase.common.di.ApplicationComponent;
import io.bloco.cardcase.common.di.ApplicationModule;
import io.bloco.cardcase.common.di.DaggerApplicationComponent;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AndroidApplication extends Application {

  private Mode mode;
  private ApplicationComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();
    checkTestMode();
    this.initializeInjector();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    // Analytics
    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
      applicationComponent.analyticsService()
          .init(this, new GoogleAnalyticsTracker(), new AnswersTracker());
    }

    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Lato-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());

    // Uncomment this to fill database with fake cards
    // if (BuildConfig.DEBUG) {
    //   getApplicationComponent().bootstrap().clearAndBootstrap();
    // }
  }

  public ApplicationComponent getApplicationComponent() {
    return this.applicationComponent;
  }

  public Mode getMode() {
    return mode;
  }

  private void initializeInjector() {
    this.applicationComponent =
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
  }

  // Test loading a random test class, to check if we're in test mode
  private void checkTestMode() {
    try {
      getClassLoader().loadClass("io.bloco.cardcase.presentation.HomeActivityTest");
      mode = Mode.TEST;
    } catch (final Exception e) {
      mode = Mode.NORMAL;
    }
  }

  public enum Mode {
    NORMAL, TEST
  }
}
