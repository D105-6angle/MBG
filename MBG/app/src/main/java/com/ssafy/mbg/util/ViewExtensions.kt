package com.ssafy.mbg.util

import android.widget.TextView

fun TextView.typeWrite(text: String, intervalMs: Long = 50) {
    this.text = ""
    val textLength = text.length

    for ( i in 0 until  textLength) {
        postDelayed({
            this.text = text.substring(0, i + 1)
        }, intervalMs * i)
    }
}