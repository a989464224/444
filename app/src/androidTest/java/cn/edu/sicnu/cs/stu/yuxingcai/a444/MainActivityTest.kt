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
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun displaysDefaultPlayerInfo() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        onView(withId(R.id.tv_name_value))
            .check(matches(withText(context.getString(R.string.default_name))))
        onView(withId(R.id.tv_gender_value))
            .check(matches(withText(context.getString(R.string.gender_male))))
        onView(withId(R.id.tv_score_value))
            .check(matches(withText("0")))
    }

    @Test
    fun editButtonOpensProfileEditActivity() {
        onView(withId(R.id.btn_edit_info)).perform(click())
        onView(withId(R.id.et_name)).check(matches(isDisplayed()))
    }

    @Test
    fun playGameButtonIsDisplayed() {
        onView(withId(R.id.btn_play_game)).check(matches(isDisplayed()))
    }
}
