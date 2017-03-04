package tim.rijckaert.be.betterbottombar

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color.*
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
class BetterBottomBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BottomNavigationView(context, attrs, defStyle) {

    private val CENTER_Y by lazy { height / 2 }
    private val BOTTOM_BAR_HEIGHT_DP = 56
    private val ANIMATION_DURATION = 600L
    private val START_RADIUS = 0F
    private val ACCESSIBILITY_VIEW = "mItemData"
    private val INVALID_REFERENCE = 0

    private var indexOfChild = 0
    private var colorIntArray = intArrayOf(RED, GREEN, BLUE, RED, GREEN, BLUE)
    private var contentDescriptionTitles = emptyArray<String>()
    private var overlayView: View? = null
        get() {
            removeView(field)

            field = View(context).apply {
                backgroundColor = colorIntArray[indexOfChild]
                layoutParams = android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, dip(BOTTOM_BAR_HEIGHT_DP).toInt())
            }

            addView(field, INVALID_REFERENCE)
            return field
        }

    init {
        val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.BetterBottomBar, INVALID_REFERENCE, INVALID_REFERENCE)
        initializeColors(styledAttributes)
        initializeAccessibilityTextTitles(styledAttributes)
        styledAttributes.recycle()

        setWillNotDraw(true)
        prepareBottomNavigationItems()
    }

    private fun initializeAccessibilityTextTitles(styledAttributes: TypedArray) {
        val titles = styledAttributes.getResourceId(R.styleable.BetterBottomBar_accessibilityTitles, INVALID_REFERENCE)
        if (titles != INVALID_REFERENCE) {
            contentDescriptionTitles = resources.getStringArray(titles)
        }
    }

    private fun initializeColors(styledAttributes: TypedArray) {
        val colors = styledAttributes.getResourceId(R.styleable.BetterBottomBar_colors, INVALID_REFERENCE)
        if (colors != INVALID_REFERENCE) {
            colorIntArray = resources.getIntArray(colors)
        }
    }

    private fun prepareBottomNavigationItems() {
        val navigationItemViews =
                getAllViewGroups()
                        .filter { it is BottomNavigationItemView }
                        .filterNotNull()

        setContentDescriptions(navigationItemViews)

        navigationItemViews.forEach { btmNavItem ->
            btmNavItem.setOnClickListener {
                val selectedTabIndex = indexOfClickedViewChild(it)
                setContentDescriptions(navigationItemViews, selectedTabIndex)
                announceForAccessibility(it.contentDescription)
                with(createRevealAnimator(it)) {
                    start()

                    //does not work for now
                    //addListener(BetterBottomBarCircularRevealListener(overlayView, this@BetterBottomBar, colorIntArray[selectedTabIndex]))
                }
            }
        }
    }

    private fun createRevealAnimator(clickedView: View): Animator {
        indexOfChild = indexOfClickedViewChild(clickedView)
        val clickedViewRectXPos = Rect()
        clickedView.getGlobalVisibleRect(clickedViewRectXPos)
        val xPosClickedView = clickedViewRectXPos.left

        val xPos = xPosClickedView + (clickedView.width / 2)
        val remainingWidth = (width - xPos)
        return createCircularReveal(
                overlayView,
                xPos,
                CENTER_Y,
                START_RADIUS,
                xPos.coerceAtLeast(remainingWidth).toFloat()
        ).apply { duration = ANIMATION_DURATION }
    }

    private fun indexOfClickedViewChild(clickedView: View) = (clickedView.parent as? ViewGroup)?.indexOfChild(clickedView) ?: INVALID_REFERENCE

    private fun setContentDescriptions(navigationItemViews: List<ViewGroup>, selectedTabIndex: Int = 0) {
        navigationItemViews.forEach { viewGroup ->
            val btmNavItem = viewGroup as BottomNavigationItemView

            val title = getAccessibilityTitle(btmNavItem)
            val isSelectedText = if (btmNavItem.isSelected || btmNavItem.itemPosition == selectedTabIndex) "geseleecteerd" else ""

            btmNavItem.contentDescription = "$title tab ${btmNavItem.itemPosition + 1} van ${navigationItemViews.size} $isSelectedText"
        }
    }

    fun getAccessibilityTitle(btmNavItem: BottomNavigationItemView): String =
            if (contentDescriptionTitles.isNotEmpty()) {
                contentDescriptionTitles[btmNavItem.itemPosition]
            } else {
                btmNavItem.javaClass.getDeclaredField(ACCESSIBILITY_VIEW).isAccessible = true
                btmNavItem.itemData.title.toString()
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