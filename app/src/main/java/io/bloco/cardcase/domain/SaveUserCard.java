package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SaveUserCard {

  private final Database database;

  public interface Callback {
    void onSaveUserCard(Card savedCard);
  }

  @Inject public SaveUserCard(Database database) {
    this.database = database;
  }

  public void save(Card userCard, Callback callback) {
    userCard.setIsUser(true);
    database.saveCard(userCard);
    callback.onSaveUserCard(userCard);
  }
}
