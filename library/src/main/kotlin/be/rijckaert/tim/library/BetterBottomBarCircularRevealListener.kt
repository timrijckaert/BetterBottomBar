package be.rijckaert.tim.library

import android.animation.Animator
import android.util.Log
import android.view.View
import org.jetbrains.anko.backgroundColor
import be.rijckaert.tim.library.BetterBottomBar

class BetterBottomBarCircularRevealListener(private val overlayView : View?,
                                            private val betterBottomBar: BetterBottomBar,
                                            private val color: Int) : AnimatorListenerAdapter() {

    override fun onAnimationEnd(animation: Animator?) {
        Log.d("Animation listener", "Animation is done")
        removeOverlay()
        //setBackgroundColor()
    }

    override fun onAnimationCancel(animation: Animator?) {
        Log.d("Animation listener", "Animation was cancelled")
        //removeOverlay()
        //setBackgroundColor()
    }

    private fun setBackgroundColor() {
        betterBottomBar.backgroundColor = color
    }

    private fun removeOverlay() {
        betterBottomBar.removeView(overlayView)
    }
}