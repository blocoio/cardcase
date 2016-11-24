package io.bloco.cardcase.presentation.home;

import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.presentation.common.CardAdapter;
import io.bloco.cardcase.presentation.common.CategoryAdapter;
import io.bloco.cardcase.presentation.common.SearchToolbar;

import java.util.List;
import java.util.UUID;

public class HomeContract {

  public interface View {
    void showEmpty();

    void showCards(List<Card> cards);

    void showCategories(List<Category> categories);

    void hideCategories();

    void resumeCategories();

    void showEmptySearchResult();

    void hideEmptySearchResult();

    void openOnboarding();

    void openUser();

    void openExchange();

    void openSearch();

    void closeSearch();

    void openSettings();

    void showDoneButton();

    void hideDoneButton();

    CategoryAdapter getCategoryAdapter();

    CardAdapter getCardAdapter();
  }

  public interface Presenter {
    void start(View view);

    void clickedSearch();

    void clickedCloseSearch();

    void searchEntered(String query, UUID category);

    void clickedUser();

    void clickedChangeTheme();

    void clickedExchange();
  }
}
