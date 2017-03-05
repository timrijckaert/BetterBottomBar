package be.rijckaert.tim.library

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import be.rijckaert.tim.library.AnimatorListenerAdapter.Companion.withCircularRevealListener
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip

@SuppressLint("NewApi")
class BetterBottomBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BottomNavigationView(context, attrs, defStyle) {

    private val BOTTOM_BAR_HEIGHT_DP = 56
    private val ANIMATION_DURATION = 300L
    private val START_RADIUS = 0F
    private val ACCESSIBILITY_VIEW = "mItemData"
    private val INVALID_REFERENCE = 0
    private val SELECTED_TAB_INDEX = "be.rijckaert.tim.library.BetterBottomBar.SELECTED_TAB_INDEX"

    private var colorIntArray = emptyArray<Int>()
    private var contentDescriptionTitles = emptyArray<String>()
    private var overlayView: View? = null
        get() {
            if (field != null) {
                return field
            }

            field = View(context).apply {
                backgroundColor = currentBackgroundColor
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(BOTTOM_BAR_HEIGHT_DP).toInt())
            }

            addView(field, INVALID_REFERENCE)
            return field
        }
    var selectedTab = 0
        private set
    private val currentBackgroundColor
        get() = colorIntArray.takeIf { it.isNotEmpty() }?.get(selectedTab) ?: (background as ColorDrawable).color

    private val SELECTED_ACC_TEXT by lazy { context.getString(R.string.acc_was_selected) }
    private val TAB_ACC_TEXT by lazy { context.getString(R.string.acc_tab) }
    private val OF_ACC_TEXT by lazy { context.getString(R.string.acc_of) }

    init {
        val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.BetterBottomBar, INVALID_REFERENCE, INVALID_REFERENCE)
        initializeColors(styledAttributes)
        initializeAccessibilityTextTitles(styledAttributes)
        styledAttributes.recycle()

        setWillNotDraw(true)
        prepareBottomNavigationItems()
    }

    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()
        return Bundle().apply { putInt(SELECTED_TAB_INDEX, selectedTab) }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(null)
        selectedTab = (state as Bundle).getInt(SELECTED_TAB_INDEX)
        prepareBottomNavigationItems()
        setBackgroundColor()
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
            colorIntArray = resources.getIntArray(colors).toTypedArray()
        }
    }

    private fun prepareBottomNavigationItems() {
        val navigationItemViews =
                getAllViewGroups()
                        .filter { it is BottomNavigationItemView }
                        .filterNotNull()

        setContentDescriptions(navigationItemViews)
        navigationItemViews[selectedTab].isSelected = true

        navigationItemViews.forEach { btmNavItem ->
            btmNavItem.setOnClickListener {
                selectedTab = (btmNavItem as BottomNavigationItemView).itemPosition
                setContentDescriptions(navigationItemViews)
                announceForAccessibility(it.contentDescription)
                with(createRevealAnimator(it)) {
                    start()
                    addListener(
                            withCircularRevealListener(
                                    onTerminate = {
                                        removeOverlay()
                                        setBackgroundColor()
                                    })
                    )
                }
            }
        }
    }

    private fun removeOverlay() {
        removeView(overlayView)
        overlayView = null
    }

    private fun setBackgroundColor() {
        backgroundColor = currentBackgroundColor
    }

    private fun createRevealAnimator(clickedView: View): Animator {
        val clickedViewRectXPos = Rect()
        clickedView.getGlobalVisibleRect(clickedViewRectXPos)
        val xPosClickedView = clickedViewRectXPos.left

        val xPos = xPosClickedView + (clickedView.width / 2)
        val remainingWidth = (width - xPos)
        return createCircularReveal(
                overlayView,
                xPos,
                height / 2,
                START_RADIUS,
                xPos.coerceAtLeast(remainingWidth).toFloat()
        ).apply { duration = ANIMATION_DURATION }
    }

    private fun setContentDescriptions(navigationItemViews: List<ViewGroup>) {
        navigationItemViews.forEach { viewGroup ->
            val btmNavItem = viewGroup as BottomNavigationItemView

            val title = getAccessibilityTitle(btmNavItem)
            val isSelectedText = if (btmNavItem.isSelected || btmNavItem.itemPosition == selectedTab) SELECTED_ACC_TEXT else ""

            btmNavItem.contentDescription = "$title $TAB_ACC_TEXT ${btmNavItem.itemPosition + 1} $OF_ACC_TEXT ${navigationItemViews.size} $isSelectedText"
        }
    }

    private fun getAccessibilityTitle(btmNavItem: BottomNavigationItemView): String =
            if (contentDescriptionTitles.isNotEmpty()) {
                contentDescriptionTitles[btmNavItem.itemPosition]
            } else {
                btmNavItem.javaClass.getDeclaredField(ACCESSIBILITY_VIEW).isAccessible = true
                btmNavItem.itemData.title.toString()
            }
}