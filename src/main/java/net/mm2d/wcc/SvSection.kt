/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import net.mm2d.color.ColorUtils
import net.mm2d.color.clamp
import net.mm2d.color.toRatio

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

import javax.swing.JPanel

/**
 * 輝度彩度を円柱モデル断面で操作するUI
 */
class SvSection : JPanel() {
    private val image = BufferedImage(RANGE + 1, RANGE + 1, BufferedImage.TYPE_4BYTE_ABGR)
    private val marginTop: Int = (WIDTH - RANGE) / 2
    private val marginLeft: Int = (HEIGHT - RANGE) / 2
    private var hue: Float = 0f
    private var saturation: Float = 1f
    private var value: Float = 1f
    private var maxColor: Color = Color.RED
    var onSvChangeListener: ((hue: Float, saturation: Float, value: Float) -> Unit)? = null

    /**
     * インスタンス作成
     */
    init {
        for (y in 0..RANGE) {
            for (x in 0..RANGE) {
                val s = x.toFloat() / RANGE
                val v = (RANGE - y).toFloat() / RANGE
                image.setRGB(x, y, ColorUtils.svToMask(s, v))
            }
        }
        preferredSize = Dimension(WIDTH, HEIGHT)
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                selectPoint(e.x, e.y)
            }
        })
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                selectPoint(e.x, e.y)
            }
        })
    }

    /**
     * マウス操作による値の選択
     *
     * @param x X座標
     * @param y Y座標
     */
    private fun selectPoint(x: Int, y: Int) {
        val s = (x - marginLeft).clamp(0, 255).toRatio()
        val v = (RANGE - (y - marginTop)).clamp(0, 255).toRatio()
        setHsv(hue, s, v, true)
    }

    /**
     * HSVの値を設定する
     *
     * @param h H
     * @param s S
     * @param v V
     */
    fun setHsv(h: Float, s: Float, v: Float) {
        setHsv(h, s, v, false)
    }

    /**
     * HSVの値を設定する
     *
     * @param h H
     * @param s S
     * @param v V
     * @param notify リスナー通知する場合true
     */
    private fun setHsv(h: Float, s: Float, v: Float, notify: Boolean) {
        if (hue != h) {
            hue = h
            maxColor = Color(ColorUtils.hsvToColor(hue, 1f, 1f))
        }
        saturation = s
        value = v
        if (notify) {
            onSvChangeListener?.invoke(h, s, v)
        }
        repaint()
    }


    override fun paint(g: Graphics?) {
        val g2 = g as? Graphics2D ?: return
        g2.background = background
        g2.clearRect(0, 0, width, height)
        g2.color = maxColor
        g2.fillRect(marginLeft, marginTop, image.width, image.height)
        g2.drawImage(image, marginLeft, marginTop, this)
        // 選択している点を描画
        val x = (saturation * RANGE).toInt() + marginLeft
        val y = (RANGE - value * RANGE).toInt() + marginTop
        g2.setXORMode(Color.WHITE)
        g2.drawRect(x - 1, y - 1, 2, 2)
    }

    companion object {
        private const val RANGE = 255
        private const val WIDTH = 260
        private const val HEIGHT = 260
    }
}
