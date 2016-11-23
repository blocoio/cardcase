package io.bloco.cardcase.presentation.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.domain.DoneClickedEvent;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.common.CardAdapter;
import io.bloco.cardcase.presentation.common.CategoryAdapter;
import io.bloco.cardcase.presentation.common.MyEditText;
import io.bloco.cardcase.presentation.common.SearchToolbar;
import io.bloco.cardcase.presentation.exchange.ExchangeActivity;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;
import timber.log.Timber;

public class HomeActivity extends BaseActivity
    implements HomeContract.View, SearchToolbar.SearchListener {

  private static final int DURATION = 200;

  @Inject HomeContract.Presenter presenter;
  @Inject CardAdapter cardAdapter;
  @Inject CategoryAdapter categoryAdapter;

  @Bind(R.id.toolbar_search) SearchToolbar searchToolbar;
  @Bind(R.id.home_empty) ViewGroup homeEmpty;
  @Bind(R.id.home_search_empty) ViewGroup homeSearchEmpty;
  @Bind(R.id.home_cards) RecyclerView cardsView;
  @Bind(R.id.home_categories) RecyclerView categoriesView;
  @Bind(R.id.home_exchange) FloatingActionButton exchangeButton;
  @Bind(R.id.home_transition_overlay) View transitionOverlay;
  @Bind(R.id.change_theme) FloatingActionButton changeThemeButton;
  @Bind(R.id.category_done) FloatingActionButton done;

  private Category currentCategory;

  public static class Factory {
    public static Intent getIntent(Context context) {
      return new Intent(context, HomeActivity.class);
    }
  }

  public static Intent getIntent(Context context) {
    return new Intent(context, HomeActivity.class);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    initializeInjectors();

    bindToolbar();
    toolbar.setTitle(R.string.cards_received);
    toolbar.setStartButton(R.drawable.ic_user, R.string.user_card, new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedUser();
      }
    });

    Transition slideEnd = TransitionInflater.from(this).inflateTransition(R.transition.slide_end);
    getWindow().setEnterTransition(slideEnd);

    done.setVisibility(View.INVISIBLE);
    currentCategory = null;
  }

  private void initializeInjectors() {
    ActivityComponent component = DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
    component.inject(this);

    ButterKnife.bind(this);
  }

  public void setCurrentCategory(Category currentCategory) {
    this.currentCategory = currentCategory;
  }

  @Override protected void onStart() {
    super.onStart();
    transitionOverlay.setVisibility(View.GONE);
    presenter.start(this);
  }

  @Override protected void onResume() {
    super.onResume();
    Theme.applyThemeFor(this.getWindow().getDecorView(), getApplicationContext());
  }

  @Override public void onBackPressed() {
    if (searchToolbar.getVisibility() == View.VISIBLE) {
      presenter.clickedCloseSearch();
    }

    if (categoriesView.getVisibility() == View.GONE) {
      resumeCategories();
    } else {
      finish();
    }
    hideEmptySearchResult();
    currentCategory = null;
  }

  @OnClick(R.id.home_exchange) public void onClickedExchange() {
    presenter.clickedExchange();
  }

  @OnClick(R.id.change_theme) public void onClickedChangeTheme() {
    presenter.clickedChangeTheme();
  }

  @Override public void openSettings() {
    Intent intent = SettingsActivity.getIntent(this);
    startActivityWithAnimation(intent);
  }

  @OnClick(R.id.add_user_card) void onClickStart() {
    Intent intent = UserActivity.Factory.getOnboardingIntent(this);
    startActivity(intent);
  }

  @OnClick(R.id.category_done) public void onDoneClicked() {
    MyEditText nameEdit = ((MyEditText) findViewById(R.id.name_text_edit));
    nameEdit.deactivate();
    hideDoneButton();
    try {
      getCurrentFocus().clearFocus();
    } catch (NullPointerException npe) {

    }

    EventBus.getDefault().post(new DoneClickedEvent("Zdraste!"));

    //close virtual keyboard
    InputMethodManager inputManager =
        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }

  @Override public void showEmpty() {
    homeEmpty.setVisibility(View.VISIBLE);
    cardsView.setVisibility(View.GONE);
    toolbar.removeEndButton(); // Hide Search
  }

  @Override public void showCards(final List<Card> cards) {
    cardAdapter.setCards(cards);
    cardsView.setAdapter(cardAdapter);
    if (cards.size() == 0) {
      Context context = getApplicationContext();
      CharSequence text = "Category is empty!";
      int duration = Toast.LENGTH_SHORT;
      Toast toast = Toast.makeText(context, text, duration);
      toast.show();
    }

    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
    cardsView.setLayoutManager(layoutManager);
    homeEmpty.setVisibility(View.GONE);
    cardsView.animate().translationY(cardsView.getHeight()).setDuration(0);
    // Show Search
    toolbar.setEndButton(R.drawable.ic_search, R.string.search, new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedSearch();
      }
    });
  }

  public CategoryAdapter getCategoryAdapter() {
    return this.categoryAdapter;
  }

  public CardAdapter getCardAdapter() {
    return this.cardAdapter;
  }

  @Override public void showCategories(List<Category> categories) {
    categoryAdapter.setCategories(categories);
    categoriesView.setAdapter(categoryAdapter);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
    categoriesView.setLayoutManager(layoutManager);
    homeEmpty.setVisibility(View.GONE);
  }

  @Override public void resumeCategories() {
    cardsView.animate().translationY(cardsView.getHeight())

        .setDuration(DURATION).setListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        categoriesView.setVisibility(View.VISIBLE);
        cardsView.setVisibility(View.GONE);

        categoriesView.animate()
            .translationY(-categoriesView.getHeight() * 2)
            .setDuration(0)
            .setListener(new AnimatorListenerAdapter() {
              @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                categoriesView.animate().translationY(0).setDuration(DURATION);
              }
            });
      }
    });
  }

  @Override public void hideCategories() {
    cardsView.setVisibility(View.VISIBLE);
    categoriesView.animate().translationY(-categoriesView.getHeight() * 2)

        .setDuration(DURATION).setListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);

        cardsView.animate()
            .translationY(cardsView.getHeight())
            .setDuration(0)
            .setListener(new AnimatorListenerAdapter() {
              @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cardsView.animate().translationY(0).setDuration(DURATION);
                categoriesView.setVisibility(View.GONE);
              }
            });
      }
    });
  }

  @Override public void hideEmptySearchResult() {
    homeSearchEmpty.setVisibility(View.GONE);
  }

  @Override public void showEmptySearchResult() {
    homeSearchEmpty.setVisibility(View.VISIBLE);
  }

  @Override public void openOnboarding() {
    Intent intent = WelcomeActivity.Factory.getIntent(HomeActivity.this);
    startActivity(intent);
    finish();
  }

  @Override public void openUser() {
    Intent intent = UserActivity.Factory.getIntent(this);
    startActivityWithAnimation(intent);
  }

  @Override public void openExchange() {
    animateExchangeOverlay();
    Intent intent = ExchangeActivity.Factory.getIntent(this);
    startActivityWithAnimation(intent);
  }

  @Override public void openSearch() {
    toolbar.setVisibility(View.GONE);
    searchToolbar.setVisibility(View.VISIBLE);
    searchToolbar.focus();
    searchToolbar.setListener(this);
  }

  @Override public void closeSearch() {
    toolbar.setVisibility(View.VISIBLE);
    searchToolbar.setVisibility(View.GONE);
    searchToolbar.clear();
    searchToolbar.setListener(null);
  }

  @Override public void showDoneButton() {
    done.setVisibility(View.VISIBLE);
  }

  @Override public void hideDoneButton() {
    done.setVisibility(View.GONE);
  }

  @Override public void onSearchClosed() {
    presenter.clickedCloseSearch();
  }

  @Override public void onSearchQuery(String query) {
    Timber.i("onSearchQuery");
    if (currentCategory != null) {
      presenter.searchEntered(query, currentCategory.getId());
    } else {
      presenter.searchEntered(query, null);
    }
  }

  private void animateExchangeOverlay() {
    int cx = (int) exchangeButton.getX() + exchangeButton.getWidth() / 2;
    int cy = (int) exchangeButton.getY() + exchangeButton.getHeight() / 2;

    View rootView = findViewById(android.R.id.content);
    float finalRadius = Math.max(rootView.getWidth(), rootView.getHeight());

    // create the animator for this view (the start radius is zero)
    Animator circularReveal =
        ViewAnimationUtils.createCircularReveal(transitionOverlay, cx, cy, 0, finalRadius);
    circularReveal.setDuration(getResources().getInteger(R.integer.animation_duration));

    // make the view visible and start the animation
    transitionOverlay.setVisibility(View.VISIBLE);
    circularReveal.start();
  }
}
