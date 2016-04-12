package io.bloco.cardcase.data;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import io.bloco.cardcase.data.models.Card;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class Database {

  private RuntimeExceptionDao<Card, String> cardDao;

  @Inject public Database(RuntimeExceptionDao<Card, String> cardDao) {
    this.cardDao = cardDao;
  }

  public Card getUserCard() {
    try {
      return getCardQuery().eq("isUser", true).queryForFirst();
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
  }

  public List<Card> getReceivedCards() {
    try {
      return getCardQuery().eq("isUser", false).query();
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void saveCard(final Card card) {
    card.setUpdatedAt(now());
    if (card.getCreatedAt() == null) {
      card.setCreatedAt(card.getUpdatedAt());
    }
    cardDao.createOrUpdate(card);
  }

  public void saveCards(List<Card> cards) {
    for (Card card : cards) {
      saveCard(card);
    }
  }

  public void clear() {
    try {
      TableUtils.clearTable(cardDao.getConnectionSource(), Card.class);
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
  }

  private Where<Card, String> getCardQuery() {
    return cardDao.queryBuilder().orderBy("updatedAt", false).where();
  }

  private Date now() {
    return Calendar.getInstance().getTime();
  }
}
