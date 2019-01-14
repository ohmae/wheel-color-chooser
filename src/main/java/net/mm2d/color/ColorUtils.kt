/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color

/**
 * HSVやRGBの色空間表現を扱う上でのメソッド
 */
object ColorUtils {
    /**
     * HSV表現をRGB値に変換する
     *
     * @param hsv HSV
     * @return RGB float配列
     */
    fun convertHsvToRgb(hsv: FloatArray): FloatArray {
        return convertHsvToRgb(hsv[0], hsv[1], hsv[2])
    }

    /**
     * HSV表現をRGB値に変換する
     *
     * @param h Hue
     * @param s Saturation
     * @param v Value
     * @return RGB float配列
     */
    fun convertHsvToRgb(h: Float, s: Float, v: Float): FloatArray {
        if (s <= 0.0f) {
            return floatArrayOf(v, v, v)
        }
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
        return floatArrayOf(r, g, b)
    }

    /**
     * RGB値をHSV表現に変換する
     *
     * @param rgb RGB float配列
     * @return HSV float配列
     */
    fun convertRgbToHsv(rgb: FloatArray): FloatArray {
        return convertRgbToHsv(rgb[0], rgb[1], rgb[2])
    }

    /**
     * RGB値をHSV表現に変換する
     *
     * @param r R
     * @param g G
     * @param b B
     * @return HSV float配列
     */
    fun convertRgbToHsv(r: Float, g: Float, b: Float): FloatArray {
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

    private fun max(v1: Float, v2: Float, v3: Float): Float {
        val max = if (v1 > v2) v1 else v2
        return if (max > v3) max else v3
    }

    private fun min(v1: Float, v2: Float, v3: Float): Float {
        val min = if (v1 < v2) v1 else v2
        return if (min < v3) min else v3
    }

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
        hue = clamp(hue / 6.0f, 0.0f, 1.0f)
        return hue
    }

    /**
     * float配列のRGB値からint表現に変換する
     *
     * @param rgb RGB配列
     * @return RGB int配列
     */
    fun toColor(rgb: FloatArray): Int {
        return toColor(rgb[0], rgb[1], rgb[2])
    }

    /**
     * float値で表現されたRGB値からint表現に変換する
     *
     * @param r 赤
     * @param g 緑
     * @param b 青
     * @return RGB int配列
     */
    private fun toColor(r: Float, g: Float, b: Float): Int {
        return toColor(to8bit(r), to8bit(g), to8bit(b))
    }

    /**
     * RGBそれぞれの値を一つのint値表現に変換する
     *
     * @param r 赤
     * @param g 緑
     * @param b 青
     * @return RGB int配列
     */
    private fun toColor(r: Int, g: Int, b: Int): Int {
        return 0xff shl 24 or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)
    }

    /**
     * int値で表現されたRGB値をint[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB int配列
     */
    fun toRGBInt(color: Int): IntArray {
        return intArrayOf(
                0xff and color.ushr(16),
                0xff and color.ushr(8),
                0xff and color
        )
    }

    /**
     * int値で表現されたRGB値をfloat[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB float配列
     */
    fun toRGB(color: Int): FloatArray {
        return floatArrayOf(
                toFloat(color.ushr(16)),
                toFloat(color.ushr(8)),
                toFloat(color)
        )
    }

    /**
     * 色にアルファ値を加える。
     *
     * @param color 色
     * @param alpha アルファ値[0.0, 1.0]
     * @return ARGB int値
     */
    fun setAlpha(color: Int, alpha: Float): Int {
        return setAlpha(color, (0xff * clamp(alpha, 0.0f, 1.0f)).toInt())
    }

    /**
     * 色にアルファ値を加える。
     *
     * @param color 色
     * @param alpha アルファ値[0, 255]
     * @return ARGB int値
     */
    fun setAlpha(color: Int, alpha: Int): Int {
        return color and 0xffffff or (alpha shl 24)
    }

    /**
     * 8bit値を[0.0, 1.0]のfloat表現に変換する
     *
     * @param value 8bit値[0, 0xff]
     * @return float値[0.0, 1.0]
     */
    fun toFloat(value: Int): Float {
        return clamp((value and 0xff) / 255.0f, 0.0f, 1.0f)
    }

    /**
     * [0.0f, 1.0f]で表現された値を8ビット表現に変換する
     *
     * @param value float値[0.0, 1.0]
     * @return int値
     */
    private fun to8bit(value: Float): Int {
        return clamp((value * 255 + 0.5f).toInt(), 0, 255)
    }

    /**
     * min以下はmin、max以上はmaxに飽和させる
     *
     * @param value 値
     * @param min   最小値
     * @param max   最大値
     * @return 飽和させた値
     */
    fun clamp(value: Int, min: Int, max: Int): Int {
        return Math.min(Math.max(value, min), max)
    }

    /**
     * min以下はmin、max以上はmaxに飽和させる
     *
     * @param value 値
     * @param min   最小値
     * @param max   最大値
     * @return 飽和させた値
     */
    fun clamp(value: Float, min: Float, max: Float): Float {
        return Math.min(Math.max(value, min), max)
    }
}
