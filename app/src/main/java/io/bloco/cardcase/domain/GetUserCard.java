package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetUserCard {

  private final Database database;

  public interface Callback {
    void onGetUserCard(Card userCard);
  }

  @Inject public GetUserCard(Database database) {
    this.database = database;
  }

  public void get(Callback callback) {
    Card userCard = this.database.getUserCard();
    callback.onGetUserCard(userCard);
  }
}
