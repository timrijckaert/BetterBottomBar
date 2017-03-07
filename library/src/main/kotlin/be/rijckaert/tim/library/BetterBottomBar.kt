package be.rijckaert.tim.library

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Region
import android.os.Bundle
import android.os.Parcelable
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout.DefaultBehavior
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.widget.TintTypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import be.rijckaert.tim.library.AnimatorListenerAdapter.Companion.withCircularRevealListener
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.topPadding


@SuppressLint("NewApi")
@DefaultBehavior(BetterBottomBarDefaultBehavior::class)
class BetterBottomBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BottomNavigationView(context, attrs, defStyle) {

    private val BOTTOM_BAR_HEIGHT_DP = 56
    private val ANIMATION_DURATION = 300L
    private val START_RADIUS = 0F
    private val ACCESSIBILITY_VIEW = "mItemData"
    private val INVALID_REFERENCE = 0
    private val SELECTED_TAB_INDEX = "be.rijckaert.tim.library.BetterBottomBar.SELECTED_TAB_INDEX"

    var textColors = emptyArray<ColorStateList>()
    var iconColors = emptyArray<ColorStateList>()
    var colors = emptyArray<Int>()
    var contentDescriptionTitles = emptyArray<String>()
    var betterBottomBarClickListener: (BottomNavigationItemView, Int) -> Unit = { btmNav, index -> }

    private val navigationMenu by lazy { getChildAt(0) as BottomNavigationMenuView }
    private var overlayView: View? = null
        get() {
            if (field != null) {
                return field
            }

            field = View(context).apply {
                backgroundColor = currentBackgroundColor
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(BOTTOM_BAR_HEIGHT_DP))
            }

            addView(field, INVALID_REFERENCE)
            return field
        }
    var selectedTab = 0
        private set
    private val currentBackgroundColor
        get() = colors.takeIf { it.isNotEmpty() }?.get(selectedTab) ?: (background as android.graphics.drawable.ColorDrawable).color

    private val SELECTED_ACC_TEXT by lazy { context.getString(R.string.acc_was_selected) }
    private val TAB_ACC_TEXT by lazy { context.getString(R.string.acc_tab) }
    private val OF_ACC_TEXT by lazy { context.getString(R.string.acc_of) }

    private val TOP_PADDING by lazy { dip(3) }
    private val SHADOW_DRAWABLE by lazy { getDrawable(context, R.drawable.shadow) }

    init {
        val styledAttributes = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.BetterBottomBar, 0, 0)
        initializeTextColors(styledAttributes)
        initializeIconColors(styledAttributes)
        initializeColors(styledAttributes)
        initializeAccessibilityTextTitles(styledAttributes)
        styledAttributes.recycle()

        setPadding(0, TOP_PADDING, 0, 0)

        setTextColors()
        setIconColors()
        setWillNotDraw(false)
        prepareBottomNavigationItems()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val newRect = canvas.clipBounds
        newRect.inset(0, -topPadding)
        canvas.clipRect(newRect, Region.Op.REPLACE)

        SHADOW_DRAWABLE.apply {
            setBounds(0, -topPadding, right, 0)
            draw(canvas)
        }
    }

    //<editor-fold desc="View State">
    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()
        return Bundle().apply { putInt(SELECTED_TAB_INDEX, selectedTab) }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(null)
        selectedTab = (state as Bundle).getInt(SELECTED_TAB_INDEX)
        prepareBottomNavigationItems()
        setBackgroundColor()
        setIconColors()
        setTextColors()
    }
//</editor-fold>

    //<editor-fold desc="Styling">
    private fun initializeTextColors(styledAttributes: TintTypedArray) {
        val textColorRes = arrayListOf(
                R.styleable.BetterBottomBar_firstTabTextColors,
                R.styleable.BetterBottomBar_secondTabTextColors,
                R.styleable.BetterBottomBar_thirdTabTextColors,
                R.styleable.BetterBottomBar_fourthTabTextColors,
                R.styleable.BetterBottomBar_fifthTabTextColors
        )

        textColors = textColorRes
                .map { styledAttributes.getColorStateList(it) }
                .toTypedArray()
    }

    private fun initializeIconColors(styledAttributes: TintTypedArray) {
        val iconColorRes = arrayListOf(
                R.styleable.BetterBottomBar_firstTabIconColors,
                R.styleable.BetterBottomBar_secondTabIconColors,
                R.styleable.BetterBottomBar_thirdTabIconColors,
                R.styleable.BetterBottomBar_fourthTabIconColors,
                R.styleable.BetterBottomBar_fifthTabIconColors
        )

        iconColors = iconColorRes
                .map { styledAttributes.getColorStateList(it) }
                .toTypedArray()
    }

    private fun initializeAccessibilityTextTitles(styledAttributes: TintTypedArray) {
        val titles = styledAttributes.getResourceId(R.styleable.BetterBottomBar_contentDescriptionTitles, INVALID_REFERENCE)
        if (titles != INVALID_REFERENCE) {
            contentDescriptionTitles = resources.getStringArray(titles)
        }
    }

    private fun initializeColors(styledAttributes: TintTypedArray) {
        val colors = styledAttributes.getResourceId(R.styleable.BetterBottomBar_colors, INVALID_REFERENCE)
        if (colors != INVALID_REFERENCE) {
            this.colors = resources.getIntArray(colors).toTypedArray()
        }
    }
//</editor-fold>

    //<editor-fold desc="View Modification">
    private fun setIconColors() {
        iconColors.getOrNull(selectedTab)?.let {
            navigationMenu.iconTintList = it
        }
    }

    private fun setTextColors() {
        textColors.getOrNull(selectedTab)?.let {
            navigationMenu.itemTextColor = it
        }
    }

    private fun setBackgroundColor() {
        backgroundColor = currentBackgroundColor
    }
//</editor-fold>

    private fun prepareBottomNavigationItems() {
        val navigationItemViews =
                getAllViewGroups()
                        .filter { it is BottomNavigationItemView }
                        .filterNotNull()

        setContentDescriptions(navigationItemViews)
        navigationItemViews.getOrNull(selectedTab)?.isSelected = true

        navigationItemViews.forEach { btmNavItem ->
            btmNavItem.setOnClickListener {
                val clickedBtmNavItem = btmNavItem as BottomNavigationItemView
                selectedTab = clickedBtmNavItem.itemPosition

                menu.getItem(selectedTab).isChecked = true
                betterBottomBarClickListener(clickedBtmNavItem, selectedTab)

                setContentDescriptions(navigationItemViews)
                announceForAccessibility(it.contentDescription)

                setTextColors()
                setIconColors()

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