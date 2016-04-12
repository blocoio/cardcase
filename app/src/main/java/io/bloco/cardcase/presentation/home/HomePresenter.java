package io.bloco.cardcase.presentation.home;

import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.domain.GetReceivedCards;
import io.bloco.cardcase.domain.GetUserCard;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@PerActivity public class HomePresenter
    implements HomeContract.Presenter, GetUserCard.Callback, GetReceivedCards.Callback {

  private final GetUserCard getUserCard;
  private final GetReceivedCards getReceivedCards;
  private final AnalyticsService analyticsService;
  private HomeContract.View view;
  private List<Card> receivedCards;

  @Inject public HomePresenter(GetUserCard getUserCard, GetReceivedCards getReceivedCards,
      AnalyticsService analyticsService) {
    this.getUserCard = getUserCard;
    this.getReceivedCards = getReceivedCards;
    this.analyticsService = analyticsService;
  }

  @Override public void start(HomeContract.View view) {
    this.view = view;
    getUserCard.get(HomePresenter.this);

    /*
    Card card1 = new Card();
    card1.setId("1");
    card1.setName("Caroline Smith");
    card1.setCreatedAt(Calendar.getInstance().getTime());
    card1.setUpdatedAt(Calendar.getInstance().getTime());

    Card card2 = new Card();
    card2.setId("2");
    card2.setName("Jamie Frazier");
    card2.setCreatedAt(new Date(Calendar.getInstance().getTimeInMillis() - 1000000));
    card2.setUpdatedAt(new Date(Calendar.getInstance().getTimeInMillis() - 1000000));

    Card card3 = new Card();
    card3.setId("3");
    card3.setName("Arnold Tucker");
    card3.setCreatedAt(new Date(Calendar.getInstance().getTimeInMillis() - 5000000));
    card3.setUpdatedAt(new Date(Calendar.getInstance().getTimeInMillis() - 5000000));

    onGetReceivedCards(Arrays.asList(card1, card2, card3));
    */

    analyticsService.trackEvent("Home Screen");
  }

  @Override public void clickedSearch() {
    view.openSearch();
    analyticsService.trackEvent("Home Open Search");
  }

  @Override public void clickedCloseSearch() {
    view.hideEmptySearchResult();
    view.closeSearch();
    showReceivedCards();
  }

  @Override public void searchEntered(String query) {
    List<Card> filteredCards = new ArrayList<>(receivedCards.size());
    for (Card card : receivedCards) {
      if (card.matchQuery(query)) {
        filteredCards.add(card);
      }
    }

    if (filteredCards.isEmpty()) {
      view.showEmptySearchResult();
    } else {
      view.hideEmptySearchResult();
    }

    view.showCards(filteredCards);
  }

  @Override public void clickedUser() {
    view.openUser();
    analyticsService.trackEvent("Home View Card");
  }

  @Override public void clickedExchange() {
    view.openExchange();
  }

  @Override public void onGetUserCard(Card userCard) {
    if (userCard == null) {
      view.openOnboarding();
    } else {
      getReceivedCards.get(this);
    }
  }

  @Override public void onGetReceivedCards(List<Card> receivedCards) {
    this.receivedCards = receivedCards;
    showReceivedCards();
  }

  private void showReceivedCards() {
    if (this.receivedCards.isEmpty()) {
      view.showEmpty();
    } else {
      view.showCards(this.receivedCards);
    }
  }
}
