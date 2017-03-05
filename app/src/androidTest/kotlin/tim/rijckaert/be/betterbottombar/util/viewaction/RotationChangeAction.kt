package be.vrt.nieuws.util.viewaction

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.view.View
import org.hamcrest.Matcher

class RotationChangeAction(val activity : Activity,
                           var orientation: Int) : LoopingMainThreadViewAction() {

    override fun performAsync(uiController: UiController, view: View) {
        activity.requestedOrientation = orientation
    }

    override fun getDescription(): String = "change orientation to $orientation"

    override fun getConstraints(): Matcher<View> = isRoot()

    companion object {
        fun rotateToLandscape(activity: Activity) : ViewAction = RotationChangeAction(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        fun rotateToPortrait(activity: Activity) : ViewAction = RotationChangeAction(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }
}