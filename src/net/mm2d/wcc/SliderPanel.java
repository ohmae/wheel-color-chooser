/**
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import net.mm2d.color.ColorMethod;
import net.mm2d.wcc.SliderSpinnerPanel.OnValueChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * RGBおよびHSVの値をスライダーで操作するパネル
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class SliderPanel extends JPanel {
    private final SliderSpinnerPanel[] mHSV = new SliderSpinnerPanel[3]; // HSVの操作
    private final SliderSpinnerPanel[] mRGB = new SliderSpinnerPanel[3]; // RGBの操作
    private OnHSVChangeListener mListener;

    /**
     * 値変化のリスナー
     */
    public interface OnHSVChangeListener {
        /**
         * 値が変化したときコール
         *
         * @param hue 色相
         * @param saturation 彩度
         * @param value 輝度
         */
        public void onHSVChange(float hue, float saturation, float value);
    }

    /**
     * インスタンス作成
     */
    public SliderPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mHSV[0] = new SliderSpinnerPanel("H", 0, 0, 360);
        mHSV[1] = new SliderSpinnerPanel("S", 255, 0, 255);
        mHSV[2] = new SliderSpinnerPanel("V", 255, 0, 255);
        mRGB[0] = new SliderSpinnerPanel("R", 255, 0, 255);
        mRGB[1] = new SliderSpinnerPanel("G", 0, 0, 255);
        mRGB[2] = new SliderSpinnerPanel("B", 0, 0, 255);
        final OnValueChangeListener hsvListener = new OnValueChangeListener() {
            @Override
            public void onValueChanged() {
                onHSVChanged();
            }
        };
        final OnValueChangeListener rgbListener = new OnValueChangeListener() {
            @Override
            public void onValueChanged() {
                onRGBChanged();
            }
        };
        for (final SliderSpinnerPanel p : mHSV) {
            add(p);
            p.setOnColorChangeListener(hsvListener);
        }
        for (final SliderSpinnerPanel p : mRGB) {
            add(p);
            p.setOnColorChangeListener(rgbListener);
        }
    }

    /**
     * HSVの変化をRGBに連動させる
     */
    private void onHSVChanged() {
        final float[] hsv = new float[3];
        for (int i = 0; i < 3; i++) {
            hsv[i] = mHSV[i].getValue();
        }
        final float[] rgb = ColorMethod.HSVtoRGB(hsv);
        for (int i = 0; i < 3; i++) {
            mRGB[i].setValue(rgb[i]);
        }
        performHSVChange();
    }

    /**
     * RGBの変化をHSVに連動させる
     */
    private void onRGBChanged() {
        final float[] rgb = new float[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = mRGB[i].getValue();
        }
        final float[] hsv = ColorMethod.RGBtoHSV(rgb);
        for (int i = 0; i < 3; i++) {
            mHSV[i].setValue(hsv[i]);
        }
        performHSVChange();
    }

    /**
     * 外部からHSVを指定して状態変更
     *
     * @param h H
     * @param s S
     * @param v V
     */
    public void setHSV(float h, float s, float v) {
        final float[] hsv = new float[3];
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
        final float[] rgb = ColorMethod.HSVtoRGB(hsv);
        for (int i = 0; i < 3; i++) {
            mHSV[i].setValue(hsv[i]);
            mRGB[i].setValue(rgb[i]);
        }
    }

    /**
     * 値変化のリスナーを登録する
     *
     * @param listener リスナー
     */
    public void setOnHSVChangeListener(OnHSVChangeListener listener) {
        mListener = listener;
    }

    /**
     * 値変化をリスナー通知
     */
    private void performHSVChange() {
        if (mListener != null) {
            mListener.onHSVChange(mHSV[0].getValue(), mHSV[1].getValue(), mHSV[2].getValue());
        }
    }
}
