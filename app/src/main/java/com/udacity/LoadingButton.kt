package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState.*
import kotlin.properties.Delegates

private const val LOADING_TEXT_DEFAULT = "Loading"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    //Properties
    private var backgroundButtonColor: Int = 0
    private var progressColor: Int = 0
    private var textColor: Int = 0
    private var defaultText: String = ""
    private var loadingText: String = ""
    private var loadingBackgroundColor: Int = 0
    private var circleProgressColor: Int = 0
    private var currentText = ""
    private var progressIndicator = 0

    // Shapes
    private val rect = Rect()
    private var progressArc = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create("Roboto", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(Completed) { _, _, new ->
        when (new) {
            Loading -> {
                valueAnimator = ValueAnimator.ofInt(0, 1000).apply {
                    addUpdateListener {
                        progressIndicator = animatedValue as Int
                        invalidate()
                    }
                    duration = 10000
                    doOnStart {
                        currentText = loadingText
                        this@LoadingButton.isEnabled = false
                    }

                    doOnEnd {
                        progressIndicator = 0
                        this@LoadingButton.isEnabled = true
                        currentText = defaultText
                    }
                    start()
                }
            }

            Completed -> {
                progressIndicator = 0
                this@LoadingButton.isEnabled = true
                currentText = defaultText
            }

            Clicked -> {
                buttonState = Loading
                this.isEnabled = false
            }
        }
        invalidate()
    }

    init {
        context.withStyledAttributes( attrs, R.styleable.LoadingButton) {
            backgroundButtonColor = getColor(R.styleable.LoadingButton_bgColor, Color.BLUE)
            defaultText = getString(R.styleable.LoadingButton_text) ?: ""
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: LOADING_TEXT_DEFAULT
            textColor = getColor(R.styleable.LoadingButton_txtColor, Color.WHITE)
            progressColor = getColor(R.styleable.LoadingButton_progressColor, Color.RED)
            circleProgressColor = getColor(R.styleable.LoadingButton_circleProgressColor, Color.GREEN)
            loadingBackgroundColor = getColor(R.styleable.LoadingButton_loadingBgColor, Color.BLUE)
            currentText = defaultText
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Background button
        paint.color = backgroundButtonColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //progress arc and progress rect
        if (buttonState == Loading) {
            //rect progress
            paint.color = loadingBackgroundColor
            val progressRect = progressIndicator / 1000f * widthSize
            canvas?.drawRect(0f, 0f, progressRect, heightSize.toFloat(), paint)

            //circle progress
            val sweepAngle = progressIndicator / 1000f * 360f
            paint.color = circleProgressColor
            canvas?.drawArc(progressArc, 0f, sweepAngle, true, paint)
        }

        //text
        paint.color = textColor
        paint.getTextBounds(currentText, 0, currentText.length, rect)
        val centerbutton = measuredHeight.toFloat() / 2 - rect.centerY()
        canvas?.drawText(currentText, widthSize / 2f, centerbutton, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        progressArc = drawRecF()
    }

    private fun drawRecF() =
        RectF(
            widthSize - 100f,
            heightSize / 2 - 25f,
            widthSize.toFloat() - 50f,
            heightSize / 2 + 25f
        )

    fun startLoading() {
        buttonState = Loading
    }

    fun setDelayedCompleted() {
        val fraction = valueAnimator.animatedFraction
        valueAnimator.cancel()
        valueAnimator.setCurrentFraction(fraction+0.1f)
        valueAnimator.duration = 1000
        valueAnimator.start()
    }
}