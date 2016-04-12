package io.bloco.cardcase.common.di;

import android.app.Activity;
import dagger.Component;
import io.bloco.cardcase.presentation.exchange.ExchangeActivity;
import io.bloco.cardcase.presentation.exchange.NearbyManager;
import io.bloco.cardcase.presentation.home.HomeActivity;
import io.bloco.cardcase.presentation.user.CropAvatarActivity;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;

@PerActivity @Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

  void inject(CropAvatarActivity cropAvatarActivity);

  void inject(HomeActivity activity);

  void inject(UserActivity activity);

  void inject(ExchangeActivity activity);

  void inject(WelcomeActivity activity);

  //Exposed to sub-graphs.
  Activity activity();

  NearbyManager nearbyManager();
}
