/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import java.awt.Color
import java.awt.Dimension
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JPanel

/**
 * カラーサンプルを表示する
 *
 * @param count 色数
 */
class ColorSamplePanel(count: Int) : JPanel() {
    private val panelList: MutableList<JPanel>
    private val cellSize: Dimension
    private var sampleCount: Int = 0

    init {
        sampleCount = count
        cellSize = Dimension(30, 17) // 一つの大きさ
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        sampleCount = 12
        panelList = ArrayList(sampleCount)
        for (i in 0 until sampleCount) {
            val panel = JPanel()
            panel.preferredSize = cellSize
            panelList.add(panel)
            add(panel)
        }
    }

    /**
     * 色数を設定する
     *
     * @param count 色数
     */
    private fun setSampleCount(count: Int) {
        when {
            count == sampleCount ->
                return
            count < sampleCount ->
                for (i in count until sampleCount) {
                    remove(i)
                }
            count < panelList.size ->
                for (i in sampleCount until count) {
                    add(panelList[i])
                }
            else -> {
                for (i in sampleCount until panelList.size) {
                    // 確保済みが残っていたら追加
                    add(panelList[i])
                }
                for (i in panelList.size until count) {
                    // 確保しながら追加
                    JPanel().also {
                        it.preferredSize = cellSize
                        panelList.add(it)
                        add(it)
                    }
                }
            }
        }
        sampleCount = count
    }

    /**
     * 色リストを設定
     *
     * @param colors 表示する色
     */
    fun setColors(colors: IntArray) {
        setSampleCount(colors.size)
        colors.indices.forEach {
            panelList[it].background = Color(colors[it])
        }
    }
}
