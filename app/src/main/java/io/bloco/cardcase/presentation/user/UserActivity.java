package io.bloco.cardcase.presentation.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import android.widget.FrameLayout;

import butterknife.Bind;
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
import io.bloco.cardcase.presentation.home.Theme;


import java.io.File;

import javax.inject.Inject;

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

    @Bind(R.id.user_layout)
    ViewGroup rootLayout;
    @Bind(R.id.user_card)
    CardInfoView cardView;
    @Bind(R.id.user_done)
    FloatingActionButton done;
    @Bind(R.id.fab_main)
    FloatingActionButton fabMain;
    @Bind(R.id.user_edit_fab)
    FloatingActionButton fabEdit;
    @Bind(R.id.user_delete)
    FloatingActionButton fabDelete;
    @Bind(R.id.user_create)
    FloatingActionButton fabCreate;

    private Animation fabOpen, fabClose, rotateBackward, rotateForward;

    private boolean isFabOpen = false;

    public static class Factory {
        public static class BundleArgs {
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

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
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

    private void onFabClick() {
        if (isFabOpen) {
            fabMain.startAnimation(rotateBackward);
            fabEdit.startAnimation(fabClose);
            fabDelete.startAnimation(fabClose);
            fabCreate.startAnimation(fabClose);
            fabEdit.setClickable(false);
            fabCreate.setClickable(false);
            fabDelete.setClickable(false);
            isFabOpen = false;
            Log.d("TEST", "isFabOpen");
        } else {
            presenter.clickedCancel();
            fabMain.startAnimation(rotateForward);
            fabEdit.startAnimation(fabOpen);
            fabDelete.startAnimation(fabOpen);
            fabCreate.startAnimation(fabOpen);
            fabEdit.setClickable(true);
            fabDelete.setClickable(true);
            fabCreate.setClickable(true);
            isFabOpen = true;
            Log.d("TEST", "!isFabOpen");
        }
    }


    @OnClick(R.id.fab_main)
    public void mainFabClick() {
        onFabClick();
    }

    @OnClick(R.id.user_delete)
    public void fab1Click() {
        AlertDialog alert = getDialog().create();
        alert.show();
    }

    private AlertDialog.Builder getDialog() {
        TextView title = new TextView(this);
        title.setText("Remove the card");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(23);

        TextView msg = new TextView(this);
        msg.setText("Are you sure ?");
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(title);
        builder.setView(msg);

        builder.setIcon(R.drawable.ic_delete_forever_white_24px);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                presenter.clickRemoveUserCard();
                enableEditMode();
                fabInvisible();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onFabClick();
                dialog.dismiss();
            }
        });

        return builder;
    }

    @OnClick(R.id.user_create)
    public void fab2Click() {
        //todo adding a new user card. It works, but it's too buggy.
        /*onFabClick();
        this.finish();
        Intent intent = UserActivity.Factory.getOnboardingIntent(this);
        startActivity(intent);
        Log.d("TEST", "user create clicked");*/

        //workaround for the next demo.
        presenter.clickRemoveUserCard();
        enableEditMode();
        fabInvisible();
    }

    @OnClick(R.id.user_edit_fab)
    public void onEditClicked() {
        presenter.clickedEdit();
        fabInvisible();
    }

    private void fabInvisible() {
        fabCreate.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);
        fabEdit.setVisibility(View.GONE);
        fabMain.setVisibility(View.INVISIBLE);
    }

    private void fabVisible() {
        fabCreate.setVisibility(View.INVISIBLE);
        fabDelete.setVisibility(View.INVISIBLE);
        fabEdit.setVisibility(View.INVISIBLE);
        fabMain.setVisibility(View.VISIBLE);
        fabMain.startAnimation(rotateBackward);
    }

    @OnClick(R.id.user_done)
    public void onDoneClicked() {
        presenter.clickedDone(cardView.getCard());
        fabVisible();
        this.finish();
        startActivity(getIntent());

        System.out.println(fabMain.isClickable());
        System.out.println(fabMain.isEnabled());
        System.out.println(fabMain.isFocusable());
    }

    @Override
    public void showUser(Card userCard) {
        cardView.setCard(userCard);
    }

    @Override
    public void showBack() {
        toolbar.setEndButton(R.drawable.ic_back_right, R.string.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clickedBack();
            }
        });
    }

    @Override
    public void hideBack() {
        toolbar.removeEndButton();
    }

    @Override
    public void showCancel() {
        toolbar.setStartButton(R.drawable.ic_close, R.string.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clickedCancel();
            }
        });
    }

    @Override
    public void hideCancel() {
        toolbar.removeStartButton();
    }

    @Override
    public void showEditButton() {
    }

    @Override
    public void hideEditButton() {
    }

    @Override
    public void showDoneButton() {
        done.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDoneButton() {
        done.setVisibility(View.GONE);
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
