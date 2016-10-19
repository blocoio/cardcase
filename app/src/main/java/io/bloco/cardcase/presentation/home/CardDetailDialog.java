package io.bloco.cardcase.presentation.home;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.CardInfoView;
import io.bloco.cardcase.presentation.exchange.ExchangeActivity;

import javax.inject.Inject;

@PerActivity
public class CardDetailDialog {

    private final Dialog dialog;
    private final Database database;
    private HomeContract.View homeContract;

    @Bind(R.id.card_detail_dialog_id)
    View overlay;
    @Bind(R.id.card_dialog_info)
    CardInfoView cardInfoView;
    @Bind(R.id.buttonDeleteCard)
    FloatingActionButton deleteCard;
    @Bind(R.id.card_dialog_transition_overlay)
    View transitionOverlay;
    @Bind(R.id.buttonShareCard)
    FloatingActionButton shareCard;

    // TODO: Inject only the activity context?
    @Inject
    public CardDetailDialog(Activity activity, Database database) {
        if (!(activity instanceof ExchangeActivity)) {
            homeContract = (HomeContract.View) activity;
        } else {
            homeContract = null;
        }

        this.dialog = new Dialog(activity);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.card_detail_dialog);
        this.database = database;
        ButterKnife.bind(this, dialog);

        //Add animation to the CardDialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void show(Card card) {
        fillCardInfoInDialog(card);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        transitionOverlay.setVisibility(View.GONE);
        overlay.setVisibility(View.VISIBLE);
    }

    private void fillCardInfoInDialog(Card card) {
        cardInfoView.setCard(card);
        cardInfoView.showTime();
    }

    @OnClick(R.id.buttonDeleteCard)
    public void onClickedDelete() {
        AlertDialog alert = getDialog().create();
        alert.show();
    }

    private AlertDialog.Builder getDialog() {
        TextView title = new TextView((Activity) homeContract);
        title.setText("Remove the card");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(23);

        TextView msg = new TextView((Activity) homeContract);
        msg.setText("Are you sure ?");
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) homeContract);
        builder.setCustomTitle(title);
        builder.setView(msg);

        builder.setIcon(R.drawable.ic_delete_forever_white_24px);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                database.deleteCard(cardInfoView.getCard());
                closeActivityWithAnimation();
                if (homeContract != null)
                    homeContract.showCards(database.getReceivedCards());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    @OnClick(R.id.buttonShareCard)
    public void onClickedShare() {
        database.prepareCardSharing(cardInfoView.getCard());
        database.getUserCard();
        Log.d("TEST", "isUser: " + cardInfoView.getCard().isUser());
        homeContract.openExchange();
    }

    private void closeActivityWithAnimation() {

        int cx = (int) deleteCard.getX() + deleteCard.getWidth() / 2;
        int cy = (int) deleteCard.getY() + deleteCard.getHeight() / 2;

        View rootView = dialog.findViewById(android.R.id.content);
        float finalRadius = Math.max(rootView.getWidth(), rootView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(transitionOverlay, cx, cy, 0, finalRadius);
        circularReveal.setDuration(600);

        // make the view visible and start the animation
        transitionOverlay.setVisibility(View.VISIBLE);
        circularReveal.start();

        overlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                overlay.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }, 600);
    }

}
