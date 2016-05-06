package io.bloco.cardcase.presentation.exchange;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.Status;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.common.CardAdapter;
import java.util.List;
import javax.inject.Inject;

public class ExchangeActivity extends BaseActivity implements ExchangeContract.View {

  private static final int REQUEST_PERMISSION = 123;
  private static final int REQUEST_INVITE = 145;

  @Inject ExchangeContract.Presenter presenter;
  @Inject CardAdapter cardAdapter;

  @Bind(R.id.exchange_container) View overlay;
  @Bind(R.id.exchange_empty) ViewGroup emptyView;
  @Bind(R.id.exchange_cards) ViewGroup cardsView;
  @Bind(R.id.exchange_cards_list) RecyclerView cardsListView;
  @Bind(R.id.exchange_done) FloatingActionButton done;
  @Bind(R.id.exchange_invite) Button invite;
  @Bind(R.id.exchange_loader) View loader;

  private AlertDialog errorDialog;
  private AlertDialog confirmDialog;

  public static class Factory {
    public static Intent getIntent(Context context) {
      return new Intent(context, ExchangeActivity.class);
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exchange);

    initializeInjectors();

    bindToolbar();
    toolbar.setTitle(R.string.exchange);
    toolbar.setStartButton(R.drawable.ic_close, R.string.close, new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedClose();
      }
    });

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

  @Override protected void onStart() {
    super.onStart();
    presenter.start(this);
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    dismissDialogs();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_PERMISSION) {
      if (resultCode == RESULT_OK) {
        presenter.permissionApproved();
      } else {
        presenter.permissionRejected();
      }
    }
  }

  @Override public void onBackPressed() {
    presenter.clickedClose();
  }

  @OnClick(R.id.exchange_done) public void onClickedDone() {
    presenter.clickedDone();
  }

  @OnClick(R.id.exchange_invite) public void onClickedInvite() {
    presenter.clickedInvite();
  }

  @Override public void requestPermission(Status status) {
    try {
      status.startResolutionForResult(this, REQUEST_PERMISSION);
    } catch (IntentSender.SendIntentException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override public void setupCards(List<Card> receivedCards) {
    cardAdapter.setCards(receivedCards);
    cardsListView.setAdapter(cardAdapter);
    cardsListView.setItemAnimator(new DefaultItemAnimator());
    cardsListView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
  }

  @Override public void notifyCardAdded() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        cardAdapter.notifyItemInserted(cardAdapter.getItemCount() - 1);
      }
    });
  }

  @Override public void showCards() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        emptyView.setVisibility(View.GONE);
        cardsView.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override public void showDone() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        done.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override public void showError(@StringRes int messageRes) {
    final String message = getString(messageRes);
    runOnUiThread(new Runnable() {
      @Override public void run() {
        errorDialog = new AlertDialog.Builder(ExchangeActivity.this).setTitle(R.string.error)
            .setMessage(message)
            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                close();
              }
            })
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
              @Override public void onDismiss(DialogInterface dialog) {
                close();
              }
            })
            .show();
      }
    });
  }

  @Override public void openInvite() {
    Intent intent =
        new AppInviteInvitation.IntentBuilder(getString(R.string.invite_title)).setMessage(
            getString(R.string.invite_message))
            .setCallToActionText(getString(R.string.invite_button))
            .build();
    startActivityForResult(intent, REQUEST_INVITE);
  }

  @Override public void closeWithConfirmation() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        confirmDialog = new AlertDialog.Builder(ExchangeActivity.this).setMessage(
            R.string.exchange_close_confirm)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.exchange_close_yes, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                close();
              }
            })
            .show();
      }
    });
  }

  @Override public void close() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        closeActivityWithAnimation();
      }
    });
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

    overlay.postDelayed(new Runnable() {
      @Override public void run() {
        overlay.setVisibility(View.GONE);
        finishWithAnimation();
      }
    }, duration);
  }
}
