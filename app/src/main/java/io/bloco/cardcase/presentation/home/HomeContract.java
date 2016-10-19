package io.bloco.cardcase.presentation.home;

import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;

import java.util.List;

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

    }

    public interface Presenter {
        void start(View view);

        void clickedSearch();

        void clickedCloseSearch();

        void searchEntered(String query);

        void clickedUser();

        void clickedChangeTheme();

        void clickedExchange();
    }
}
