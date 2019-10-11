package io.bloco.cardcase.presentation;

import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.helpers.CardFactory;
import io.bloco.cardcase.presentation.home.HomeActivity;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.faker.Faker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class UserActivityOnEditTest {

  @Rule
  public final ActivityTestRule<HomeActivity> initialTestRule =
      new ActivityTestRule<>(HomeActivity.class);

  @Rule
  public final ActivityTestRule<UserActivity> activityTestRule =
      new ActivityTestRule<>(UserActivity.class, true, false);

  private CardFactory cardFactory;
  private Card userCard;

  @Before
  public void setUp() {
    cardFactory = new CardFactory(initialTestRule);
    userCard = cardFactory.createUserCard();

    startActivity();
  }

  @Test
  public void testUserFields() {
    onNameField().check(matches(withText(userCard.getName())));
    onEmailField().check(matches(withText(userCard.getEmail())));
    onPhoneField().check(matches(withText(userCard.getPhone())));
  }

  @Test
  public void testSwitchModes() {
    assertViewMode();
    startEdit();
    assertEditMode();
    cancelEdit();
    assertViewMode();
  }

  @Test
  public void testEditField() {
    String newName = new Faker().name.name();
    startEdit();
    onNameField().perform(clearText()).perform(typeText(newName));
    onDoneButton().perform(click());

    // Restart activity
    activityTestRule.getActivity().finish();
    startActivity();

    onNameField().check(matches(withText(newName)));
  }

  @After
  public void tearDown() {
    if (cardFactory != null) {
      cardFactory.clear();
    }
  }

  private void startActivity() {
    Intent intent = UserActivity.Factory.getIntent(getInstrumentation().getTargetContext());
    activityTestRule.launchActivity(intent);
  }

  private void startEdit() {
    onEditButton().perform(click());
  }

  private void cancelEdit() {
    onCancelButton().perform(click());
  }

  private void assertViewMode() {
    onNameField().check(matches(not(isFocusable())));
    onEmailField().check(matches(not(isFocusable())));
    onPhoneField().check(matches(not(isFocusable())));
    onEditButton().check(matches(isDisplayed()));
    onDoneButton().check(matches(not(isDisplayed())));
    onCancelButton().check(matches(not(isDisplayed())));
  }

  private void assertEditMode() {
    onNameField().check(matches(isFocusable()));
    onEmailField().check(matches(isFocusable()));
    onPhoneField().check(matches(isFocusable()));
    onEditButton().check(matches(not(isDisplayed())));
    onCancelButton().check(matches(isDisplayed()));
  }

  private ViewInteraction onNameField() {
    return onView(withId(R.id.card_name));
  }

  private ViewInteraction onEmailField() {
    return onView(withId(R.id.card_email));
  }

  private ViewInteraction onPhoneField() {
    return onView(withId(R.id.card_phone));
  }

  private ViewInteraction onDoneButton() {
    return onView(withId(R.id.user_done));
  }

  private ViewInteraction onEditButton() {
    return onView(withId(R.id.user_edit));
  }

  private ViewInteraction onCancelButton() {
    return onView(withId(R.id.toolbar_icon_start));
  }
}
