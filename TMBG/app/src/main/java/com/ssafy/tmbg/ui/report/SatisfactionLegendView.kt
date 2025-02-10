package com.ssafy.tmbg.ui.report

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.ssafy.tmbg.data.report.SatisfactionData

class SatisfactionLegendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    fun setData(data: SatisfactionData) {
        removeAllViews()

        // 색상 원형
        val colorDot = View(context).apply {
            layoutParams = LayoutParams(8.dp, 8.dp).apply {
                marginEnd = 4.dp
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(data.type.color)
            }
        }
        addView(colorDot)

        // 퍼센트 텍스트
        val percentText = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            text = "%.1f%%".format(data.percentage)
            setTextColor(Color.parseColor("#666666"))
            textSize = 12f
        }
        addView(percentText)
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}