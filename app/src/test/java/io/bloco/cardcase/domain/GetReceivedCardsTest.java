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
import static org.mockito.Mockito.when;

public class GetReceivedCardsTest {

  private GetReceivedCards getReceivedCards;

  @Mock private Database database;
  @Mock private GetReceivedCards.Callback callback;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    getReceivedCards = new GetReceivedCards(database);
  }

  @Test public void testGet() {
    List<Card> cards = Arrays.asList(new Card(), new Card());
    when(database.getReceivedCards()).thenReturn(cards);

    getReceivedCards.get(callback);

    verify(callback).onGetReceivedCards(eq(cards));
  }
}
