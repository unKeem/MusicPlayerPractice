package com.example.musicplayer.view.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/*Using itemdecoration is lighter than applying the effect to each item in the xml file.*/
class Decoration() : RecyclerView.ItemDecoration() {

    /*Set spacing between items in RecyclerView*/
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val count = state.itemCount
        val offset = 5

        if (position == 0) {
            outRect.top = offset
        } else if (position == count - 1) {
            outRect.bottom = offset
        } else {
            outRect.top = offset
            outRect.bottom = offset
        }
    }

    /*Add a separator line between items*/
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val widthMargin = 20f
        val height = 2f
        val paint = Paint()
        val left = parent.paddingStart.toFloat()
        val right = (parent.width - parent.paddingEnd).toFloat()
        paint.color = Color.parseColor("#FFC8C8C8")

        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val layouParams = view.layoutParams as RecyclerView.LayoutParams
            val top = view.bottom.toFloat() + layouParams.bottomMargin
            val bottom = top + height
            c.drawRect(left + widthMargin, top, right - widthMargin, bottom, paint)
        }
    }
}