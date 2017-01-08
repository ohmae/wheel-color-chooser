/*
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * カラーサンプルを表示する
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class ColorSamplePanel extends JPanel {
    private final List<JPanel> mPanelList;
    private final Dimension mSize;
    private int mNum;

    /**
     * インスタンス作成
     *
     * @param num 色数
     */
    public ColorSamplePanel(int num) {
        super();
        mNum = num;
        mSize = new Dimension(30, 17); // 一つの大きさ
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mNum = 12;
        mPanelList = new ArrayList<>(mNum);
        for (int i = 0; i < mNum; i++) {
            final JPanel panel = new JPanel();
            panel.setPreferredSize(mSize);
            mPanelList.add(panel);
            add(panel);
        }
    }

    /**
     * 色数を設定する
     *
     * @param num 色数
     */
    public void setNum(int num) {
        if (num == mNum) {
            return;
        }
        if (num < mNum) {
            for (int i = num; i < mNum; i++) {
                remove(i);
            }
        } else if (num < mPanelList.size()) {
            for (int i = mNum; i < num; i++) {
                add(mPanelList.get(i));
            }
        } else {
            for (int i = mNum; i < mPanelList.size(); i++) {
                // 確保済みが残っていたら追加
                add(mPanelList.get(i));
            }
            for (int i = mPanelList.size(); i < num; i++) {
                // 確保しながら追加
                final JPanel panel = new JPanel();
                panel.setPreferredSize(mSize);
                mPanelList.add(panel);
                add(panel);
            }
        }
        mNum = num;
    }

    /**
     * 色リストを設定
     *
     * @param colors 表示する色
     */
    public void setColors(int[] colors) {
        setNum(colors.length);
        for (int i = 0; i < colors.length; i++) {
            mPanelList.get(i).setBackground(new Color(colors[i]));
        }
    }
}
