package io.bloco.cardcase.presentation.home;

import android.animation.Animator;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.common.CardAdapter;
import io.bloco.cardcase.presentation.common.SearchToolbar;
import io.bloco.cardcase.presentation.exchange.ExchangeActivity;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class HomeActivity extends BaseActivity
    implements HomeContract.View, SearchToolbar.SearchListener {

  @Inject HomeContract.Presenter presenter;
  @Inject CardAdapter cardAdapter;

  @Bind(R.id.toolbar_search) SearchToolbar searchToolbar;

  @Bind(R.id.home_empty) ViewGroup homeEmpty;

  @Bind(R.id.home_search_empty) ViewGroup homeSearchEmpty;

  @Bind(R.id.home_cards) RecyclerView cardsView;

  @Bind(R.id.home_exchange) FloatingActionButton exchangeButton;

  @Bind(R.id.home_transition_overlay) View transitionOverlay;

  public static class Factory {
    public static Intent getIntent(Context context) {
      return new Intent(context, HomeActivity.class);
    }
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
  }

  private void initializeInjectors() {
    ActivityComponent component = DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
    component.inject(this);

    ButterKnife.bind(this);
  }

  @Override protected void onStart() {
    super.onStart();
    transitionOverlay.setVisibility(View.GONE);
    presenter.start(this);
  }

  @Override public void onBackPressed() {
    if (searchToolbar.getVisibility() == View.VISIBLE) {
      presenter.clickedCloseSearch();
    } else {
      finish();
    }
  }

  @OnClick(R.id.home_exchange) public void onClickedExchange() {
    presenter.clickedExchange();
  }

  @Override public void showEmpty() {
    homeEmpty.setVisibility(View.VISIBLE);
    cardsView.setVisibility(View.GONE);
    toolbar.removeEndButton(); // Hide Search
  }

  @Override public void showCards(final List<Card> cards) {
    cardAdapter.setCards(cards);
    cardsView.setAdapter(cardAdapter);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
    cardsView.setLayoutManager(layoutManager);
    cardsView.setVisibility(View.VISIBLE);
    homeEmpty.setVisibility(View.GONE);

    // Show Search
    toolbar.setEndButton(R.drawable.ic_search, R.string.search, new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedSearch();
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

  @Override public void onSearchClosed() {
    presenter.clickedCloseSearch();
  }

  @Override public void onSearchQuery(String query) {
    Timber.i("onSearchQuery");
    presenter.searchEntered(query);
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
