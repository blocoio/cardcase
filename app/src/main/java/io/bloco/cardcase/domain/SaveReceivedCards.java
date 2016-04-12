package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SaveReceivedCards {

  private final Database database;

  public interface Callback {
    void onSavedReceivedCards(List<Card> savedCards);
  }

  @Inject public SaveReceivedCards(Database database) {
    this.database = database;
  }

  public void save(List<Card> receivedCards, Callback callback) {
    for (Card receivedCard : receivedCards) {
      receivedCard.setIsUser(false);
      receivedCard.setCreatedAt(null);
    }
    database.saveCards(receivedCards);
    callback.onSavedReceivedCards(receivedCards);
  }
}
