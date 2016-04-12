package io.bloco.cardcase.presentation.exchange;

import android.support.annotation.StringRes;
import com.google.android.gms.common.api.Status;
import io.bloco.cardcase.data.models.Card;
import java.util.List;

public class ExchangeContract {
  public interface View {
    void requestPermission(Status status);

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

    void permissionApproved();

    void permissionRejected();

    void clickedInvite();

    void clickedClose();

    void clickedDone();
  }
}
