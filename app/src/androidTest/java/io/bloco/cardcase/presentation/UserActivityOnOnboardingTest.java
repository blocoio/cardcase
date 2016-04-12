package io.bloco.cardcase.presentation;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import io.bloco.cardcase.R;
import io.bloco.cardcase.helpers.CardFactory;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.faker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class) public class UserActivityOnOnboardingTest {

  @Rule public ActivityTestRule<UserActivity> activityTestRule =
      new ActivityTestRule<>(UserActivity.class, true, false);
  private CardFactory cardFactory;

  @Before public void setUp() {
    Intent intent = UserActivity.Factory.getOnboardingIntent(getInstrumentation().getContext());
    activityTestRule.launchActivity(intent);
    cardFactory = new CardFactory(activityTestRule);
  }

  @Test public void testBackIsNotVisible() throws Exception {
    onView(withContentDescription(R.string.back)).check(doesNotExist());
  }

  @Test public void testDoneVisibility() throws Exception {
    onView(withId(R.id.user_done)).check(matches(not(isDisplayed())));

    String name = new Faker().name.name();
    onView(withId(R.id.card_name)).perform(typeText(name));
    onView(withId(R.id.user_done)).check(matches(isDisplayed()));

    onView(withId(R.id.card_name)).perform(clearText());
    onView(withId(R.id.user_done)).check(matches(not(isDisplayed())));
  }

  @Test public void testUserDataIsSaved() throws Exception {
    String name = new Faker().name.name();
    onView(withId(R.id.card_name)).perform(typeText(name));
    // closeSoftKeyboard();
    onView(withId(R.id.user_done)).perform(click());

    onView(withContentDescription(R.string.user_card)).perform(click());
    onView(withContentDescription(R.string.user_card)).perform(click());
    onView(withText(name)).check(matches(isDisplayed()));
  }

  @After public void tearDown() {
    cardFactory.clear();
  }
}
