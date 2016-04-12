package io.bloco.cardcase.presentation.home;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.CardInfoView;
import javax.inject.Inject;

@PerActivity public class CardDetailDialog {

  private final Dialog dialog;

  @Bind(R.id.card_dialog_info) CardInfoView cardInfoView;

  // TODO: Inject only the activity context?
  @Inject public CardDetailDialog(Activity activity) {
    this.dialog = new Dialog(activity);
    this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.dialog.setContentView(R.layout.card_detail_dialog);
    ButterKnife.bind(this, dialog);
  }

  public void show(Card card) {
    fillCardInfoInDialog(card);
    dialog.show();
    Window window = dialog.getWindow();
    window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
  }

  private void fillCardInfoInDialog(Card card) {
    cardInfoView.setCard(card);
    cardInfoView.showTime();
  }
}
