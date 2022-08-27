/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import net.mm2d.color.ColorUtils

import javax.swing.BoxLayout
import javax.swing.JPanel

/**
 * RGBおよびHSVの値をスライダーで操作するパネル
 */
class SliderPanel : JPanel() {
    private val hsvPanels = arrayOf( // HSVの操作
        SliderSpinnerPanel("H", 0, 0, 360),
        SliderSpinnerPanel("S", 255, 0, 255),
        SliderSpinnerPanel("V", 255, 0, 255)
    )
    private val rgbPanels = arrayOf( // RGBの操作
        SliderSpinnerPanel("R", 255, 0, 255),
        SliderSpinnerPanel("G", 0, 0, 255),
        SliderSpinnerPanel("B", 0, 0, 255)
    )
    var onHsvChangeListener: ((h: Float, s: Float, v: Float) -> Unit)? = null

    /**
     * インスタンス作成
     */
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        hsvPanels.forEach {
            add(it)
            it.onColorChangeListener = { onHsvChanged() }
        }
        rgbPanels.forEach {
            add(it)
            it.onColorChangeListener = { onRgbChanged() }
        }
    }

    /**
     * HSVの変化をRGBに連動させる
     */
    private fun onHsvChanged() {
        val rgb = ColorUtils.hsvToRgb(hsvPanels[0].value, hsvPanels[1].value, hsvPanels[2].value)
        for (i in 0..2) {
            rgbPanels[i].value = rgb[i]
        }
        performHsvChange()
    }

    /**
     * RGBの変化をHSVに連動させる
     */
    private fun onRgbChanged() {
        val hsv = ColorUtils.rgbToHsv(rgbPanels[0].value, rgbPanels[1].value, rgbPanels[2].value)
        for (i in 0..2) {
            hsvPanels[i].value = hsv[i]
        }
        performHsvChange()
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param h H
     * @param s S
     * @param v V
     */
    fun setHsv(h: Float, s: Float, v: Float) {
        val hsv = floatArrayOf(h, s, v)
        val rgb = ColorUtils.hsvToRgb(h, s, v)
        for (i in 0..2) {
            hsvPanels[i].value = hsv[i]
            rgbPanels[i].value = rgb[i]
        }
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param r R
     * @param g G
     * @param b B
     */
    fun setRgb(r: Float, g: Float, b: Float) {
        val rgb = floatArrayOf(r, g, b)
        val hsv = ColorUtils.rgbToHsv(rgb)
        for (i in 0..2) {
            hsvPanels[i].value = hsv[i]
            rgbPanels[i].value = rgb[i]
        }
        performHsvChange()
    }

    /**
     * 値変化をリスナー通知
     */
    private fun performHsvChange() {
        onHsvChangeListener?.invoke(hsvPanels[0].value, hsvPanels[1].value, hsvPanels[2].value)
    }
}
