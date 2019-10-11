package io.bloco.cardcase.presentation;

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
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.bloco.cardcase.helpers.AssertCurrentActivity.assertCurrentActivity;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

  @Rule
  public final ActivityTestRule<HomeActivity> activityTestRule =
      new ActivityTestRule<>(HomeActivity.class);
  private CardFactory cardFactory;

  @Before
  public void setupFactory() {
    cardFactory = new CardFactory(activityTestRule);
  }

  @Test
  public void testFirstOpen() {
    assertCurrentActivity(WelcomeActivity.class);
  }

  @Test
  public void testOpenWithUserCard() {
    createUserCard();
    launchApp();
    assertCurrentActivity(HomeActivity.class);
  }

  @Test
  public void testReceivedCards(){
    Card card1 = createReceivedCard();
    Card card2 = createReceivedCard();

    createUserCard();
    launchApp();

    onView(withText(card1.getName())).check(matches(isDisplayed()));
    onView(withText(card2.getName())).check(matches(isDisplayed()));
  }

  @Test
  public void testViewCardDetails() {
    Card card = createReceivedCard();

    createUserCard();
    launchApp();

    onView(withText(card.getName())).perform(click());

    onView(withId(R.id.card_avatar)).check(matches(isDisplayed()));
    onView(withId(R.id.card_name)).check(matches(withText(card.getName())));
    onView(withId(R.id.card_email)).check(matches(withText(card.getEmail())));
    onView(withId(R.id.card_phone)).check(matches(withText(card.getPhone())));
    onView(withId(R.id.card_time)).check(matches(withText(startsWith("Ad"))));
  }

  @After
  public void cleanUp() {
    cardFactory.clear();
  }

  private void launchApp() {
    activityTestRule.launchActivity(
        HomeActivity.Factory.getIntent(getInstrumentation().getTargetContext()));
  }

  @SuppressWarnings("UnusedReturnValue")
  private Card createUserCard() {
    return cardFactory.createUserCard();
  }

  private Card createReceivedCard() {
    return cardFactory.createReceivedCard();
  }
}
