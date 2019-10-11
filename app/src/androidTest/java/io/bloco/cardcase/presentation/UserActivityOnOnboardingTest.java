package io.bloco.cardcase.presentation;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bloco.cardcase.R;
import io.bloco.cardcase.helpers.CardFactory;
import io.bloco.cardcase.presentation.user.UserActivity;
import io.bloco.faker.Faker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class UserActivityOnOnboardingTest {

  @Rule
  public final ActivityTestRule<UserActivity> activityTestRule =
      new ActivityTestRule<>(UserActivity.class, true, false);
  private CardFactory cardFactory;

  @Before
  public void setUp() {
    Intent intent = UserActivity.Factory.getOnboardingIntent(getInstrumentation().getTargetContext());
    activityTestRule.launchActivity(intent);
    cardFactory = new CardFactory(activityTestRule);
  }

  @Test
  public void testBackIsNotVisible() {
    onView(withContentDescription(R.string.back)).check(doesNotExist());
  }

  @Test
  public void testDoneVisibility() throws Exception {
    onView(withId(R.id.user_done)).check(matches(not(isDisplayed())));

    String name = new Faker().name.name();
    onView(withId(R.id.card_name)).perform(typeText(name));
    onView(withId(R.id.user_done)).check(matches(isDisplayed()));

    onView(withId(R.id.card_name)).perform(clearText());
    Thread.sleep(250);
    onView(withId(R.id.user_done)).check(matches(not(isDisplayed())));
  }

  @Test
  public void testUserDataIsSaved() {
    String name = new Faker().name.name();
    onView(withId(R.id.card_name)).perform(typeText(name));
    // closeSoftKeyboard();
    onView(withId(R.id.user_done)).perform(click());

    onView(withContentDescription(R.string.user_card)).perform(click());
    onView(withContentDescription(R.string.user_card)).perform(click());
    onView(withText(name)).check(matches(isDisplayed()));
  }

  @After
  public void tearDown() {
    cardFactory.clear();
  }
}
