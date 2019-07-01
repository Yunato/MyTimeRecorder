package io.github.yunato.myrecordtimer.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView
import io.github.yunato.myrecordtimer.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TimerTextView(context: Context, attributeSet: AttributeSet) : TextView(context, attributeSet) {
    private val paint: Paint = Paint()
    private val offset: Float = 100F
    private val stdLen: Float = 25F
    private var mode: String = MODE_NONE
    private var time: Long = 0L
    private var now: Long = 0L

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(mode == MODE_NONE) return

        val stdAngle: Float = if(mode == MODE_DOWN){
            if((time - now) <= (time - 999))
                (time - now).toFloat() / (time - 999) * 360.0F
            else
                360.0F
        }else{
            val min = now % (60.0F * 1000L)
            min / (60.0F * 1000L) * 360.0F
        }

        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        val x = width / 2F
        val y = height / 2F
        val r = height / 2F - offset

        // circle
        paint.strokeWidth = 30F
        paint.color = ContextCompat.getColor(context, R.color.colorGray)
        canvas?.drawCircle(x, y, r, paint)

        paint.strokeWidth = 30F
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        if(mode == MODE_DOWN){
            canvas?.drawArc(x-r, y-r, x+r, y+r, -90F, 360.0F - stdAngle, false, paint)
        }else{
            canvas?.drawArc(x-r, y-r, x+r, y+r, -90F, stdAngle, false, paint)
        }

        // line
        paint.strokeWidth = 3F
        for(angle in 0 until 300){
            val correctAngle = (angle / 300.0) * 360.0 + 270.0
            val len = when {
                angle % 25 == 0 -> stdLen + 30F
                angle % 5 == 0 -> stdLen + 15F
                else -> stdLen
            }
            paint.color = if(mode == MODE_DOWN){
                if((630.0F - correctAngle) <= stdAngle) ContextCompat.getColor(context, R.color.colorGray)
                else ContextCompat.getColor(context, R.color.colorAccent)
            }else{
                if((correctAngle - 270.0F) < stdAngle) ContextCompat.getColor(context, R.color.colorAccent)
                else ContextCompat.getColor(context, R.color.colorGray)
            }
            val startPos = getPositionOnCircle(r, 0F, getRadians(correctAngle.toFloat()))
            val endPos = getPositionOnCircle(r - len, 0F, getRadians(correctAngle.toFloat()))
            canvas?.drawLine(startPos[0] + x, startPos[1] + y, endPos[0] + x, endPos[1] + y, paint)
        }


    }

    private fun getPositionOnCircle(x: Float, y: Float, angle: Float): List<Float> = listOf(
        x * cos(angle) - y * sin(angle),
        x * sin(angle) + y * cos(angle)
    )

    private fun getRadians(deg: Float): Float = (deg * PI / 180.0F).toFloat()

    fun setParam(mode: String, time: Long = 0L){
        this.mode = mode
        this.time = time
        this.now = this.time
        invalidate()
    }

    fun updateNowTime(time: Long){
        this.now = time
        invalidate()
    }

    companion object{
        @JvmStatic private val MODE_NONE: String = "io.github.yunato.myrecordtimer.ui.view.MODE_NONE"
        @JvmStatic val MODE_UP: String = "io.github.yunato.myrecordtimer.ui.view.MODE_UP"
        @JvmStatic val MODE_DOWN: String = "io.github.yunato.myrecordtimer.ui.view.MODE_DOWN"
    }
}
