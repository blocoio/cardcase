package io.bloco.cardcase.presentation.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.user.UserActivity;
import javax.inject.Inject;

public class WelcomeActivity extends BaseActivity {

  @Inject AnalyticsService analyticsService;

  public static class Factory {
    public static Intent getIntent(Context context) {
      return new Intent(context, WelcomeActivity.class);
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    initializeInjectors();
    ButterKnife.bind(this);
    analyticsService.trackEvent("Welcome Screen");
  }

  @OnClick(R.id.welcome_start) void onClickStart() {
    Intent intent = UserActivity.Factory.getOnboardingIntent(this);
    startActivity(intent);
    finishWithAnimation();
  }

  private void initializeInjectors() {
    ActivityComponent component = DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
    component.inject(this);

    ButterKnife.bind(this);
  }
}
