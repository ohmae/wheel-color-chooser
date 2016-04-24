/**
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import net.mm2d.color.ColorUtils;
import net.mm2d.wcc.SliderSpinnerPanel.OnValueChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * RGBおよびHSVの値をスライダーで操作するパネル
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class SliderPanel extends JPanel {
    private final SliderSpinnerPanel[] mHsv = new SliderSpinnerPanel[3]; // HSVの操作
    private final SliderSpinnerPanel[] mRgb = new SliderSpinnerPanel[3]; // RGBの操作
    private OnHsvChangeListener mListener;

    /**
     * 値変化のリスナー
     */
    public interface OnHsvChangeListener {
        /**
         * 値が変化したときコール
         *
         * @param hue 色相
         * @param saturation 彩度
         * @param value 輝度
         */
        public void onHsvChange(float hue, float saturation, float value);
    }

    /**
     * インスタンス作成
     */
    public SliderPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mHsv[0] = new SliderSpinnerPanel("H", 0, 0, 360);
        mHsv[1] = new SliderSpinnerPanel("S", 255, 0, 255);
        mHsv[2] = new SliderSpinnerPanel("V", 255, 0, 255);
        mRgb[0] = new SliderSpinnerPanel("R", 255, 0, 255);
        mRgb[1] = new SliderSpinnerPanel("G", 0, 0, 255);
        mRgb[2] = new SliderSpinnerPanel("B", 0, 0, 255);
        final OnValueChangeListener hsvListener = new OnValueChangeListener() {
            @Override
            public void onValueChanged() {
                onHsvChanged();
            }
        };
        final OnValueChangeListener rgbListener = new OnValueChangeListener() {
            @Override
            public void onValueChanged() {
                onRgbChanged();
            }
        };
        for (final SliderSpinnerPanel p : mHsv) {
            add(p);
            p.setOnColorChangeListener(hsvListener);
        }
        for (final SliderSpinnerPanel p : mRgb) {
            add(p);
            p.setOnColorChangeListener(rgbListener);
        }
    }

    /**
     * HSVの変化をRGBに連動させる
     */
    private void onHsvChanged() {
        final float[] hsv = new float[3];
        for (int i = 0; i < 3; i++) {
            hsv[i] = mHsv[i].getValue();
        }
        final float[] rgb = ColorUtils.convertHsvToRgb(hsv);
        for (int i = 0; i < 3; i++) {
            mRgb[i].setValue(rgb[i]);
        }
        performHsvChange();
    }

    /**
     * RGBの変化をHSVに連動させる
     */
    private void onRgbChanged() {
        final float[] rgb = new float[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = mRgb[i].getValue();
        }
        final float[] hsv = ColorUtils.convertRgbToHsv(rgb);
        for (int i = 0; i < 3; i++) {
            mHsv[i].setValue(hsv[i]);
        }
        performHsvChange();
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param h H
     * @param s S
     * @param v V
     */
    public void setHsv(float h, float s, float v) {
        setHsv(h, s, v, false);
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param h H
     * @param s S
     * @param v V
     * @param notify リスナー通知する場合true
     */
    public void setHsv(float h, float s, float v, boolean notify) {
        final float[] hsv = new float[3];
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
        final float[] rgb = ColorUtils.convertHsvToRgb(hsv);
        for (int i = 0; i < 3; i++) {
            mHsv[i].setValue(hsv[i]);
            mRgb[i].setValue(rgb[i]);
        }
        if (notify) {
            performHsvChange();
        }
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param r R
     * @param g G
     * @param b B
     */
    public void setRgb(float r, float g, float b) {
        setRgb(r, g, b, false);
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param r R
     * @param g G
     * @param b B
     * @param notify リスナー通知する場合true
     */
    public void setRgb(float r, float g, float b, boolean notify) {
        final float[] rgb = new float[3];
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
        final float[] hsv = ColorUtils.convertRgbToHsv(rgb);
        for (int i = 0; i < 3; i++) {
            mHsv[i].setValue(hsv[i]);
            mRgb[i].setValue(rgb[i]);
        }
        if (notify) {
            performHsvChange();
        }
    }

    /**
     * 現在のHSVの値を返す
     *
     * @return HSV
     */
    public float[] getHsv() {
        final float[] hsv = new float[3];
        for (int i = 0; i < 3; i++) {
            hsv[i] = mHsv[i].getValue();
        }
        return hsv;
    }

    /**
     * 現在のRGBの値を返す
     *
     * @return RGB
     */
    public float[] getRgb() {
        final float[] rgb = new float[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = mRgb[i].getValue();
        }
        return rgb;
    }

    /**
     * 値変化のリスナーを登録する
     *
     * @param listener リスナー
     */
    public void setOnHsvChangeListener(OnHsvChangeListener listener) {
        mListener = listener;
    }

    /**
     * 値変化をリスナー通知
     */
    private void performHsvChange() {
        if (mListener != null) {
            mListener.onHsvChange(mHsv[0].getValue(), mHsv[1].getValue(), mHsv[2].getValue());
        }
    }
}
