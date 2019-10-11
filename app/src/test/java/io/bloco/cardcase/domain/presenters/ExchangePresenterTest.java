package io.bloco.cardcase.domain.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.domain.GetUserCard;
import io.bloco.cardcase.domain.SaveReceivedCards;
import io.bloco.cardcase.presentation.exchange.CardSerializer;
import io.bloco.cardcase.presentation.exchange.ExchangeContract;
import io.bloco.cardcase.presentation.exchange.ExchangePresenter;
import io.bloco.cardcase.presentation.exchange.NearbyManager;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
public class ExchangePresenterTest {
  @InjectMocks
  private ExchangePresenter exchangePresenter;

  @Mock
  private NearbyManager nearbyManager;
  @Mock
  private CardSerializer cardSerializer;
  @Mock
  private GetUserCard getUserCard;
  @Mock
  private SaveReceivedCards saveReceivedCards;
  @Mock
  private AnalyticsService analyticsService;
  @Mock
  private ExchangeContract.View view;
  @Captor
  private ArgumentCaptor<List<Card>> receivedCardsCaptor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    exchangePresenter.start(view);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void onMessageReceived() {
    ArgumentCaptor<List<Card>> receivedCardsCaptor = ArgumentCaptor.forClass(List.class);
    verify(view).setupCards(receivedCardsCaptor.capture());
    List<Card> receivedCards = receivedCardsCaptor.getValue();

    Card card = new Card();
    card.setId("1");
    card.setName("Sergio");
    when(cardSerializer.deserialize(any())).thenReturn(card);

    exchangePresenter.onMessageReceived(new byte[]{});
    verify(view).showCards();
    assertTrue(receivedCards.contains(card));
  }
}
