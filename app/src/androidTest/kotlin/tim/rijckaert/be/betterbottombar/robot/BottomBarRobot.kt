package tim.rijckaert.be.betterbottombar.robot;

import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import be.rijckaert.tim.betterbottombar.R
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.AllOf.allOf

fun bottomBar(func: BottomBarRobot.() -> Unit) = BottomBarRobot().apply { func() }

class BottomBarRobot {
    enum class MenuItems(@IdRes val id: Int) {
        NEWS(R.id.bottom_navigation_news),
        VIDEO(R.id.bottom_navigation_video),
        DISCOVER(R.id.bottom_navigation_discover)
    }

    val bottomBar: ViewInteraction by lazy { onView(allOf(withId(R.id.bottom_navigation))) }

    fun clickItem(menuItem: MenuItems) {
        onView(allOf(withId(menuItem.id))).perform(click())
    }

    fun isSelected(menuItem: MenuItems) {
        onView((allOf(withId(R.id.bottom_navigation)))).check { view, _ ->
            if (view is BottomNavigationView) {
                view.menu.getItem(menuItem.id).isChecked
            }
        }
    }

    fun isDisplayed() {
        bottomBar.check(matches(isCompletelyDisplayed()))
    }

    fun isNotDisplayed() {
        bottomBar.check(matches(not(isCompletelyDisplayed())))
    }
}