package io.bloco.cardcase.presentation.user;

import android.app.Activity;

import io.bloco.cardcase.common.analytics.AnalyticsService;
import io.bloco.cardcase.common.di.PerActivity;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.domain.GetUserCard;
import io.bloco.cardcase.domain.SaveUserCard;

import java.util.UUID;

import javax.inject.Inject;

@PerActivity
public class UserPresenter
        implements UserContract.Presenter, GetUserCard.Callback, SaveUserCard.Callback {

    private final GetUserCard getUserCard;
    private final SaveUserCard saveUserCard;
    private final AnalyticsService analyticsService;
    private UserContract.View view;
    private boolean onboarding;
    private Card userCard;
    private Database database;

    @Inject
    public UserPresenter(GetUserCard getUserCard,
                         SaveUserCard saveUserCard,
                         AnalyticsService analyticsService,
                         Database database) {
        this.getUserCard = getUserCard;
        this.saveUserCard = saveUserCard;
        this.analyticsService = analyticsService;
        this.database = database;
    }

    @Override
    public void start(UserContract.View view, boolean onboarding) {
        this.view = view;
        this.onboarding = onboarding;

        if (onboarding) {
            userCard = new Card();
//            userCard.setId(UUID.randomUUID());
            view.showUser(userCard);
            view.enableEditMode();
            showDone();
            analyticsService.trackEvent("Onboarding Screen");
        } else {
            getUserCard.get(this);
            view.showBack();
            view.showEditButton();
            analyticsService.trackEvent("User Screen");
        }
    }

    @Override
    public void clickedBack() {
        view.close();
    }

    @Override
    public void clickedCancel() {
        view.showUser(userCard);
        view.disabledEditMode();
        view.hideDoneButton();
        view.hideCancel();
        view.showBack();
        view.showEditButton();
    }

    @Override
    public void clickedEdit() {
        view.hideEditButton();
        view.hideBack();
        view.showCancel();
        view.enableEditMode();
        analyticsService.trackEvent("User Card Edit");
    }

    @Override
    public void clickedDone(Card updatedCard) {
        view.hideDoneButton();
        view.hideCancel();
        this.userCard = updatedCard;
        saveUserCard.save(userCard, this);
        analyticsService.trackEvent("User Card Save");
        if (onboarding) {
            ((Activity)view).finish();
            this.start(view, false);
        }
    }

    @Override
    public void onCardChanged(Card updatedCard) {
        if (updatedCard.isValid()) {
            view.showDoneButton();
        } else {
            view.hideDoneButton();
        }
    }

    @Override
    public void clickRemoveUserCard() {
        database.deleteCard(userCard);
        userCard = new Card();
//            userCard.setId(UUID.randomUUID());
        view.showUser(userCard);
        view.enableEditMode();
        view.showDoneButton();
    }

    @Override
    public void showDone() {
//        showDone();
    }

    @Override
    public void onGetUserCard(Card userCard) {
        this.userCard = userCard;
        view.showUser(this.userCard);
    }

    @Override
    public void onSaveUserCard(Card savedCard) {
        if (onboarding) {
            view.openHome();
            view.close();
        } else {
            userCard = savedCard;
            view.disabledEditMode();
            view.showBack();
            view.showEditButton();
            view.showUser(userCard);
        }
    }
}
