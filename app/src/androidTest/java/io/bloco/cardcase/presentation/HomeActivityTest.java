package io.bloco.cardcase.presentation;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.helpers.CardFactory;
import io.bloco.cardcase.presentation.home.HomeActivity;
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.bloco.cardcase.helpers.AssertCurrentActivity.assertCurrentActivity;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4.class) public class HomeActivityTest {

  @Rule public ActivityTestRule<HomeActivity> activityTestRule =
      new ActivityTestRule<>(HomeActivity.class);
  private CardFactory cardFactory;

  @Before public void setupFactory() {
    cardFactory = new CardFactory(activityTestRule);
  }

  @Test public void testFirstOpen() throws Exception {
    assertCurrentActivity(WelcomeActivity.class);
  }

  @Test public void testOpenWithUserCard() throws Exception {
    createUserCard();
    launchApp();
    assertCurrentActivity(HomeActivity.class);
  }

  @Test public void testReceivedCards() throws Exception {
    Card card1 = createReceivedCard();
    Card card2 = createReceivedCard();

    createUserCard();
    launchApp();

    onView(withText(card1.getName())).check(matches(isDisplayed()));
    onView(withText(card2.getName())).check(matches(isDisplayed()));
  }

  @Test public void testViewCardDetails() throws Exception {
    Card card = createReceivedCard();

    createUserCard();
    launchApp();

    onView(withText(card.getName())).perform(click());

    onView(withId(R.id.card_avatar)).check(matches(isDisplayed()));
    onView(withId(R.id.card_name)).check(matches(withText(card.getName())));
    onView(withId(R.id.card_email)).check(matches(withText(card.getEmail())));
    onView(withId(R.id.card_phone)).check(matches(withText(card.getPhone())));
    onView(withId(R.id.card_time)).check(matches(withText(startsWith("Added"))));
  }

  @After public void cleanUp() {
    cardFactory.clear();
  }

  private void launchApp() {
    activityTestRule.launchActivity(
        HomeActivity.Factory.getIntent(getInstrumentation().getContext()));
  }

  private Card createUserCard() {
    return cardFactory.createUserCard();
  }

  private Card createReceivedCard() {
    return cardFactory.createReceivedCard();
  }
}
