package io.bloco.cardcase.presentation.home;

import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.view.Window;
import android.view.WindowManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.CardInfoView;

import javax.inject.Inject;

@PerActivity
public class CardDetailDialog {

    private final Dialog dialog;
    private final Database database;
    private HomeContract.View homeContract;

    @Bind(R.id.card_dialog_info)
    CardInfoView cardInfoView;
    @Bind(R.id.buttonDeleteCard)
    FloatingActionButton deleteCard;

    // TODO: Inject only the activity context?
    @Inject
    public CardDetailDialog(Activity activity, Database database) {
        homeContract = (HomeContract.View) activity;
        this.dialog = new Dialog(activity);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.card_detail_dialog);
        this.database = database;
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

    @OnClick(R.id.buttonDeleteCard)
    public void onClickedDelete() {
        database.deleteCard(cardInfoView.getCard());
        dialog.dismiss();
        homeContract.showCards(database.getReceivedCards());
    }
}
