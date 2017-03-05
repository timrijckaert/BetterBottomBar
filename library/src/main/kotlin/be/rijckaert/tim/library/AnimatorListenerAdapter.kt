package be.rijckaert.tim.library

import android.animation.Animator

open class AnimatorListenerAdapter : Animator.AnimatorListener {

    companion object {
        fun withCircularRevealListener(onTerminate: (Animator) -> Unit) =
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) = onTerminate.invoke(animation)
                    override fun onAnimationCancel(animation: Animator) = onTerminate.invoke(animation)
                }
    }

    override fun onAnimationRepeat(animation: Animator) {}

    override fun onAnimationEnd(animation: Animator) {}

    override fun onAnimationCancel(animation: Animator) {}

    override fun onAnimationStart(animation: Animator) {}
}