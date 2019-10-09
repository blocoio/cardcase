package io.bloco.cardcase.data;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.common.di.ApplicationModule;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.helpers.CardFactory;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

  private RuntimeExceptionDao<Card, String> cardDao;
  private Database database;
  private CardFactory cardFactory;

  @Before
  public void setUp() {
    cardDao = new ApplicationModule(getApplication()).provideCardDao();
    database = new Database(cardDao);
    cardFactory = new CardFactory(cardDao);
  }

  @Test
  public void testSaveCard() throws Exception {
    Card card = cardFactory.buildUserCard();
    database.saveCard(card);

    assertEquals(cardDao.countOf(), 1);
    assertEquals(cardDao.queryBuilder().queryForFirst(), card);
  }
  @Test
  public void testSaveCards() {
    Card card1 = cardFactory.buildUserCard();
    Card card2 = cardFactory.buildUserCard();
    List<Card> cards = Arrays.asList(card1, card2);
    database.saveCards(cards);

    assertEquals(cardDao.countOf(), 2);
    assertThat(cardDao.queryForAll(), hasItems(card1, card2));
  }
  @Test
  public void testGetUserCard() {
    Card card = cardFactory.createUserCard();
    assertEquals(database.getUserCard().getId(), card.getId());
  }
  @Test
  public void testGetUserCardEmpty() {
    assertNull(database.getUserCard());
  }
  @Test
  public void testGetReceivedCard() {
    cardFactory.createUserCard();
    cardFactory.createReceivedCard();
    cardFactory.createReceivedCard();

    assertEquals(database.getReceivedCards().size(), 2);
  }

  @After
  public void tearDown() {
    cardFactory.clear();
  }

  private AndroidApplication getApplication() {
    return (AndroidApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
  }
}
