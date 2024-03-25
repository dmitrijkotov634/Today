package com.wavecat.today.services

import android.view.accessibility.AccessibilityNodeInfo

fun AccessibilityNodeInfo.toText(builder: StringBuilder) {
    repeat(childCount) { index ->
        val node = getChild(index) ?: return@repeat

        if (node.childCount != 0) {
            node.toText(builder)
            return@repeat
        }

        if (node.isVisibleToUser && node.text != null) {
            builder.append(node.text)
            builder.append("\n")
        }
    }
}