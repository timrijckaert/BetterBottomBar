package tim.rijckaert.be.betterbottombar.util.matcher

import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.view.View
import android.widget.TextView
import org.hamcrest.Description

/**
 * Select a TabView with a specific TextView with a specific text
 */
class TabBarMatcher(@StringRes val titleRes : Int) : BoundedMatcher<View, TextView>(TextView::class.java) {
    override fun describeTo(description: Description) {
        description.appendText("tab with titleRes: $titleRes")
    }

    override fun matchesSafely(item: TextView): Boolean {
        val correctTabTitle = item.text == item.context.getString(titleRes)
        val isCompletelyDisplayed = isCompletelyDisplayed().matches(item)
        val hasTabBarAsParent = item.parent?.parent?.parent is TabLayout
        return correctTabTitle && hasTabBarAsParent && isCompletelyDisplayed
    }
}

fun withTabBar(@StringRes titleRes : Int) = TabBarMatcher(titleRes)