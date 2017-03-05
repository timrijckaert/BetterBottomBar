package tim.rijckaert.be.betterbottombar

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.graphics.drawable.ColorDrawable
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.content.ContextCompat
import be.rijckaert.tim.betterbottombar.MainActivity
import be.rijckaert.tim.betterbottombar.R
import be.vrt.nieuws.util.viewaction.RotationChangeAction
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import tim.rijckaert.be.betterbottombar.robot.BottomBarRobot
import tim.rijckaert.be.betterbottombar.robot.bottomBar
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
@LargeTest
class BetterBottomBarUITest {

    val activityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule val testRule = RuleChain.outerRule(activityTestRule)

    @Test
    fun onStartUpBottomBarHasCorrectBackgroundColor() {
        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.colorPrimary)
                    assertTrue(hasCorrectBackgroundColor)
                }
    }

    @Test
    fun onStartUpBottomBarHasCorrectBackgroundColorEvenAfterRotation() {
        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.colorPrimary)
                    assertTrue(hasCorrectBackgroundColor)
                }

        onView(isRoot())
                .perform(RotationChangeAction(activityTestRule.activity, SCREEN_ORIENTATION_LANDSCAPE))

        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.colorPrimary)
                    assertTrue(hasCorrectBackgroundColor)
                }
    }

    @Test
    fun onChangeTabToVideoBackgroundColorShouldBeRed() {
        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.colorPrimary)
                    assertTrue(hasCorrectBackgroundColor)
                }

        bottomBar {
            clickItem(BottomBarRobot.MenuItems.VIDEO)
        }

        sleep(600)

        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.someReddishColor)
                    assertTrue(hasCorrectBackgroundColor)
                }
    }

    @Test
    fun onChangeTabToVideoBackgroundColorShouldBeRedEventAfterRotation() {
        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.colorPrimary)
                    assertTrue(hasCorrectBackgroundColor)
                }

        bottomBar {
            clickItem(BottomBarRobot.MenuItems.VIDEO)
        }

        onView(isRoot())
                .perform(RotationChangeAction(activityTestRule.activity, SCREEN_ORIENTATION_LANDSCAPE))

        onView(withId(R.id.bottom_navigation))
                .check { view, _ ->
                    val hasCorrectBackgroundColor = (view.background as ColorDrawable).color == ContextCompat.getColor(activityTestRule.activity, R.color.someReddishColor)
                    assertTrue(hasCorrectBackgroundColor)
                }
    }
}