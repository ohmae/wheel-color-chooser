/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color

import kotlin.math.roundToInt

/**
 * HSVやRGBの色空間表現を扱う上でのメソッド
 */
object ColorUtils {
    /**
     * Convert given HSV [0.0f, 1.0f] to RGB FloatArray
     *
     * @param h Hue
     * @param s Saturation
     * @param v Value
     * @return RGB FloatArray
     */
    fun hsvToRgb(h: Float, s: Float, v: Float): FloatArray = toRGB(hsvToColor(h, s, v))

    /**
     * Convert given HSV [0.0f, 1.0f] to color
     *
     * @param h Hue
     * @param s Saturation
     * @param v Value
     * @return color
     */
    fun hsvToColor(h: Float, s: Float, v: Float): Int {
        if (s <= 0.0f) return toColor(v, v, v)
        val hue = h * 6.0f // 計算しやすいように[0.0f, 6.0f]で扱う
        val i = hue.toInt() // hueの整数部
        val f = hue - i // hueの小数部
        var r = v
        var g = v
        var b = v
        when (i) {
            0 -> { // h:[0.0f, 1.0f)
                g *= 1 - s * (1 - f)
                b *= 1 - s
            }
            1 -> { // h:[1.0f, 2.0f)
                r *= 1 - s * f
                b *= 1 - s
            }
            2 -> { // h:[2.0f, 3.0f)
                r *= 1 - s
                b *= 1 - s * (1 - f)
            }
            3 -> { // h:[3.0f, 4.0f)
                r *= 1 - s
                g *= 1 - s * f
            }
            4 -> { // h:[4.0f, 5.0f)
                r *= 1 - s * (1 - f)
                g *= 1 - s
            }
            5 -> { // h:[5.0f, 6.0f)
                g *= 1 - s
                b *= 1 - s * f
            }
            else -> {
                g *= 1 - s * (1 - f)
                b *= 1 - s
            }
        }
        return toColor(r, g, b)
    }

    fun svToMask(s: Float, v: Float): Int {
        val a = 1f - (s * v)
        val g = if (a == 0f) 0f else (v * (1f - s) / a).coerceIn(0f, 1f)
        return toColor(a, g, g, g)
    }

    /**
     * RGB値をHSV表現に変換する
     *
     * @param rgb RGB float配列
     * @return HSV float配列
     */
    fun rgbToHsv(rgb: FloatArray): FloatArray = rgbToHsv(rgb[0], rgb[1], rgb[2])

    /**
     * RGB値をHSV表現に変換する
     *
     * @param r R
     * @param g G
     * @param b B
     * @return HSV float配列
     */
    fun rgbToHsv(r: Float, g: Float, b: Float): FloatArray {
        val max = max(r, g, b)
        val min = min(r, g, b)
        val hsv = FloatArray(3)
        hsv[0] = hue(r, g, b, max, min)
        hsv[1] = max - min
        if (max != 0.0f) {
            hsv[1] /= max
        }
        hsv[2] = max
        return hsv
    }

    private fun max(v1: Float, v2: Float, v3: Float): Float = maxOf(maxOf(v1, v2), v3)

    private fun min(v1: Float, v2: Float, v3: Float): Float = minOf(minOf(v1, v2), v3)

    /**
     * rgbおよびrgbの最大値最小値から色相を計算する
     *
     * @param r   赤
     * @param g   緑
     * @param b   青
     * @param max 最大
     * @param min 最小
     * @return 色相
     */
    private fun hue(r: Float, g: Float, b: Float, max: Float, min: Float): Float {
        var hue = max - min
        if (hue > 0.0f) {
            if (max == r) {
                hue = (g - b) / hue
                if (hue < 0.0f) {
                    hue += 6.0f
                }
            } else if (max == g) {
                hue = 2.0f + (b - r) / hue
            } else {
                hue = 4.0f + (r - g) / hue
            }
        }
        hue = (hue / 6.0f).coerceIn(0.0f, 1.0f)
        return hue
    }

    private fun toColor(r: Float, g: Float, b: Float): Int =
        toColor(r.to8bit(), g.to8bit(), b.to8bit())

    private fun toColor(a: Float, r: Float, g: Float, b: Float): Int =
        toColor(a.to8bit(), r.to8bit(), g.to8bit(), b.to8bit())

    private fun toColor(r: Int, g: Int, b: Int): Int =
        0xff shl 24 or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    private fun toColor(a: Int, r: Int, g: Int, b: Int): Int =
        0xff and a shl 24 or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    /**
     * int値で表現されたRGB値をint[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB int配列
     */
    fun toRGBInt(color: Int): IntArray =
        intArrayOf(
                0xff and color.ushr(16),
                0xff and color.ushr(8),
                0xff and color
        )

    /**
     * int値で表現されたRGB値をfloat[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB float配列
     */
    fun toRGB(color: Int): FloatArray =
        floatArrayOf(
                color.ushr(16).toRatio(),
                color.ushr(8).toRatio(),
                color.toRatio()
        )
}

/**
 * Overwrite alpha value of color
 *
 * @receiver color
 * @param alpha Alpha
 * @return alpha applied color
 */
fun Int.setAlpha(alpha: Float): Int = setAlpha((0xff * alpha.coerceIn(0f, 1f)).toInt())

/**
 * Overwrite alpha value of color
 *
 * @receiver color
 * @param alpha Alpha
 * @return alpha applied color
 */
fun Int.setAlpha(alpha: Int): Int = this and 0xffffff or (alpha shl 24)

/**
 * Convert [0, 255] to [0.0f, 1.0f]
 *
 * @receiver [0, 255]
 * @return [0.0f, 1.0f]
 */
fun Int.toRatio(): Float = this / 255f

/**
 * Convert [0.0f, 1.0f] to [0, 255]
 *
 * @receiver [0.0f, 1.0f]
 * @return [0, 255]
 */
fun Float.to8bit(): Int = (this * 255f).roundToInt().coerceIn(0, 255)
