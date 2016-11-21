package io.bloco.cardcase.presentation;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import io.bloco.cardcase.R;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.helpers.CardFactory;
import io.bloco.cardcase.presentation.exchange.ExchangeActivity;
import io.bloco.cardcase.presentation.home.HomeActivity;
import io.bloco.cardcase.presentation.welcome.WelcomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.bloco.cardcase.helpers.AssertCurrentActivity.assertCurrentActivity;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Inject
    Database database;

    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule =
            new ActivityTestRule<>(HomeActivity.class);
    private CardFactory cardFactory;

    @Before
    public void setup() {
        cardFactory = new CardFactory(activityTestRule);
        mDevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void testFirstOpen() throws Exception {
        assertCurrentActivity(WelcomeActivity.class);
    }

    @Test
    public void testOpenWithUserCard() throws Exception {
        createUserCard();
        launchApp();
        assertCurrentActivity(HomeActivity.class);
    }

    @Before
    public void eraseData() {
        cardFactory.clearCategories();
    }

    @Test
    public void testCategories() throws Exception {
        Category category = cardFactory.buildCategory("test");
        Card card = createReceivedCard();
        card.setCategoryId(category.getId());
        cardFactory.updateCard(card);

        createUserCard();
        launchApp();

        onView(withText(card.getName())).check(matches(not(isDisplayed())));
        UiObject categoryObject = mDevice.findObject(new UiSelector().text(category.getName()));
        categoryObject.click();
        onView(withText(card.getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteCard() throws Exception {
        Card card = createReceivedCard();
        Category category = cardFactory.buildCategory("test cat");
        card.setCategoryId(category.getId());
        card.setName("to be deleted");
        cardFactory.updateCard(card);

        createUserCard();
        launchApp();

        UiObject categoryObject = mDevice.findObject(new UiSelector().text(category.getName()));
        categoryObject.click();

        mDevice.findObject(new UiSelector().text(card.getName())).click();
        mDevice.findObject(new UiSelector().descriptionStartsWith("Delete")).click();
        mDevice.findObject(new UiSelector().textStartsWith("Yes")).click();


        while (!mDevice.pressBack()) ;
        mDevice.findObject(new UiSelector().text(category.getName())).click();
        onView(withText(card.getName())).check(doesNotExist());
    }

    @Test
    public void testViewCardDetails() throws Exception {
        Card card = createReceivedCard();
        Category category = cardFactory.buildCategory("yet another cat");
        card.setCategoryId(category.getId());
        cardFactory.updateCard(card);

        createUserCard();
        launchApp();

        mDevice.findObject(new UiSelector().text(category.getName())).click();
        mDevice.findObject(new UiSelector().text(card.getName())).click();

        onView(withId(R.id.card_avatar)).check(matches(isDisplayed()));
        onView(withId(R.id.card_name)).check(matches(withText(card.getName())));
        onView(withId(R.id.card_email)).check(matches(withText(card.getEmail())));
        onView(withId(R.id.card_phone)).check(matches(withText(card.getPhone())));
        onView(withId(R.id.card_time)).check(matches(withText(startsWith("Added"))));
    }

    @Test
    public void testExchangeClicked() throws Exception {
        Card card = createReceivedCard();
        Category category = cardFactory.buildCategory("test category");
        card.setCategoryId(category.getId());
        cardFactory.updateCard(card);

        createUserCard();
        launchApp();

        mDevice.findObject(new UiSelector().text(category.getName())).click();
        mDevice.findObject(new UiSelector().text(card.getName())).click();
        mDevice.findObject(new UiSelector().descriptionStartsWith("Share")).click();


        assertCurrentActivity(ExchangeActivity.class);

        while (!mDevice.pressBack());
        assertCurrentActivity(HomeActivity.class);

    }

    @After
    public void cleanUp() {
        cardFactory.clear();
        cardFactory.clearCategories();
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
