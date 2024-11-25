package net.mm2d.wcc

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class TextLabelArray(columns: Int) : JPanel() {
    private val cellCache: MutableList<JLabel> = ArrayList()
    private val cellSize: Dimension = Dimension(8 * columns, 20) // 一つの大きさ
    private var childCount: Int = 0
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private fun addNewCell() {
        val cell = newCell()
        cellCache.add(cell)
        add(
            JPanel(BorderLayout()).also {
                it.add(cell)
                it.background = Color.WHITE
                it.preferredSize = cellSize
            },
            BorderLayout.CENTER,
        )
    }

    private fun newCell(): JLabel = JLabel().also {
        it.size = cellSize
        font?.let { font ->
            it.font = font
        }
        it.border = BorderFactory.createEmptyBorder(0, 4, 0, 0)
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                clipboard.setContents(StringSelection(it.text), null)
                Toast.show(this@TextLabelArray, "copy to clipboard \"${it.text}\"")
            }
        })
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

    fun setTexts(texts: List<String>) {
        ensureRows(texts.size)
        texts.forEachIndexed { index, text ->
            cellCache[index].text = text
        }
    }
}
