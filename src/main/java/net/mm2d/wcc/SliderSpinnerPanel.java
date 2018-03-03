/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 連動したスライダー＋スピナーを表示する
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class SliderSpinnerPanel extends JPanel implements ChangeListener {
    private final JSlider mSlider;
    private final JSpinner mSpinner;
    private boolean mNotify = true;
    private OnValueChangeListener mListener;
    private float mValue;

    /**
     * 値変化通知を受け取るリスナー
     */
    public interface OnValueChangeListener {
        /**
         * 値変化時にコール
         */
        void onValueChanged();
    }

    /**
     * ラベル、初期値、最小値、最大値を指定して初期化
     *
     * @param label ラベル
     * @param value 初期値
     * @param min   最小値
     * @param max   最大値
     */
    public SliderSpinnerPanel(String label, int value, int min, int max) {
        super();
        setLayout(new FlowLayout());
        SpinnerNumberModel model = new SpinnerNumberModel();
        JLabel jLabel = new JLabel(label);
        mSlider = new JSlider();
        mSpinner = new JSpinner(model);
        final DefaultEditor editor = (DefaultEditor) mSpinner.getEditor();
        editor.getTextField().setColumns(3);
        mSlider.setMinimum(min);
        mSlider.setMaximum(max);
        model.setMinimum(min);
        model.setMaximum(max);
        setValue(value);
        add(jLabel);
        add(mSlider);
        add(mSpinner);
        mSlider.addChangeListener(this);
        mSpinner.addChangeListener(this);
    }

    /**
     * int値指定で値を設定する
     *
     * このメソッドで値が変わってもリスナー通知は発生しない
     *
     * @param value 設定する値
     */
    public void setValue(int value) {
        final int min = mSlider.getMinimum();
        final int max = mSlider.getMaximum();
        mValue = (float) (value - min) / (float) (max - min);
        mNotify = false;
        mSlider.setValue(value);
        mSpinner.setValue(value);
        mNotify = true;
    }

    /**
     * float値[0.0, 1.0]指定で値を設定する
     *
     * このメソッドで値が変わってもリスナー通知は発生しない
     *
     * @param value 設定する値
     */
    public void setValue(float value) {
        mValue = value;
        final int min = mSlider.getMinimum();
        final int max = mSlider.getMaximum();
        final int v = (int) (value * (max - min) + 0.5f) + min;
        mNotify = false;
        mSlider.setValue(v);
        mSpinner.setValue(v);
        mNotify = true;
    }

    /**
     * 値変化のリスナーを登録
     *
     * @param listener リスナー
     */
    public void setOnColorChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 値変化をリスナー通知
     */
    private void performValueChange() {
        if (mListener != null) {
            mListener.onValueChanged();
        }
    }

    /**
     * 現在の値を返す
     *
     * @return 現在の値
     */
    public float getValue() {
        return mValue;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!mNotify) {
            // 外部要因による通知無しの変化
            return;
        }
        int value;
        if (mSpinner == e.getSource()) {
            // スピナーの操作をスライダーに伝える
            value = (Integer) mSpinner.getValue();
            mNotify = false;
            mSlider.setValue(value);
            mNotify = true;
        } else {
            // スライダーの操作をスピナーに伝える
            value = mSlider.getValue();
            mNotify = false;
            mSpinner.setValue(value);
            mNotify = true;
        }
        // 自身の値を更新して通知
        final int min = mSlider.getMinimum();
        final int max = mSlider.getMaximum();
        mValue = (float) (value - min) / (float) (max - min);
        performValueChange();
    }
}
