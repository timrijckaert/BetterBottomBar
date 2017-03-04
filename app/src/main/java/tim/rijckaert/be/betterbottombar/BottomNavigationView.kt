package tim.rijckaert.be.betterbottombar

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.childrenSequence
import org.jetbrains.anko.dip

@SuppressLint("NewApi")
class BottomNavigationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BottomNavigationView(context, attrs, defStyle) {

    private var overlayView: View? = null
        get() {
            removeView(field)

            if (field == null) {
                field = View(context).apply {
                    backgroundColor = android.graphics.Color.BLACK
                    layoutParams = android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, dip(BOTTOM_BAR_HEIGHT_DP).toInt())
                }
            }

            addView(field, 0)
            return field
        }

    private val CENTER_Y by lazy { height / 2 }
    private val BOTTOM_BAR_HEIGHT_DP = 56
    private val ANIMATION_DURATION = 6000L
    private val START_RADIUS = 0F
    private val ACCESSIBILITY_VIEW = "mItemData"

    init {
        setWillNotDraw(true)
        prepareBottomNavigationItems()
    }

    private fun prepareBottomNavigationItems() {
        val viewGroup =
                getAllViewGroups()
                        .filter { it is BottomNavigationItemView }
                        .filterNotNull()

        viewGroup.forEachIndexed { index, btmNavItem ->
            setContentDescription(viewGroup.size, index, btmNavItem as BottomNavigationItemView)
            btmNavItem.setOnClickListener {
                announceForAccessibility(it.contentDescription)
                createRevealAnimator(it)
                        .start()
            }
        }
    }

    private fun createRevealAnimator(clickedView: View): Animator {
        val clickedViewRectXPos = Rect()
        clickedView.getGlobalVisibleRect(clickedViewRectXPos)
        val x = clickedViewRectXPos.left

        return createCircularReveal(
                overlayView,
                x + (clickedView.width / 2),
                CENTER_Y,
                START_RADIUS,
                width.toFloat()
        ).apply { duration = ANIMATION_DURATION }
    }


    /**
     * Yes yes I know it's dirty
     * Fix your shit Google
     */
    private fun setContentDescription(amountOfBottomNavigationItemViews: Int, tabIndex: Int, btnItemView: BottomNavigationItemView) {
        btnItemView.javaClass.getDeclaredField(ACCESSIBILITY_VIEW).isAccessible = true
        btnItemView.contentDescription = "${btnItemView.itemData.title} tab ${tabIndex + 1} van $amountOfBottomNavigationItemViews ${if (btnItemView.isSelected) "geselecteerd" else ""}"
    }
}

//<editor-fold desc="Helper Shit ">
fun ViewGroup.getAllViewGroups(): List<ViewGroup> {
    val innerChilds = mutableListOf<ViewGroup>()

    fun getNestedViewGroup(view: View) {
        if (view is ViewGroup) {
            innerChilds.add(view)
            val elements: List<ViewGroup> = view.getAllViewGroups()
            innerChilds.addAll(elements)
        }
    }

    this.childrenSequence().forEach(::getNestedViewGroup)
    return innerChilds.filterNotNull()
}
//</editor-fold>