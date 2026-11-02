package cn.edu.sicnu.cs.stu.yuxingcai.a444.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.Card
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.sqrt

/**
 * The game's View component: renders the deck of cards on a [Canvas] and
 * reports taps back to the controller through [onCardTap].
 *
 * It only draws state that the model already owns; the controller keeps
 * [cards] in sync and calls [flipTo] to animate individual cards.
 */
class GameBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var cards: List<Card> = emptyList()
        set(value) {
            field = value
            flipProgress = FloatArray(value.size) { if (value[it].isChosen) 1f else 0f }
            invalidate()
        }

    /** Suits that should be drawn in red (hearts / diamonds). */
    var redSuits: Set<String> = emptySet()

    var onCardTap: ((Int) -> Unit)? = null

    private var flipProgress = FloatArray(0)

    private val flipInterpolator = DecelerateInterpolator()

    private val backFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backDecorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val faceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val symbolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val bounds = RectF()
    private val inner = RectF()

    private val cardBackColor = 0xFF3949AB.toInt()
    private val cardBackDecorColor = 0xFFC5CAE9.toInt()
    private val cardFaceColor = 0xFFFFFFFF.toInt()
    private val cardMatchedColor = 0xFFE8F5E9.toInt()
    private val cardBorderColor = 0xFFBDBDBD.toInt()
    private val cardMatchedBorderColor = 0xFF4CAF50.toInt()

    private val redColor = 0xFFD32F2F.toInt()
    private val blackColor = 0xFF212121.toInt()

    /** Animates the card at [index] to the given face state. */
    fun flipTo(index: Int, faceUp: Boolean) {
        if (index !in flipProgress.indices) return
        val start = flipProgress[index]
        val target = if (faceUp) 1f else 0f
        if (start == target) return
        ValueAnimator.ofFloat(start, target).apply {
            duration = FLIP_DURATION_MS
            interpolator = flipInterpolator
            addUpdateListener { animator ->
                flipProgress[index] = animator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cards.isEmpty() || width == 0 || height == 0) return
        val grid = computeGrid()
        for (i in cards.indices) {
            val left = grid.pad + (i % grid.cols) * (grid.cellWidth + grid.gap)
            val top = grid.pad + (i / grid.cols) * (grid.cellHeight + grid.gap)
            drawCard(canvas, i, left, top, grid.cellWidth, grid.cellHeight)
        }
    }

    private fun drawCard(canvas: Canvas, index: Int, left: Float, top: Float, w: Float, h: Float) {
        val progress = flipProgress.getOrElse(index) { if (cards[index].isChosen) 1f else 0f }
        val scaleX = abs(cos(progress * PI.toFloat()))
        val radius = dp(10f)

        canvas.save()
        canvas.translate(left + w / 2f, top + h / 2f)
        canvas.scale(scaleX, 1f)
        if (progress <= 0.5f) {
            drawBack(canvas, -w / 2f, -h / 2f, w, h, radius)
        } else {
            drawFace(canvas, index, -w / 2f, -h / 2f, w, h, radius)
        }
        canvas.restore()
    }

    private fun drawBack(canvas: Canvas, left: Float, top: Float, w: Float, h: Float, radius: Float) {
        bounds.set(left, top, left + w, top + h)
        backFillPaint.color = cardBackColor
        canvas.drawRoundRect(bounds, radius, radius, backFillPaint)

        val inset = min(w, h) * 0.14f
        inner.set(left + inset, top + inset, left + w - inset, top + h - inset)
        backDecorPaint.style = Paint.Style.STROKE
        backDecorPaint.strokeWidth = dp(2f)
        backDecorPaint.color = cardBackDecorColor
        canvas.drawRoundRect(inner, radius * 0.6f, radius * 0.6f, backDecorPaint)

        val cx = left + w / 2f
        val cy = top + h / 2f
        val d = min(w, h) * 0.18f
        val diamond = Path().apply {
            moveTo(cx, cy - d)
            lineTo(cx + d, cy)
            lineTo(cx, cy + d)
            lineTo(cx - d, cy)
            close()
        }
        backDecorPaint.style = Paint.Style.FILL
        canvas.drawPath(diamond, backDecorPaint)
    }

    private fun drawFace(canvas: Canvas, index: Int, left: Float, top: Float, w: Float, h: Float, radius: Float) {
        val card = cards[index]
        bounds.set(left, top, left + w, top + h)
        faceFillPaint.color = if (card.isMatched) cardMatchedColor else cardFaceColor
        canvas.drawRoundRect(bounds, radius, radius, faceFillPaint)

        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = dp(2f)
        borderPaint.color = if (card.isMatched) cardMatchedBorderColor else cardBorderColor
        canvas.drawRoundRect(bounds, radius, radius, borderPaint)

        symbolPaint.color = if (card.suit in redSuits) redColor else blackColor
        symbolPaint.textSize = min(w, h) * 0.42f
        val baseline = top + h / 2f - (symbolPaint.descent() + symbolPaint.ascent()) / 2f
        canvas.drawText(card.faceText, left + w / 2f, baseline, symbolPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        if (cards.isEmpty()) return true
        val grid = computeGrid()
        for (i in cards.indices) {
            val left = grid.pad + (i % grid.cols) * (grid.cellWidth + grid.gap)
            val top = grid.pad + (i / grid.cols) * (grid.cellHeight + grid.gap)
            if (event.x >= left && event.x <= left + grid.cellWidth &&
                event.y >= top && event.y <= top + grid.cellHeight
            ) {
                onCardTap?.invoke(i)
                break
            }
        }
        return true
    }

    private fun computeGrid(): Grid {
        val count = cards.size
        val cols = ceil(sqrt(count.toDouble())).toInt().coerceAtLeast(1)
        val rows = ceil(count.toDouble() / cols).toInt()
        val gap = dp(8f)
        val pad = dp(10f)
        val cellWidth = (width - pad * 2 - gap * (cols - 1)) / cols.toFloat()
        val cellHeight = (height - pad * 2 - gap * (rows - 1)) / rows.toFloat()
        return Grid(cols, rows, gap, pad, cellWidth, cellHeight)
    }

    private data class Grid(
        val cols: Int,
        val rows: Int,
        val gap: Float,
        val pad: Float,
        val cellWidth: Float,
        val cellHeight: Float
    )

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    companion object {
        private const val FLIP_DURATION_MS = 220L
    }
}
