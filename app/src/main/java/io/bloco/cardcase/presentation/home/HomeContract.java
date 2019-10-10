package io.bloco.cardcase.presentation.home;

import java.util.List;

import io.bloco.cardcase.data.models.Card;

public class HomeContract {
  public interface View {
    void showEmpty();

    void showCards(List<Card> cards);

    void showEmptySearchResult();

    void hideEmptySearchResult();

    void openOnboarding();

    void openUser();

    void openExchange();

    void openSearch();

    void closeSearch();
  }

  public interface Presenter {
    void start(View view);

    void clickedSearch();

    void clickedCloseSearch();

    void searchEntered(String query);

    void clickedUser();

    void clickedExchange();
  }
}
