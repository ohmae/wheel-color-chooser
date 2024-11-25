/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import java.awt.Color
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class ColorSamplePanel : JPanel() {
    private val cellCache: MutableList<JPanel> = ArrayList()
    private val cellSize: Dimension = Dimension(30, 20) // 一つの大きさ
    private var childCount: Int = 0

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private fun addNewCell() {
        val cell = newCell()
        cellCache.add(cell)
        add(cell)
    }

    private fun newCell(): JPanel = JPanel().also {
        it.preferredSize = cellSize
    }

    private fun ensureRows(rows: Int) {
        when {
            rows == childCount -> return
            rows < childCount -> {
                (rows until childCount).forEach { remove(it) }
            }

            rows < cellCache.size -> {
                (childCount until rows).forEach { add(cellCache[it]) }
            }

            else -> {
                (childCount until cellCache.size).forEach {
                    // 確保済みが残っていたら追加
                    add(cellCache[it])
                }
                repeat(rows - cellCache.size) {
                    addNewCell()
                }
            }
        }
        childCount = rows
    }

    fun setColors(colors: IntArray) {
        ensureRows(colors.size)
        colors.forEachIndexed { index, color ->
            cellCache[index].background = Color(color)
        }
    }
}
