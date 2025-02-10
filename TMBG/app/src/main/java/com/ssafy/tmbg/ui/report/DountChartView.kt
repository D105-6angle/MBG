package com.ssafy.tmbg.ui.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.report.SatisfactionData

class DountChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var data: List<SatisfactionData> = emptyList()
    private val defaultSize = context.resources.getDimensionPixelSize(R.dimen.default_donut_size)
    private val strokeWidth = context.resources.getDimensionPixelSize(R.dimen.donut_stroke_width)

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(defaultSize, widthMeasureSpec)
        val height = resolveSize(defaultSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidth / 2f
        rectF.set(
            padding,
            padding,
            w.toFloat() - padding,
            h.toFloat() - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var startAngle = -90f
        data.forEach { item ->
            paint.color = item.type.color
            val sweepAngle = (item.percentage / 100f) * 360f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
    }

    fun setData(newData: List<SatisfactionData>) {
        data = newData
        invalidate()
    }
}