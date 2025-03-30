package com.example.filmsearch

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class RatingDonutView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {
    private val oval = RectF()
    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var stroke = 10f
    private var progress = 0f // Изначально прогресс будет float
    private var scaleSize = 60f
    private lateinit var strokePaint: Paint
    private lateinit var digitPaint: Paint
    private lateinit var circlePaint: Paint

    private fun initPaint() {
        strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        digitPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(5f, 2f, 0f, Color.DKGRAY)
            textSize = scaleSize
            typeface = Typeface.SANS_SERIF
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        circlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.DKGRAY
        }
    }

    private fun getPaintColor(progress: Float): Int = when (progress.toInt()) {
        in 0..25 -> Color.parseColor("#E84258") // Добавлен символ "#"
        in 26..50 -> Color.parseColor("#FD8060") // Добавлен символ "#"
        in 51..75 -> Color.parseColor("#FEE191") // Добавлен символ "#"
        else -> Color.parseColor("#B0D8A4") // Добавлен символ "#"
    }

    init {
        val a = context.theme.obtainStyledAttributes(attributeSet, R.styleable.RatingDonutView, 0, 0)
        try {
            stroke = a.getFloat(R.styleable.RatingDonutView_stroke, stroke)
            progress = a.getInt(R.styleable.RatingDonutView_progress, progress.toInt()).toFloat() // преобразуем в Float
        } finally {
            a.recycle()
        }
        initPaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = if (width > height) {
            height.div(2f)
        } else {
            width.div(2f)
        }
    }
    fun setProgress(pr: Int) {
        //Кладем новове значение в нашу поле класса
        progress = pr.toFloat()
        //Создаем краски с новыми цветами
        initPaint()
        //вызываем перерисовку View
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val choosenHeight = chooseDimension(heightMode, heightSize)

        val minSide = Math.min(chosenWidth, choosenHeight)
        centerX = minSide.div(2f)
        centerY = minSide.div(2f)
        setMeasuredDimension(minSide, minSide)
    }

    private fun chooseDimension(mode: Int, size: Int) =
        when (mode) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
            else -> 300
        }

    private fun drawRating(canvas: Canvas) {
        val scale = radius * 0.8f
        canvas.save()
        canvas.translate(centerX, centerY)
        oval.set(0f - scale, 0f - scale, scale, scale)
        canvas.drawCircle(0f, 0f, radius, circlePaint)
        canvas.drawArc(oval, -90f, convertProgressToDegrees(progress), false, strokePaint)
        canvas.restore()
    }

    private fun convertProgressToDegrees(progress: Float): Float = progress * 3.6f

    private fun drawText(canvas: Canvas) {
        val message = String.format("%.1f", progress / 10f)
        val widths = FloatArray(message.length)
        digitPaint.getTextWidths(message, widths)
        var advance = 0f
        for (width in widths) advance += width
        canvas.drawText(message, centerX - advance / 2, centerY + advance / 4, digitPaint)
    }

    override fun onDraw(canvas: Canvas) {
        drawRating(canvas)
        drawText(canvas)
    }

    // Измененный метод для анимации с использованием Float
    fun animateProgress(progress: Float) {
        val startProgress = this.progress // Начальный прогресс
        val endProgress = progress // Конечный прогресс
        val animation = ObjectAnimator.ofFloat(this, "progress", startProgress, endProgress)

        // Задайте длительность анимации
        animation.duration = 1000 // 1 секунда анимации
        animation.start() // Запуск анимации
    }
}
