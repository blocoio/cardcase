package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetReceivedCards {

  private final Database database;

  public interface Callback {
    void onGetReceivedCards(List<Card> receivedCards);
  }

  @Inject public GetReceivedCards(Database database) {
    this.database = database;
  }

  public void get(Callback callback) {
    List<Card> receivedCards = database.getReceivedCards();
    callback.onGetReceivedCards(receivedCards);
  }
}
