package tim.rijckaert.be.betterbottombar

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*

@SuppressLint("NewApi")
class BottomNavigationView : BottomNavigationView {

    private var heightMeasureSpec: Int = 0
    private var widthMeasureSpec: Int = 0
    val overLayView
        get() = this.context.UI {
                view {
                    layoutParams = android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, dip(57).toInt())
                    backgroundColor = android.graphics.Color.BLACK
                }

        }.view

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setWillNotDraw(true)
        prepareBottomNavigationItems()
    }


    override fun onMeasure(rawWidth: Int, rawHeight: Int) {
        super.onMeasure(rawWidth, rawHeight)

        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY)
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY)

        overLayView.measure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun prepareBottomNavigationItems() {
        val bottomNavigationItemViews =
                getAllViewGroups()
                        .filter { it is BottomNavigationItemView }
                        .filterNotNull()

        bottomNavigationItemViews.forEachIndexed { index, item ->
            setContentDescription(bottomNavigationItemViews.size, index, item as BottomNavigationItemView)
            item.setOnClickListener {
                announceForAccessibility(it)
                doColorAnimation()
            }
        }
    }

    private fun doColorAnimation() {
        addView(overLayView, 0)
    }

    private fun setContentDescription(amountOfBottomNavigationItemViews: Int, index: Int, bottomNavigationItemView: BottomNavigationItemView) {
        bottomNavigationItemView.javaClass.getDeclaredField("mItemData").isAccessible = true
        bottomNavigationItemView.contentDescription = "${bottomNavigationItemView.itemData.title} tab ${index + 1} van $amountOfBottomNavigationItemViews ${if (bottomNavigationItemView.isSelected) "geselecteerd" else ""}"
    }

    private fun announceForAccessibility(it: View) {
        announceForAccessibility(it.contentDescription)
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