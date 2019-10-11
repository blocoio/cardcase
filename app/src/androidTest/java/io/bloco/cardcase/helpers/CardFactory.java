package io.bloco.cardcase.helpers;

import androidx.test.rule.ActivityTestRule;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.data.models.Card;
import io.bloco.faker.Faker;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("ALL")
public class CardFactory {

  private final Faker faker;
  private RuntimeExceptionDao<Card, String> dao;

  private CardFactory() {
    faker = new Faker();
  }

  public CardFactory(ActivityTestRule activityTestRule) {
    this(((AndroidApplication) activityTestRule.getActivity().getApplication())
        .getApplicationComponent().cardDao());
  }

  public CardFactory(RuntimeExceptionDao<Card, String> dao) {
    this();
    this.dao = dao;
  }

  private Card build() {
    Card card = new Card();
    card.setId(UUID.randomUUID().toString());
    card.setName(faker.name.name());
    card.setEmail(faker.internet.email(card.getName()));
    card.setPhone(faker.phoneNumber.phoneNumber());
    card.setCreatedAt(faker.time.backward(365));
    card.setUpdatedAt(card.getCreatedAt());
    return card;
  }

  public Card buildUserCard() {
    Card card = build();
    card.setIsUser(true);
    return card;
  }

  private Card buildReceivedCard() {
    Card card = build();
    card.setIsUser(false);
    return card;
  }

  public Card create() {
    return create(build());
  }

  public Card createUserCard() {
    return create(buildUserCard());
  }

  public Card createReceivedCard() {
    return create(buildReceivedCard());
  }

  private Card create(final Card card) {
    dao.createOrUpdate(card);
    return card;
  }

  public void clear() {
    try {
      TableUtils.clearTable(dao.getConnectionSource(), Card.class);
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
  }
}
