package io.bloco.cardcase.common.di;

import android.content.Context;
import android.content.res.Resources;
import com.google.gson.Gson;
import dagger.Component;
import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.domain.GetReceivedCards;
import io.bloco.cardcase.domain.GetUserCard;
import io.bloco.cardcase.domain.SaveReceivedCards;
import io.bloco.cardcase.domain.SaveUserCard;
import io.bloco.cardcase.presentation.common.Bootstrap;
import io.bloco.cardcase.presentation.common.CardInfoView;
import io.bloco.cardcase.presentation.common.CardViewHolder;
import io.bloco.cardcase.presentation.common.ErrorDisplayer;
import io.bloco.cardcase.presentation.common.ImageLoader;
import io.bloco.cardcase.presentation.exchange.CardSerializer;
import io.bloco.cardcase.presentation.user.AvatarPicker;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  void inject(CardViewHolder cardViewHolder);

  void inject(CardInfoView cardInfoView);

  //Exposed to sub-graphs.
  Context context();

  Resources resources();

  Gson gson();

  Database database();

  AvatarPicker avatarPicker();

  GetUserCard getUserCard();

  GetReceivedCards getReceivedCards();

  SaveUserCard saveUserCard();

  SaveReceivedCards saveReceivedCards();

  ImageLoader imageLoader();

  CardSerializer cardSerializer();

  AnalyticsService analyticsService();

  Bootstrap bootstrap();

  ErrorDisplayer errorDisplayer();
}
