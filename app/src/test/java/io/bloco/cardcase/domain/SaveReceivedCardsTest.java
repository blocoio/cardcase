package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class SaveReceivedCardsTest {

  private SaveReceivedCards saveReceivedCards;

  @Mock private Database database;
  @Mock private SaveReceivedCards.Callback callback;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    saveReceivedCards = new SaveReceivedCards(database);
  }

  @Test public void testGet() {
    List<Card> cards = Arrays.asList(new Card(), new Card());
    saveReceivedCards.save(cards, callback);

    verify(database).saveCards(eq(cards));
    verify(callback).onSavedReceivedCards(eq(cards));
  }
}
