package be.rijckaert.tim.library

import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.view.View

class BetterBottomBarDefaultBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    private var totalDyConsumed = -1

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: BottomNavigationView, directTargetChild: View?, target: View?,
                                     nestedScrollAxes: Int): Boolean = true

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView, target: View?, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed > 0 && totalDyConsumed < 0) {
            totalDyConsumed = 0
            animateBottomBar(child, coordinatorLayout, ScrollDirection.UP)
        } else if (dyConsumed < 0 && totalDyConsumed > 0) {
            totalDyConsumed = 0
            animateBottomBar(child, coordinatorLayout, ScrollDirection.DOWN)
        }

        totalDyConsumed += dyConsumed
    }

    private fun animateBottomBar(view: View, container: View, direction: ScrollDirection) {
        val yPos = calculateY(view.height, container.height, direction)
        view.animate().y(yPos).start()
    }

    private fun calculateY(measuredHeightView: Int, measuredHeightContainer: Int, scrollDirection: ScrollDirection): Float =
            measuredHeightContainer.plus((measuredHeightView.times(scrollDirection.directionFactor))).toFloat()

    private enum class ScrollDirection(val directionFactor: Int = 1) {
        UP(), DOWN(-1);
    }
}