package cn.edu.sicnu.cs.stu.yuxingcai.a444

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardGameActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CardGameActivity::class.java)

    private fun expectedCardCount(): String {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return context.resources.getInteger(R.integer.game_card_count).toString()
    }

    @Test
    fun showsInitialStats() {
        onView(withId(R.id.tv_score_value)).check(matches(withText("0")))
        onView(withId(R.id.tv_matches_value)).check(matches(withText("0")))
        onView(withId(R.id.tv_remaining_value)).check(matches(withText(expectedCardCount())))
    }

    @Test
    fun recyclerViewAndRestartButtonAreDisplayed() {
        onView(withId(R.id.recycler_cards)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_restart)).check(matches(isDisplayed()))
    }

    @Test
    fun restartButtonResetsStats() {
        onView(withId(R.id.btn_restart)).perform(click())
        onView(withId(R.id.tv_score_value)).check(matches(withText("0")))
        onView(withId(R.id.tv_remaining_value)).check(matches(withText(expectedCardCount())))
    }
}
