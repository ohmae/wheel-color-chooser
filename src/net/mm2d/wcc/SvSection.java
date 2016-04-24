/**
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import net.mm2d.color.ColorUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 輝度彩度を円柱モデル断面で操作するUI
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class SvSection extends JPanel {
    private static final int RANGE = 255;
    private static final int WIDTH = 260;
    private static final int HEIGHT = 260;
    private final BufferedImage mImage;
    private final int mMarginTop;
    private final int mMarginLeft;
    private float mHue;
    private float mSaturation;
    private float mValue;
    private OnSvChangeListener mListener;

    /**
     * 値変化のリスナー
     */
    public interface OnSvChangeListener {
        /**
         * 値が変化したときコール
         *
         * @param hue 色相
         * @param saturation 彩度
         * @param value 輝度
         */
        public void onSvChange(float hue, float saturation, float value);
    }

    /**
     * インスタンス作成
     */
    public SvSection() {
        super();
        mHue = 0.0f;
        mSaturation = 1.0f;
        mValue = 1.0f;
        mImage = new BufferedImage(RANGE, RANGE, BufferedImage.TYPE_4BYTE_ABGR);
        mMarginTop = (WIDTH - RANGE) / 2;
        mMarginLeft = (HEIGHT - RANGE) / 2;
        makeSvSection(mImage);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        final MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectPoint(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                selectPoint(e.getX(), e.getY());
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    /**
     * マウス操作による値の選択
     *
     * @param x X座標
     * @param y Y座標
     */
    private void selectPoint(int x, int y) {
        final float s = ColorUtils.toFloat(ColorUtils.clamp(x - mMarginLeft, 0, 255));
        final float v = ColorUtils.toFloat(ColorUtils.clamp(RANGE - (y - mMarginTop), 0, 255));
        setHsv(mHue, s, v, true);
        repaint();
    }

    /**
     * HSVの値を設定する
     *
     * @param h H
     * @param s S
     * @param v V
     */
    public void setHsv(float h, float s, float v) {
        setHsv(h, s, v, false);
    }

    /**
     * HSVの値を設定する
     *
     * @param h H
     * @param s S
     * @param v V
     * @param notify リスナー通知する場合true
     */
    private void setHsv(float h, float s, float v, boolean notify) {
        if (mHue != h) {
            mHue = h;
            makeSvSection(mImage);
        }
        mSaturation = s;
        mValue = v;
        if (notify) {
            performSvChange();
        }
        repaint();
    }

    /**
     * SV断面の画像を作成する
     *
     * @param image 書き込み先
     */
    private void makeSvSection(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final float[] rgb = ColorUtils.convertHsvToRgb(
                        mHue,
                        (float) x / RANGE,
                        (float) (RANGE - y) / RANGE);
                final int color = ColorUtils.toColor(rgb);
                image.setRGB(x, y, color);
            }
        }
    }

    /**
     * 値変化のリスナーを登録する
     *
     * @param listener リスナー
     */
    public void setOnSvChangeListener(OnSvChangeListener listener) {
        mListener = listener;
    }

    /**
     * 値の変化をリスナーに通知する
     */
    private void performSvChange() {
        if (mListener != null) {
            mListener.onSvChange(mHue, mSaturation, mValue);
        }
    }

    @Override
    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.drawImage(mImage, mMarginLeft, mMarginTop, this);
        // 選択している点を描画
        final int x = (int) (mSaturation * RANGE) + mMarginLeft;
        final int y = (int) (RANGE - mValue * RANGE) + mMarginTop;
        g2.setXORMode(Color.WHITE);
        g2.drawRect(x - 1, y - 1, 2, 2);
    }
}
