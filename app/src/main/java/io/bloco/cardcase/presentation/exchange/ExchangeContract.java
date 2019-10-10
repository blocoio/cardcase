package io.bloco.cardcase.presentation.exchange;

import androidx.annotation.StringRes;

import java.util.List;

import io.bloco.cardcase.data.models.Card;

public class ExchangeContract {
  public interface View {

    void setupCards(List<Card> receivedCards);

    void notifyCardAdded();

    void showCards();

    void showDone();

    void showError(@StringRes int messageRes);

    void openInvite();

    void close();

    void closeWithConfirmation();
  }

  public interface Presenter {
    void start(View view);

    void stop();

    void clickedInvite();

    void clickedClose();

    void clickedDone();
  }
}
