/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import net.mm2d.color.ColorUtils;
import net.mm2d.wcc.HueCircle.OnHsChangeListener;
import net.mm2d.wcc.SliderPanel.OnHsvChangeListener;
import net.mm2d.wcc.SvSection.OnSvChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * メインウィンドウ
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class MainWindow extends JFrame
        implements OnHsChangeListener, OnSvChangeListener, OnHsvChangeListener, ChangeListener {
    private static final int DEFAULT_COLOR_NUM = 12;
    private final HueCircle mHueCircle; // 色相環
    private final SvSection mSVSection; // 円柱モデル
    private final SliderPanel mSliderPanel; // HSV&RGBスライダー
    private final ColorSamplePanel mSamplePanel; // カラーサンプル
    private final JTextArea mHexColorArea; // 16進数表記テキスト
    private final JTextArea mDecColorArea; // 10進数表記テキスト
    private final JSpinner mDivisionSpinner; // 分割数設定
    private final JCheckBox mReverseCheck;
    private final JTextField mRgbInput;
    private int mCurrentColor;

    public MainWindow() {
        super();
        setTitle("色相環型ColorChooser");
        setSize(1064, 575);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(Color.WHITE);

        final JPanel contentPane = new JPanel();
        final GridBagLayout gbl = new GridBagLayout();
        contentPane.setLayout(gbl);

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        mHueCircle = new HueCircle(DEFAULT_COLOR_NUM);
        mHueCircle.setOnHsChangeListener(this);
        gbl.setConstraints(mHueCircle, gbc);
        contentPane.add(mHueCircle);
        gbc.gridheight = 1;

        gbc.gridx = 1;
        gbc.gridy = 0;
        mSVSection = new SvSection();
        mSVSection.setOnSvChangeListener(this);
        gbl.setConstraints(mSVSection, gbc);
        contentPane.add(mSVSection);

        final Font monospace = new Font("Monospaced", Font.PLAIN, 12);
        final JPanel settingPanel = new JPanel(new FlowLayout());
        settingPanel.add(new JLabel("RGB"));
        mRgbInput = new JTextField();
        mRgbInput.setFont(monospace);
        mRgbInput.setColumns(7);
        mRgbInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onEditRgbInput();
            }
        });
        settingPanel.add(mRgbInput);
        settingPanel.add(new JLabel("分割数"));
        final SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(2);
        model.setMaximum(360);
        mDivisionSpinner = new JSpinner(model);
        mDivisionSpinner.setValue(DEFAULT_COLOR_NUM);
        mDivisionSpinner.addChangeListener(this);
        final DefaultEditor editor = (DefaultEditor) mDivisionSpinner.getEditor();
        editor.getTextField().setColumns(3);
        editor.getTextField().setFocusable(false);
        settingPanel.add(mDivisionSpinner);
        mReverseCheck = new JCheckBox("逆順");
        mReverseCheck.addChangeListener(this);
        settingPanel.add(mReverseCheck);

        final JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(settingPanel, BorderLayout.NORTH);

        mSliderPanel = new SliderPanel();
        mSliderPanel.setOnHsvChangeListener(this);
        controlPanel.add(mSliderPanel, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbl.setConstraints(controlPanel, gbc);
        contentPane.add(controlPanel);

        mHexColorArea = new JTextArea();
        mHexColorArea.setFont(monospace);
        mHexColorArea.setColumns(7); // #xxxxxxで7文字
        mHexColorArea.setEditable(false); // 編集不可

        mDecColorArea = new JTextArea();
        mDecColorArea.setFont(monospace);
        mDecColorArea.setColumns(18); // rgb(xxx, xxx, xxx)で18文字
        mDecColorArea.setEditable(false);

        mSamplePanel = new ColorSamplePanel(DEFAULT_COLOR_NUM);

        final JPanel scrollee = new JPanel(new FlowLayout());
        scrollee.add(mSamplePanel);
        scrollee.add(mHexColorArea);
        scrollee.add(mDecColorArea);
        final JScrollPane scrollPane = new JScrollPane(scrollee,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(250, 530));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbl.setConstraints(scrollPane, gbc);
        contentPane.add(scrollPane);

        setContentPane(contentPane);
        setVisible(true);
        setColors();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new MainWindow();
    }

    private void onEditRgbInput() {
        final String hexText = mRgbInput.getText();
        int color = convertHexToRgb(hexText);
        if (color < 0) {
            mRgbInput.setBackground(Color.PINK);
        } else {
            color = ColorUtils.setAlpha(color, 255);
            mRgbInput.setBackground(Color.WHITE);
            if (color == mCurrentColor) {
                return;
            }
            final float[] rgb = ColorUtils.toRGB(color);
            mSliderPanel.setRgb(rgb[0], rgb[1], rgb[2], true);
        }
    }

    private void setRgbInput(int color) {
        mRgbInput.setBackground(Color.WHITE);
        final int[] rgb = ColorUtils.toRGBInt(color);
        final String hexText = String.format("%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
        if (!mRgbInput.getText().toUpperCase().equals(hexText)) {
            mRgbInput.setText(hexText);
        }
    }

    /**
     * 16進数テキストをRGB値に変更
     *
     * @param hexText 16進数表現の色
     * @return RGB値
     */
    private int convertHexToRgb(String hexText) {
        if (hexText.length() != 6) {
            return -1;
        }
        hexText = hexText.toLowerCase();
        int color = 0;
        for (int i = 0; i < 6; i++) {
            color <<= 4;
            final char c = hexText.charAt(i);
            if (c >= '0' && c <= '9') {
                color += c - '0';
            } else if (c >= 'a' && c <= 'f') {
                color += c - 'a' + 10;
            } else {
                return -1;
            }
        }
        return color;
    }

    /**
     * ピックアップした色をUIに反映する
     */
    private void setColors() {
        final int[] colors = mHueCircle.getColors();
        mCurrentColor = colors[0];
        setRgbInput(mCurrentColor);
        // サンプル一覧を更新
        mSamplePanel.setColors(colors);
        // 16進数表記と10進数表記まとめて作成
        final StringBuilder hexSb = new StringBuilder();
        final StringBuilder decSb = new StringBuilder();
        for (final int color : colors) {
            final int[] rgb = ColorUtils.toRGBInt(color);
            final String hexText = String.format("#%02X%02X%02X%n", rgb[0], rgb[1], rgb[2]);
            hexSb.append(hexText);
            final String decText = String.format("rgb(%d, %d, %d)%n", rgb[0], rgb[1], rgb[2]);
            decSb.append(decText);
        }
        // テキストエリアへ反映
        mHexColorArea.setText(hexSb.toString().trim());
        mDecColorArea.setText(decSb.toString().trim());
    }

    @Override
    public void onSvChange(float hue, float saturation, float value) {
        // SV断面の変化
        mHueCircle.setHsv(hue, saturation, value);
        mSliderPanel.setHsv(hue, saturation, value);
        setColors();
    }

    @Override
    public void onHsChange(float hue, float saturation, float value) {
        // HSサークルの変化
        mSVSection.setHsv(hue, saturation, value);
        mSliderPanel.setHsv(hue, saturation, value);
        setColors();
    }

    @Override
    public void onHsvChange(float hue, float saturation, float value) {
        // スレライダーによる変化
        mHueCircle.setHsv(hue, saturation, value);
        mSVSection.setHsv(hue, saturation, value);
        setColors();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == mDivisionSpinner) {
            // 分割数用Spinnerの変化
            final int div = (Integer) mDivisionSpinner.getValue();
            mHueCircle.setDivision(div);
            setColors();
        } else if (e.getSource() == mReverseCheck) {
            final boolean reverse = mReverseCheck.isSelected();
            mHueCircle.setReverse(reverse);
            setColors();
        }
    }
}
