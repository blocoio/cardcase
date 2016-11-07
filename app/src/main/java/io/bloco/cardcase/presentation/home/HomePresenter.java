package io.bloco.cardcase.presentation.home;

import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.domain.GetCategories;
import io.bloco.cardcase.domain.GetReceivedCards;
import io.bloco.cardcase.domain.GetUserCard;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerActivity
public class HomePresenter
        implements HomeContract.Presenter, GetUserCard.Callback, GetReceivedCards.Callback, GetCategories.Callback {

    private final GetUserCard getUserCard;
    private final GetReceivedCards getReceivedCards;
    private final AnalyticsService analyticsService;
    private final GetCategories getCategories;
    private HomeContract.View view;
    private List<Card> receivedCards;
    private List<Category> categories;

    @Inject
    public HomePresenter(GetUserCard getUserCard, GetReceivedCards getReceivedCards,
                         AnalyticsService analyticsService, GetCategories getCategories) {
        this.getUserCard = getUserCard;
        this.getReceivedCards = getReceivedCards;
        this.getCategories = getCategories;
        this.analyticsService = analyticsService;
    }

    @Override
    public void start(HomeContract.View view) {
        this.view = view;
        getUserCard.get(HomePresenter.this);
        getCategories.get(HomePresenter.this);
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

    @Override
    public void clickedSearch() {
        view.openSearch();
        analyticsService.trackEvent("Home Open Search");
    }

    @Override
    public void clickedCloseSearch() {
        view.hideEmptySearchResult();
        view.closeSearch();
        showReceivedCards();
    }

    @Override
    public void searchEntered(String query) {
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

    @Override
    public void clickedUser() {
        view.openUser();
        analyticsService.trackEvent("Home View Card");
    }

    @Override
    public void clickedExchange() {
        view.openExchange();
    }

     public void clickedChangeTheme() {
            view.openSettings();
    }

    @Override
    public void onGetUserCard(Card userCard) {
        if (userCard == null) {
            view.openOnboarding();
        } else {
            getReceivedCards.get(this);
        }
    }

    @Override
    public void onGetReceivedCards(List<Card> receivedCards) {
        this.receivedCards = receivedCards;
        showReceivedCards();
    }

    public void showReceivedCards() {
        if (this.receivedCards.isEmpty()) {
            view.showEmpty();
        } else {
            view.showCards(this.receivedCards);
        }
    }

    private void showCategories() {
        if (this.categories.isEmpty()) {
            view.showEmpty();
        } else {
            view.showCategories(this.categories);
        }
    }

    private void hideCategories() {
        view.hideCategories();
    }

    private void resumeCategories() {
        view.resumeCategories();
    }

    @Override
    public void onGetCategories(List<Category> categories) {
        this.categories = categories;
        showCategories();
    }
}
