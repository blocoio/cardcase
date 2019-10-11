package io.bloco.cardcase.domain.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.domain.GetReceivedCards;
import io.bloco.cardcase.domain.GetUserCard;
import io.bloco.cardcase.presentation.home.HomeContract;
import io.bloco.cardcase.presentation.home.HomePresenter;

import static org.mockito.Mockito.verify;

@SuppressWarnings("ALL")
public class HomePresenterTest {
  @InjectMocks
  private HomePresenter homePresenter;

  @Mock
  private HomeContract.View view;
  @Mock
  private List<Card> receivedCards;
  @Mock
  private GetUserCard getUserCard;
  @Mock
  private GetReceivedCards getReceivedCards;
  @Mock
  private AnalyticsService analyticsService;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    homePresenter.start(view);
  }

  @Test
  public void testClickedSearch() {
    homePresenter.clickedSearch();
    verify(view).openSearch();
  }

  @Test
  public void searchClickedUser() {
    homePresenter.clickedUser();
    verify(view).openUser();
  }

  @Test
  public void searchEnteredName() {
    receivedCards = new ArrayList<>();
    Card card1 = new Card();
    card1.setName("sergio");
    card1.setId("1");
    receivedCards.add(card1);
    Card card2 = new Card();
    card2.setName("joao");
    card2.setId("2");
    receivedCards.add(card2);
    Card card3 = new Card();
    card3.setName("maria");
    card3.setId("3");
    receivedCards.add(card3);

    homePresenter.onGetReceivedCards(receivedCards);
    homePresenter.searchEntered(card2.getName());
    verify(view).showCards(Collections.singletonList(card2));
  }

  @Test
  public void onGetReceivedCards() {
    receivedCards = new ArrayList<>();
    Card card1 = new Card();
    card1.setName("sergio");
    card1.setId("1");
    receivedCards.add(card1);
    Card card2 = new Card();
    card2.setName("joao");
    card2.setId("2");
    receivedCards.add(card2);
    Card card3 = new Card();
    card3.setName("maria");
    card3.setId("3");
    receivedCards.add(card3);

    homePresenter.onGetReceivedCards(receivedCards);
    verify(view).showCards(Arrays.asList(card1, card2, card3));
  }
}
