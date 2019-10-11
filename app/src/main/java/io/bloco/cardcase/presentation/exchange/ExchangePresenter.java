package io.bloco.cardcase.presentation.exchange;

import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.bloco.cardcase.R;
import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.domain.GetUserCard;
import io.bloco.cardcase.domain.SaveReceivedCards;
import timber.log.Timber;

@PerActivity
public class ExchangePresenter
    implements ExchangeContract.Presenter, NearbyManager.Listener, GetUserCard.Callback {

  private final NearbyManager nearbyManager;
  private final CardSerializer cardSerializer;
  private final GetUserCard getUserCard;
  private final SaveReceivedCards saveReceivedCards;
  private final AnalyticsService analyticsService;
  private ExchangeContract.View view;
  private List<Card> receivedCards;
  private boolean errorState;

  @Inject
  public ExchangePresenter(NearbyManager nearbyManager, CardSerializer cardSerializer,
                           GetUserCard getUserCard, SaveReceivedCards saveReceivedCards,
                           AnalyticsService analyticsService) {
    this.nearbyManager = nearbyManager;
    this.cardSerializer = cardSerializer;
    this.getUserCard = getUserCard;
    this.saveReceivedCards = saveReceivedCards;
    this.analyticsService = analyticsService;
  }

  @Override
  public void start(ExchangeContract.View view) {
    this.view = view;
    this.errorState = false;
    this.receivedCards = new ArrayList<>();

    view.setupCards(this.receivedCards);
    getUserCard.get(this);

    analyticsService.trackEvent("Exchange Screen");
  }

  @Override
  public void stop() {
    nearbyManager.stop();
  }

  @Override
  public void onGetUserCard(Card userCard) {
    nearbyManager.start(cardSerializer.serialize(userCard), this);
  }

  @Override
  public void clickedInvite() {
    analyticsService.trackEvent("Exchange Invite Open");
    view.openInvite();
  }

  @Override
  public void clickedClose() {
    if (receivedCards.isEmpty()) {
      view.close();
    } else {
      view.closeWithConfirmation();
    }
  }

  @Override
  public void clickedDone() {
    saveReceivedCards.save(receivedCards, savedCards -> view.close());
  }

  @Override
  public void onMessageReceived(byte[] messageBytes) {
    analyticsService.trackEvent("Exchange Card Received");
    Card card = cardSerializer.deserialize(messageBytes);
    if (card != null) {
      addNewCard(card);
    }
  }

  @Override
  public void onError(Status status) {
    Timber.e("Exchange error: %s", status);
    showConnectionError();
  }

  private void addNewCard(final Card card) {
    if (receivedCards.contains(card)) {
      return;
    }

    card.setCreatedAt(Calendar.getInstance().getTime());
    card.setUpdatedAt(card.getCreatedAt());
    card.setIsUser(false);

    receivedCards.add(card);
    view.notifyCardAdded();

    if (receivedCards.size() == 1) { // First card added
      view.showCards();
      view.showDone();
    }
  }

  private void showConnectionError() {
    if (errorState) {
      return;
    }
    errorState = true;
    view.showError(R.string.error_connection);
  }
}
