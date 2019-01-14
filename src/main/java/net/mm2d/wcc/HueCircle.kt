/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import net.mm2d.color.ColorUtils

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

import javax.swing.JPanel
import kotlin.math.roundToInt

/**
 * 色相環を表示操作するクラス
 *
 * @param sampleCount 色数
 */
class HueCircle(private var sampleCount: Int) : JPanel() {
    private val image: BufferedImage = BufferedImage(DIAMETER, DIAMETER, BufferedImage.TYPE_4BYTE_ABGR)
    private val marginTop: Int = (WIDTH - DIAMETER) / 2
    private val marginLeft: Int = (WIDTH - DIAMETER) / 2
    private val centerX: Int = WIDTH / 2
    private val centerY: Int = HEIGHT / 2
    private var hue: Float = 0f
    private var saturation: Float = 1f
    private var value: Float = 1f
    private var reverse = false
    var onHsChangeListener: ((h: Float, s: Float, v: Float) -> Unit)? = null

    /**
     * 選択した色のリストを返す
     *
     * @return 色のリスト
     */
    val colors: IntArray
        get() {
            val colors = IntArray(sampleCount)
            for (i in 0 until sampleCount) {
                val h = decimal(hue + i.toFloat() / sampleCount)
                val rgb = ColorUtils.convertHsvToRgb(h, saturation, value)
                val color = ColorUtils.toColor(rgb)
                if (reverse) {
                    colors[(sampleCount - i) % sampleCount] = color
                } else {
                    colors[i] = color
                }
            }
            return colors
        }

    init {
        makeHSCircle(value)
        preferredSize = Dimension(WIDTH, HEIGHT)
        object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                selectPoint(e.x, e.y)
            }

            override fun mouseDragged(e: MouseEvent) {
                selectPoint(e.x, e.y)
            }
        }.let {
            addMouseListener(it)
            addMouseMotionListener(it)
        }
    }

    /**
     * クリックもしくはドラッグによって色を選択する
     *
     * @param x X座標
     * @param y Y座標
     */
    private fun selectPoint(x: Int, y: Int) {
        val cx = x - centerX
        val cy = centerY - y
        val distance = Math.sqrt((cx * cx + cy * cy).toDouble()).toFloat()
        val h = (getRadian(cx.toDouble(), cy.toDouble()) / (Math.PI * 2)).toFloat()
        val s: Float = if (distance < RADIUS) {
            distance / RADIUS
        } else {
            1.0f
        }
        setHsv(h, s, value, true)
    }

    /**
     * HSVの値を設定する
     *
     * リスナー通知は行わない
     *
     * @param h Hue
     * @param s Saturation
     * @param v Value
     */
    fun setHsv(h: Float, s: Float, v: Float) {
        setHsv(h, s, v, false)
    }

    /**
     * HSVの値を設定する
     *
     * @param h      Hue
     * @param s      Saturation
     * @param v      Value
     * @param notify リスナー通知の有無
     */
    private fun setHsv(h: Float, s: Float, v: Float, notify: Boolean) {
        hue = h
        saturation = s
        if (value != v) {
            value = v
            makeHSCircle(value)
        }
        if (notify) {
            onHsChangeListener?.invoke(h, s, v)
        }
        repaint()
    }

    /**
     * 分割数を設定する
     *
     * @param div 分割数
     */
    fun setDivision(div: Int) {
        sampleCount = div
        repaint()
    }

    /**
     * 向き反転
     *
     * @param reverse 反転するときtrue
     */
    fun setReverse(reverse: Boolean) {
        this.reverse = reverse
    }

    /**
     * 色相環の画像を作成する
     *
     * @param v 明度
     */
    private fun makeHSCircle(v: Float) {
        for (y in 0 until image.height) {
            val cy = RADIUS - y
            for (x in 0 until image.width) {
                val cx = x - RADIUS
                val distance = Math.sqrt((cx * cx + cy * cy).toDouble()).toFloat()
                var color = 0
                if (distance < RADIUS + 1) {
                    val radian = getRadian(cx.toDouble(), cy.toDouble())
                    val h = (radian / (Math.PI * 2)).toFloat()
                    val s = ColorUtils.clamp(distance / RADIUS, 0.0f, 1.0f)
                    val rgb = ColorUtils.convertHsvToRgb(h, s, v)
                    color = ColorUtils.toColor(rgb)
                    val alpha = RADIUS + 1 - distance
                    if (alpha < 1) { // アンチエイリアス
                        color = ColorUtils.setAlpha(color, alpha)
                    }
                }
                image.setRGB(x, y, color)
            }
        }
    }

    /**
     * 小数部を取り出す
     *
     * @param value 実数
     * @return 小数部
     */
    private fun decimal(value: Float): Float {
        return value - value.toInt()
    }

    override fun paint(g: Graphics?) {
        val g2 = g as? Graphics2D ?: return
        g2.background = background
        g2.clearRect(0, 0, width, height)
        g2.drawImage(image, marginLeft, marginTop, this)
        val r = saturation * RADIUS
        // 選択している点を表示
        g2.setXORMode(Color.WHITE)
        for (i in 0 until sampleCount) {
            val a = decimal(hue + i.toFloat() / sampleCount) * 2.0 * Math.PI
            val x = centerX + (Math.cos(a) * r).roundToInt()
            val y = centerY - (Math.sin(a) * r).roundToInt()
            if (i == 0) {
                // 操作点を大きく
                g2.drawRect(x - 3, y - 3, 4, 4)
            } else {
                g2.drawRect(x - 2, y - 2, 2, 2)
            }
        }
    }

    companion object {
        private const val RADIUS = 255
        private const val DIAMETER = RADIUS * 2 + 1
        private const val WIDTH = 520
        private const val HEIGHT = 520

        /**
         * 指定された座標とX軸がなす角度を計算して返す
         *
         * @param x X座標
         * @param y Y座標
         * @return 座標とX軸の角度 radian
         */
        private fun getRadian(x: Double, y: Double): Double {
            if (x == 0.0) {
                // ゼロ除算回避
                return if (y > 0) {
                    Math.PI / 2
                } else {
                    Math.PI * 3 / 2
                }
            }
            return Math.atan(y / x) + when {
                x < 0 -> Math.PI
                y < 0 -> Math.PI * 2
                else -> 0.0
            }
        }
    }
}
