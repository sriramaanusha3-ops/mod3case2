package com.example.mod3case2.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mod3case2.R

class ComparisonChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val plainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.purple_bright)
        style = Paint.Style.FILL
    }

    private val ragPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.cyan_accent)
        style = Paint.Style.FILL
    }

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.surface_elevated)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_white)
        textSize = 36f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_secondary)
        textSize = 28f
    }

    private var plainValue: Float = 70f
    private var ragValue: Float = 90f
    private var plainLabel: String = "Plain LLM (70%)"
    private var ragLabel: String = "RAG (90%)"

    fun setValues(plain: Int, rag: Int, plainText: String = "Plain LLM", ragText: String = "RAG") {
        plainValue = plain.coerceIn(0, 100).toFloat()
        ragValue = rag.coerceIn(0, 100).toFloat()
        plainLabel = "$plainText ($plain%)"
        ragLabel = "$ragText ($rag%)"
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val paddingLeft = 32f
        val paddingRight = 32f
        val maxBarWidth = w - paddingLeft - paddingRight
        val cornerRadius = 12f

        val rowHeight = 44f
        val spacing = 28f

        // Row 1: Plain LLM
        var currentY = 40f
        canvas.drawText(plainLabel, paddingLeft, currentY, labelPaint)
        currentY += 16f
        val plainRectTrack = RectF(paddingLeft, currentY, paddingLeft + maxBarWidth, currentY + rowHeight)
        canvas.drawRoundRect(plainRectTrack, cornerRadius, cornerRadius, trackPaint)

        val plainBarWidth = (plainValue / 100f) * maxBarWidth
        val plainRectFill = RectF(paddingLeft, currentY, paddingLeft + plainBarWidth, currentY + rowHeight)
        canvas.drawRoundRect(plainRectFill, cornerRadius, cornerRadius, plainPaint)

        // Row 2: RAG
        currentY += rowHeight + spacing
        canvas.drawText(ragLabel, paddingLeft, currentY, labelPaint)
        currentY += 16f
        val ragRectTrack = RectF(paddingLeft, currentY, paddingLeft + maxBarWidth, currentY + rowHeight)
        canvas.drawRoundRect(ragRectTrack, cornerRadius, cornerRadius, trackPaint)

        val ragBarWidth = (ragValue / 100f) * maxBarWidth
        val ragRectFill = RectF(paddingLeft, currentY, paddingLeft + ragBarWidth, currentY + rowHeight)
        canvas.drawRoundRect(ragRectFill, cornerRadius, cornerRadius, ragPaint)
    }
}
