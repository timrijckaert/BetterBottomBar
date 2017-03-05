package be.vrt.nieuws.util.viewaction

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.view.View

abstract class LoopingMainThreadViewAction() : ViewAction {
    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadUntilIdle()
        performAsync(uiController, view)

        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.isEmpty()) { throw RuntimeException("Could not change orientation") }
    }

    abstract fun performAsync(uiController: UiController, view: View)
}