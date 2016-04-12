package io.bloco.cardcase.data;

import android.test.ApplicationTestCase;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.common.di.ApplicationModule;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.helpers.CardFactory;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class DatabaseTest extends ApplicationTestCase<AndroidApplication> {

  private RuntimeExceptionDao<Card, String> cardDao;
  private Database database;
  private CardFactory cardFactory;

  public DatabaseTest() {
    super(AndroidApplication.class);
  }

  @Override public void setUp() throws Exception {
    createApplication();
    cardDao = new ApplicationModule(getApplication()).provideCardDao();
    database = new Database(cardDao);
    cardFactory = new CardFactory(cardDao);
  }

  public void testSaveCard() throws Exception {
    Card card = cardFactory.buildUserCard();
    database.saveCard(card);

    assertEquals(cardDao.countOf(), 1);
    assertEquals(cardDao.queryBuilder().queryForFirst(), card);
  }

  public void testSaveCards() throws Exception {
    Card card1 = cardFactory.buildUserCard();
    Card card2 = cardFactory.buildUserCard();
    List<Card> cards = Arrays.asList(card1, card2);
    database.saveCards(cards);

    assertEquals(cardDao.countOf(), 2);
    assertThat(cardDao.queryForAll(), hasItems(card1, card2));
  }

  public void testGetUserCard() throws Exception {
    Card card = cardFactory.createUserCard();
    assertEquals(database.getUserCard().getId(), card.getId());
  }

  public void testGetUserCardEmpty() throws Exception {
    assertNull(database.getUserCard());
  }

  public void testGetReceivedCard() throws Exception {
    cardFactory.createUserCard();
    cardFactory.createReceivedCard();
    cardFactory.createReceivedCard();

    assertEquals(database.getReceivedCards().size(), 2);
  }

  @Override public void tearDown() throws Exception {
    cardFactory.clear();
  }
}
