package io.bloco.cardcase.presentation.user;

import io.bloco.cardcase.data.models.Card;

public class UserContract {
  public interface View {
    void showUser(Card userCard);

    void showBack();

    void hideBack();

    void showCancel();

    void hideCancel();

    void showEditButton();

    void hideEditButton();

    void showDoneButton();

    void hideDoneButton();

    void enableEditMode();

    void disabledEditMode();

    void openHome();

    void close();
  }

  public interface Presenter {
    void start(View view, boolean onboarding);

    void clickedBack();

    void clickedCancel();

    void clickedEdit();

    void clickedDone(Card updatedCard);

    void onCardChanged(Card updatedCard);
  }
}
