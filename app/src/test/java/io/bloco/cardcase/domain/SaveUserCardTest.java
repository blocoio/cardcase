package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class SaveUserCardTest {

  private SaveUserCard saveUserCard;

  @Mock private Database database;
  @Mock private SaveUserCard.Callback callback;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    saveUserCard = new SaveUserCard(database);
  }

  @Test public void testGet() {
    Card card = new Card();
    saveUserCard.save(card, callback);

    verify(database).saveCard(eq(card));
    verify(callback).onSaveUserCard(eq(card));
  }
}
