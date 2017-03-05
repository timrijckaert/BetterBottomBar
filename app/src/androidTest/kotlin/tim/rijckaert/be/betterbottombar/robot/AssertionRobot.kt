package tim.rijckaert.be.betterbottombar.robot

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers

open class AssertionRobot(val view: ViewInteraction) {
    fun tapIt(): AssertionRobot {
        view.perform(ViewActions.click())
        return this
    }

    fun isDisplayed(): AssertionRobot {
        view.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        return this
    }

    fun isCompletelyDisplayed(): AssertionRobot {
        view.check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
        return this
    }

    fun isDisplayingAtLeast(minimumVisiblePercentage: Int): AssertionRobot {
        view.check(ViewAssertions.matches(ViewMatchers.isDisplayingAtLeast(minimumVisiblePercentage)))
        return this
    }

    fun doesNotExist(): AssertionRobot {
        view.check(ViewAssertions.doesNotExist())
        return this
    }
}