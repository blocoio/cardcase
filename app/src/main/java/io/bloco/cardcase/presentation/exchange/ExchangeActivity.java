package io.bloco.cardcase.presentation.exchange;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.common.CardAdapter;

@SuppressWarnings("ALL")
public class ExchangeActivity extends BaseActivity implements ExchangeContract.View {

  private static final int REQUEST_INVITE = 145;

  @Inject
  ExchangeContract.Presenter presenter;
  @SuppressWarnings("unused")
  @Inject
  CardAdapter cardAdapter;

  @BindView(R.id.exchange_container)
  View overlay;
  @BindView(R.id.exchange_empty)
  ViewGroup emptyView;
  @BindView(R.id.exchange_cards)
  ViewGroup cardsView;
  @BindView(R.id.exchange_cards_list)
  RecyclerView cardsListView;
  @BindView(R.id.exchange_done)
  FloatingActionButton done;
  @BindView(R.id.exchange_invite)
  Button invite;
  @BindView(R.id.exchange_loader)
  View loader;

  private AlertDialog errorDialog;
  private AlertDialog confirmDialog;

  public static class Factory {
    public static Intent getIntent(Context context) {
      return new Intent(context, ExchangeActivity.class);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exchange);

    initializeInjectors();

    bindToolbar();
    toolbar.setTitle(R.string.exchange);
    toolbar.setStartButton(R.drawable.ic_close, R.string.close, v -> presenter.clickedClose());

    invite.setText(Html.fromHtml(getString(R.string.exchange_invite)));

    cardAdapter.showLoader();
  }

  private void initializeInjectors() {
    ActivityComponent component = DaggerActivityComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
    component.inject(this);

    ButterKnife.bind(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    presenter.start(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    dismissDialogs();
  }

  @Override
  public void onBackPressed() {
    presenter.clickedClose();
  }

  @OnClick(R.id.exchange_done)
  public void onClickedDone() {
    presenter.clickedDone();
  }

  @OnClick(R.id.exchange_invite)
  public void onClickedInvite() {
    presenter.clickedInvite();
  }

  @Override
  public void setupCards(List<Card> receivedCards) {
    cardAdapter.setCards(receivedCards);
    cardsListView.setAdapter(cardAdapter);
    cardsListView.setItemAnimator(new DefaultItemAnimator());
    cardsListView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
  }

  @Override
  public void notifyCardAdded() {
    runOnUiThread(() -> cardAdapter.notifyItemInserted(cardAdapter.getItemCount() - 1));
  }

  @Override
  public void showCards() {
    runOnUiThread(() -> {
      emptyView.setVisibility(View.GONE);
      cardsView.setVisibility(View.VISIBLE);
    });
  }

  @Override
  public void showDone() {
    runOnUiThread(() -> done.show());
  }

  @Override
  public void showError(@StringRes int messageRes) {
    final String message = getString(messageRes);
    runOnUiThread(() -> errorDialog = new AlertDialog.Builder(ExchangeActivity.this).setTitle(R.string.error)
        .setMessage(message)
        .setNeutralButton(R.string.ok, (dialog, which) -> close())
        .setOnDismissListener(dialog -> close())
        .show());
  }

  @Override
  public void openInvite() {
    Intent intent =
        new AppInviteInvitation.IntentBuilder(getString(R.string.invite_title)).setMessage(
            getString(R.string.invite_message))
            .setCallToActionText(getString(R.string.invite_button))
            .build();
    startActivityForResult(intent, REQUEST_INVITE);
  }

  @Override
  public void closeWithConfirmation() {
    runOnUiThread(() -> confirmDialog = new AlertDialog.Builder(ExchangeActivity.this).setMessage(
        R.string.exchange_close_confirm)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.exchange_close_yes, (dialog, which) -> close())
        .show());
  }

  @Override
  public void close() {
    runOnUiThread(this::closeActivityWithAnimation);
  }

  private void dismissDialogs() {
    if (errorDialog != null && errorDialog.isShowing()) {
      errorDialog.dismiss();
      errorDialog = null;
    }
    if (confirmDialog != null && confirmDialog.isShowing()) {
      confirmDialog.dismiss();
      confirmDialog = null;
    }
  }

  private void closeActivityWithAnimation() {
    if (!overlay.isAttachedToWindow()) {
      finishWithAnimation();
      return;
    }

    float fabMargin = getResources().getDimension(R.dimen.fab_margin);
    float fabSize = getResources().getDimension(R.dimen.fab_size);
    float fabOffset = fabMargin + fabSize / 2;
    int cx = Math.round(overlay.getWidth() - fabOffset);
    int cy = Math.round(
        overlay.getHeight() - fabOffset + getResources().getDimension(R.dimen.nav_bar_subtraction));

    float startRadius = Math.max(overlay.getWidth(), overlay.getHeight());

    int duration = getResources().getInteger(R.integer.animation_duration);

    Animator circularReveal =
        ViewAnimationUtils.createCircularReveal(overlay, cx, cy, startRadius, 0);
    circularReveal.setDuration(duration);
    circularReveal.start();

    overlay.postDelayed(() -> {
      overlay.setVisibility(View.GONE);
      finishWithAnimation();
    }, duration);
  }
}
