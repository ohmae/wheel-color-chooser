/**
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

/**
 * HSVやRGBの色空間表現を扱う上でのメソッド
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public final class ColorMethod {
    /**
     * HSV表現をRGB値に変換する
     *
     * @param hsv HSV
     * @return RGB float配列
     */
    public static float[] HSVtoRGB(float[] hsv) {
        return HSVtoRGB(hsv[0], hsv[1], hsv[2]);
    }

    /**
     * HSV表現をRGB値に変換する
     *
     * @param h Hue
     * @param s Saturation
     * @param v Value
     * @return RGB float配列
     */
    public static float[] HSVtoRGB(float h, float s, float v) {
        final float[] rgb = new float[3];
        rgb[0] = v;
        rgb[1] = v;
        rgb[2] = v;
        if (s > 0.0f) {
            h *= 6.0f; // 計算しやすいように[0.0f, 6.0f]で扱う
            final int i = (int) h; // hueの整数部
            final float f = h - i; // hueの小数部
            switch (i) {
                default:
                case 0: // h:[0.0f, 1.0f)
                    rgb[1] *= 1 - s * (1 - f);
                    rgb[2] *= 1 - s;
                    break;
                case 1: // h:[1.0f, 2.0f)
                    rgb[0] *= 1 - s * f;
                    rgb[2] *= 1 - s;
                    break;
                case 2: // h:[2.0f, 3.0f)
                    rgb[0] *= 1 - s;
                    rgb[2] *= 1 - s * (1 - f);
                    break;
                case 3: // h:[3.0f, 4.0f)
                    rgb[0] *= 1 - s;
                    rgb[1] *= 1 - s * f;
                    break;
                case 4: // h:[4.0f, 5.0f)
                    rgb[0] *= 1 - s * (1 - f);
                    rgb[1] *= 1 - s;
                    break;
                case 5: // h:[5.0f, 6.0f)
                    rgb[1] *= 1 - s;
                    rgb[2] *= 1 - s * f;
                    break;
            }
        }
        return rgb;
    }

    /**
     * RGB値をHSV表現に変換する
     *
     * @param rgb RGB float配列
     * @return HSV float配列
     */
    public static float[] RGBtoHSV(float[] rgb) {
        return RGBtoHSV(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * RGB値をHSV表現に変換する
     *
     * @param r R
     * @param g G
     * @param b B
     * @return HSV float配列
     */
    public static float[] RGBtoHSV(float r, float g, float b) {
        final float max = getMax(r, g, b);
        final float min = getMin(r, g, b);
        final float[] hsv = new float[3];
        hsv[0] = getHue(r, g, b, max, min);
        hsv[1] = (max - min);
        if (max != 0.0f) {
            hsv[1] /= max;
        }
        hsv[2] = max;
        return hsv;
    }

    /**
     * 3つの値の最大値を返す
     *
     * @param r R
     * @param g G
     * @param b B
     * @return 最大値
     */
    public static float getMax(float r, float g, float b) {
        final float max = r > g ? r : g;
        return max > b ? max : b;
    }

    /**
     * 3つの値の最小値を返す
     *
     * @param r R
     * @param g G
     * @param b B
     * @return 最小値
     */
    public static float getMin(float r, float g, float b) {
        final float min = r < g ? r : g;
        return min < b ? min : b;
    }

    /**
     * rgbおよびrgbの最大値最小値から色相を計算する
     *
     * @param r 赤
     * @param g 緑
     * @param b 青
     * @param max 最大
     * @param min 最小
     * @return 色相
     */
    public static float getHue(float r, float g, float b, float max, float min) {
        float hue = max - min;
        if (hue > 0.0f) {
            if (max == r) {
                hue = (g - b) / hue;
                if (hue < 0.0f) {
                    hue += 6.0f;
                }
            } else if (max == g) {
                hue = 2.0f + (b - r) / hue;
            } else {
                hue = 4.0f + (r - g) / hue;
            }
        }
        hue = clamp(hue / 6.0f, 0.0f, 1.0f);
        return hue;
    }

    /**
     * float配列のRGB値からint表現に変換する
     *
     * @param rgb RGB配列
     * @return RGB int配列
     */
    public static int toColor(float[] rgb) {
        return toColor(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * float値で表現されたRGB値からint表現に変換する
     *
     * @param r 赤
     * @param g 緑
     * @param b 青
     * @return RGB int配列
     */
    public static int toColor(float r, float g, float b) {
        return toColor(to8bit(r), to8bit(g), to8bit(b));
    }

    /**
     * RGBそれぞれの値を一つのint値表現に変換する
     *
     * @param rgb RGB配列
     * @return RGB int配列
     */
    public static int toColor(int[] rgb) {
        return toColor(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * RGBそれぞれの値を一つのint値表現に変換する
     *
     * @param r 赤
     * @param g 緑
     * @param b 青
     * @return RGB int配列
     */
    public static int toColor(int r, int g, int b) {
        return (0xff << 24) | ((0xff & r) << 16) | ((0xff & g) << 8) | (0xff & b);
    }

    /**
     * int値で表現されたRGB値をint[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB int配列
     */
    public static int[] toRGBInt(int color) {
        final int[] rgb = new int[3];
        rgb[0] = 0xff & (color >>> 16);
        rgb[1] = 0xff & (color >>> 8);
        rgb[2] = 0xff & color;
        return rgb;
    }

    /**
     * RGBのint配列からfloat配列に変換
     *
     * @param rgb int配列
     * @return RGB float配列
     */
    public static float[] toRGB(int[] rgb) {
        return toRGB(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * RGBの各int値からfloat配列に変換
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     * @return RGB float配列
     */
    public static float[] toRGB(int r, int g, int b) {
        final float[] rgb = new float[3];
        rgb[0] = toFloat(r);
        rgb[1] = toFloat(g);
        rgb[2] = toFloat(b);
        return rgb;
    }

    /**
     * int値で表現されたRGB値をfloat[3]に変換する。
     * α値は無視される。
     *
     * @param color intで表現されたRGB値
     * @return RGB float配列
     */
    public static float[] toRGB(int color) {
        final float[] rgb = new float[3];
        rgb[0] = toFloat(color >>> 16);
        rgb[1] = toFloat(color >>> 8);
        rgb[2] = toFloat(color);
        return rgb;
    }

    /**
     * 色にアルファ値を加える。
     *
     * @param color 色
     * @param alpha アルファ値[0.0, 1.0]
     * @return ARGB int値
     */
    public static int setAlpha(int color, float alpha) {
        return setAlpha(color, (int) (0xff * clamp(alpha, 0.0f, 1.0f)));
    }

    /**
     * 色にアルファ値を加える。
     *
     * @param color 色
     * @param alpha アルファ値[0, 255]
     * @return ARGB int値
     */
    public static int setAlpha(int color, int alpha) {
        return (color & 0xffffff) | (alpha << 24);
    }

    /**
     * 色相の値[0,360]を[0.0, 1.0]のfloat表現に変換する
     *
     * @param value 色相の値
     * @return float値
     */
    public static float angleToFloat(int value) {
        return clamp(value / 360.0f, 0.0f, 1.0f);
    }

    /**
     * 色相の値[0.0, 1.0]を[0, 360]に変換する
     *
     * @param value 色相の値
     * @return [0, 360]のint型色相
     */
    public static int floatToAngle(float value) {
        return clamp((int) (value * 360.0f + 0.5f), 0, 360);
    }

    /**
     * 8bit値を[0.0, 1.0]のfloat表現に変換する
     *
     * @param value 8bit値[0, 0xff]
     * @return float値[0.0, 1.0]
     */
    public static float toFloat(int value) {
        return clamp((value & 0xff) / 255.0f, 0.0f, 1.0f);
    }

    /**
     * [0.0f, 1.0f]で表現された値を8ビット表現に変換する
     *
     * @param value float値[0.0, 1.0]
     * @return int値
     */
    public static int to8bit(float value) {
        return clamp((int) (value * 255 + 0.5f), 0, 255);
    }

    /**
     * 輝度を求める
     *
     * ・・・なぜかこれだけYUV系・・・
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     * @return 輝度値[0, 255]
     */
    public static int getBrightness(int r, int g, int b) {
        return clamp((int) (r * 0.299 + g * 0.587 + b * 0.114 + 0.5f), 0, 255);
    }

    /**
     * min以下はmin、max以上はmaxに飽和させる
     *
     * @param value 値
     * @param min 最小値
     * @param max 最大値
     * @return 飽和させた値
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * min以下はmin、max以上はmaxに飽和させる
     *
     * @param value 値
     * @param min 最小値
     * @param max 最大値
     * @return 飽和させた値
     */
    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }
}
