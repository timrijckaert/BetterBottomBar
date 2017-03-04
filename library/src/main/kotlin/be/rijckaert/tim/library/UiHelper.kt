package be.rijckaert.tim.library

import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.childrenSequence

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