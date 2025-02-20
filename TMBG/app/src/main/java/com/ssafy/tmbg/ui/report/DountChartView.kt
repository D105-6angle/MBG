package com.ssafy.tmbg.ui.report

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
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

    // 애니메이션 관련 변수
    private var animationProgress = 0f
    private var animator: ValueAnimator? = null

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
            val sweepAngle = (item.percentage / 100f) * 360f * animationProgress
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
    }

    fun setData(newData: List<SatisfactionData>) {
        data = newData
        startAnimation()
    }

    private fun startAnimation() {
        // 이전 애니메이션이 실행 중이면 취소
        animator?.cancel()

        // 새로운 애니메이션 생성 및 시작
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500 // 1.5초 동안 애니메이션 실행
            interpolator = DecelerateInterpolator(1.5f) // 부드러운 감속 효과

            addUpdateListener { animation ->
                animationProgress = animation.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}