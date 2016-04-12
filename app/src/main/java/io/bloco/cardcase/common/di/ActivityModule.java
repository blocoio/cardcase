package io.bloco.cardcase.common.di;

import android.app.Activity;
import dagger.Module;
import dagger.Provides;
import io.bloco.cardcase.presentation.exchange.ExchangeContract;
import io.bloco.cardcase.presentation.exchange.ExchangePresenter;
import io.bloco.cardcase.presentation.home.HomeContract;
import io.bloco.cardcase.presentation.home.HomePresenter;
import io.bloco.cardcase.presentation.user.UserContract;
import io.bloco.cardcase.presentation.user.UserPresenter;

@Module public class ActivityModule {
  private final Activity activity;

  public ActivityModule(Activity activity) {
    this.activity = activity;
  }

  @Provides @PerActivity Activity activity() {
    return this.activity;
  }

  @Provides @PerActivity
  public HomeContract.Presenter provideHomePresenter(HomePresenter presenter) {
    return presenter;
  }

  @Provides @PerActivity
  public UserContract.Presenter provideUserPresenter(UserPresenter presenter) {
    return presenter;
  }

  @Provides @PerActivity
  public ExchangeContract.Presenter provideExchangePresenter(ExchangePresenter presenter) {
    return presenter;
  }
}
