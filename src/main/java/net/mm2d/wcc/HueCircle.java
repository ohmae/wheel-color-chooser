/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
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
 * 色相環を表示操作するクラス
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class HueCircle extends JPanel {
    private static final int RADIUS = 255;
    private static final int DIAMETER = RADIUS * 2 + 1;
    private static final int WIDTH = 520;
    private static final int HEIGHT = 520;
    private final BufferedImage mImage;
    private final int mMarginTop;
    private final int mMarginLeft;
    private final int mCenterX;
    private final int mCenterY;
    private float mHue;
    private float mSaturation;
    private float mValue;
    private int mNum;
    private boolean mReverse = false;
    private OnHsChangeListener mListener;

    /**
     * 値変化のリスナー
     */
    public interface OnHsChangeListener {
        /**
         * 値が変化したときコール
         *
         * @param hue        色相
         * @param saturation 彩度
         * @param value      輝度
         */
        void onHsChange(float hue, float saturation, float value);
    }

    /**
     * インスタンス作成
     *
     * @param num 色数
     */
    public HueCircle(int num) {
        super();
        mNum = num;
        mHue = 0.0f;
        mSaturation = 1.0f;
        mValue = 1.0f;
        mMarginLeft = (WIDTH - DIAMETER) / 2;
        mMarginTop = (HEIGHT - DIAMETER) / 2;
        mCenterX = WIDTH / 2;
        mCenterY = HEIGHT / 2;
        mImage = new BufferedImage(DIAMETER, DIAMETER, BufferedImage.TYPE_4BYTE_ABGR);
        makeHSCircle(mValue);
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
     * クリックもしくはドラッグによって色を選択する
     *
     * @param x X座標
     * @param y Y座標
     */
    private void selectPoint(int x, int y) {
        final int cx = x - mCenterX;
        final int cy = mCenterY - y;
        final float distance = (float) Math.sqrt(cx * cx + cy * cy);
        final float h = (float) (getRadian(cx, cy) / (Math.PI * 2));
        float s;
        if (distance < RADIUS) {
            s = distance / RADIUS;
        } else {
            s = 1.0f;
        }
        setHsv(h, s, mValue, true);
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
    public void setHsv(float h, float s, float v) {
        setHsv(h, s, v, false);
    }

    /**
     * HSVの値を設定する
     *
     * @param h      Hue
     * @param s      Saturation
     * @param v      Value
     * @param notify リスナー通知の有無
     */
    private void setHsv(float h, float s, float v, boolean notify) {
        mHue = h;
        mSaturation = s;
        if (mValue != v) {
            mValue = v;
            makeHSCircle(mValue);
        }
        if (notify) {
            performHsChange();
        }
        repaint();
    }

    /**
     * 分割数を設定する
     *
     * @param div 分割数
     */
    public void setDivision(int div) {
        mNum = div;
        repaint();
    }

    /**
     * 向き反転
     *
     * @param reverse 反転するときtrue
     */
    public void setReverse(boolean reverse) {
        mReverse = reverse;
    }

    /**
     * 色相環の画像を作成する
     *
     * @param v 明度
     */
    private void makeHSCircle(float v) {
        for (int y = 0; y < mImage.getHeight(); y++) {
            final int cy = RADIUS - y;
            for (int x = 0; x < mImage.getWidth(); x++) {
                final int cx = x - RADIUS;
                final float distance = (float) Math.sqrt(cx * cx + cy * cy);
                int color = 0;
                if (distance < RADIUS + 1) {
                    final double radian = getRadian(cx, cy);
                    final float h = (float) (radian / (Math.PI * 2));
                    final float s = ColorUtils.clamp(distance / RADIUS, 0.0f, 1.0f);
                    final float[] rgb = ColorUtils.convertHsvToRgb(h, s, v);
                    color = ColorUtils.toColor(rgb);
                    final float alpha = RADIUS + 1 - distance;
                    if (alpha < 1) { // アンチエイリアス
                        color = ColorUtils.setAlpha(color, alpha);
                    }
                }
                mImage.setRGB(x, y, color);
            }
        }
    }

    /**
     * 指定された座標とX軸がなす角度を計算して返す
     *
     * @param x X座標
     * @param y Y座標
     * @return 座標とX軸の角度[radian]
     */
    private static double getRadian(double x, double y) {
        double radian;
        if (x == 0) {
            // ゼロ除算回避
            if (y > 0) {
                radian = Math.PI / 2;
            } else {
                radian = Math.PI * 3 / 2;
            }
        } else {
            radian = Math.atan(y / x);
            if (x < 0) {
                radian += Math.PI;
            } else if (radian < 0) {
                radian += Math.PI * 2;
            }
        }
        return radian;
    }

    /**
     * 値変化のリスナーを登録する
     *
     * @param listener リスナー
     */
    public void setOnHsChangeListener(OnHsChangeListener listener) {
        mListener = listener;
    }

    /**
     * 値の変化をリスナーに通知する
     */
    private void performHsChange() {
        if (mListener != null) {
            mListener.onHsChange(mHue, mSaturation, mValue);
        }
    }

    /**
     * 選択した色のリストを返す
     *
     * @return 色のリスト
     */
    public int[] getColors() {
        final int[] colors = new int[mNum];
        for (int i = 0; i < mNum; i++) {
            final float h = decimal(mHue + (float) i / mNum);
            final float[] rgb = ColorUtils.convertHsvToRgb(h, mSaturation, mValue);
            final int color = ColorUtils.toColor(rgb);
            if (mReverse) {
                colors[(mNum - i) % mNum] = color;
            } else {
                colors[i] = color;
            }
        }
        return colors;
    }

    /**
     * 小数部を取り出す
     *
     * @param value 実数
     * @return 小数部
     */
    private float decimal(float value) {
        final int i = (int) value;
        return value - i;
    }

    @Override
    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.drawImage(mImage, mMarginLeft, mMarginTop, this);
        final float r = mSaturation * RADIUS;
        // 選択している点を表示
        g2.setXORMode(Color.WHITE);
        for (int i = 0; i < mNum; i++) {
            final double a = decimal(mHue + (float) i / mNum) * 2 * Math.PI;
            final int x = mCenterX + (int) (Math.cos(a) * r + 0.5);
            final int y = mCenterY - (int) (Math.sin(a) * r + 0.5);
            if (i == 0) {
                // 操作点を大きく
                g2.drawRect(x - 3, y - 3, 4, 4);
            } else {
                g2.drawRect(x - 2, y - 2, 2, 2);
            }
        }
    }
}
