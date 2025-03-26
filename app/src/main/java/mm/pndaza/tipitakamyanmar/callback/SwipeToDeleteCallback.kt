package mm.pndaza.tipitakamyanmar.callback

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class SwipeToDeleteCallback(
    context: Context,
    private val onSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)
    private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
    private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
    private val background = ColorDrawable()
    private val backgroundColor = "#f44336".toColorInt() // Red background for delete
    private val clearPaint = android.graphics.Paint().apply { xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if(position == RecyclerView.NO_POSITION) return // Invalid position, do nothing (or handle differently)
        onSwiped(position)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw background
        background.color = backgroundColor
        if (dX > 0) { // Swiping right
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        } else if (dX < 0) { // Swiping left
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else {
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)

        // Calculate icon position
        val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconLeft: Int
        val iconRight: Int
        val iconBottom = iconTop + intrinsicHeight

        if (dX > 0) { // Swiping right
            iconLeft = itemView.left + iconMargin
            iconRight = iconLeft + intrinsicWidth
        } else if (dX < 0) { // Swiping left
            iconRight = itemView.right - iconMargin
            iconLeft = iconRight - intrinsicWidth
        } else { // Unswiped
            iconLeft = 0; iconRight = 0
        }
        deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

        // Draw icon if needed
        if(dX != 0f) { // Only draw if actually swiping
            deleteIcon?.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
