package io.bloco.cardcase.presentation.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityComponent;
import io.bloco.cardcase.common.di.DaggerActivityComponent;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.common.CardInfoView;
import io.bloco.cardcase.presentation.common.ErrorDisplayer;
import io.bloco.cardcase.presentation.common.ImageLoader;
import io.bloco.cardcase.presentation.home.HomeActivity;

@SuppressWarnings("unused")
public class UserActivity extends BaseActivity
    implements UserContract.View, CardInfoView.CardEditListener {

  @Inject
  UserContract.Presenter presenter;
  @Inject
  ImageLoader imageLoader;
  @Inject
  AvatarPicker avatarPicker;
  @Inject
  ErrorDisplayer errorDisplayer;

  @BindView(R.id.user_layout)
  ViewGroup rootLayout;
  @BindView(R.id.user_card)
  CardInfoView cardView;
  @BindView(R.id.user_edit)
  FloatingActionButton edit;
  @BindView(R.id.user_done)
  FloatingActionButton done;

  public static class Factory {
    static class BundleArgs {
      private static final String ONBOARDING = "onboarding";
    }

    public static Intent getIntent(Context context) {
      return new Intent(context, UserActivity.class);
    }

    public static Intent getOnboardingIntent(Context context) {
      Intent intent = getIntent(context);
      intent.putExtra(BundleArgs.ONBOARDING, true);
      return intent;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);

    initializeInjectors();

    bindToolbar();
    toolbar.setTitle(R.string.user_card);

    Intent intent = getIntent();
    boolean onboarding = intent.getBooleanExtra(Factory.BundleArgs.ONBOARDING, false);

    cardView.setEditListener(this);

    presenter.start(this, onboarding);

    Transition slideEnd = TransitionInflater.from(this).inflateTransition(R.transition.slide_end);
    Transition slideStart =
        TransitionInflater.from(this).inflateTransition(R.transition.slide_start);
    getWindow().setEnterTransition(slideStart);
    getWindow().setExitTransition(slideEnd);
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
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    File avatarFile;

    try {
      avatarFile = avatarPicker.processActivityResult(requestCode, resultCode, data, this);
    } catch (AvatarPicker.ReceivingError avatarReceivingError) {
      errorDisplayer.show(rootLayout, R.string.error_avatar_receiving);
      return;
    } catch (AvatarPicker.ResizeError resizeError) {
      errorDisplayer.show(rootLayout, R.string.error_avatar_resize);
      return;
    }

    if (avatarFile != null) {
      cardView.setAvatar(avatarFile.getAbsolutePath());
      presenter.onCardChanged(cardView.getCard());
    }
  }

  @OnClick(R.id.user_edit)
  public void onEditClicked() {
    presenter.clickedEdit();
  }

  @OnClick(R.id.user_done)
  public void onDoneClicked() {
    presenter.clickedDone(cardView.getCard());
  }

  @Override
  public void showUser(Card userCard) {
    cardView.setCard(userCard);
  }

  @Override
  public void showBack() {
    toolbar.setEndButton(R.drawable.ic_back_right, R.string.back, v -> presenter.clickedBack());
  }

  @Override
  public void hideBack() {
    toolbar.removeEndButton();
  }

  @Override
  public void showCancel() {
    toolbar.setStartButton(R.drawable.ic_close, R.string.back, v -> presenter.clickedCancel());
  }

  @Override
  public void hideCancel() {
    toolbar.removeStartButton();
  }

  @Override
  public void showEditButton() {
    edit.show();
  }

  @Override
  public void hideEditButton() {
    edit.hide();
  }

  @Override
  public void showDoneButton() {
    done.show();
  }

  @Override
  public void hideDoneButton() {
    done.hide();
  }

  @Override
  public void enableEditMode() {
    cardView.enableEditMode();
  }

  @Override
  public void disabledEditMode() {
    cardView.disabledEditMode();
  }

  @Override
  public void openHome() {
    Intent intent = HomeActivity.Factory.getIntent(this);
    startActivityWithAnimation(intent);
  }

  @Override
  public void close() {
    finishWithAnimation();
  }

  @Override
  public void onPickAvatar() {
    avatarPicker.startPicker(this);
  }

  @Override
  public void onChange(Card updatedCard) {
    presenter.onCardChanged(updatedCard);
  }
}
