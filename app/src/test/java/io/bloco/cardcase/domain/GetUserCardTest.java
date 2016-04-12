package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserCardTest {

  private GetUserCard getUserCard;

  @Mock private Database database;
  @Mock private GetUserCard.Callback callback;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    getUserCard = new GetUserCard(database);
  }

  @Test public void testGet() {
    Card card = new Card();
    when(database.getUserCard()).thenReturn(card);

    getUserCard.get(callback);

    verify(callback).onGetUserCard(eq(card));
  }
}
