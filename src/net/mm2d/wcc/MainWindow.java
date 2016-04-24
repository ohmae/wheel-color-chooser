/**
 * Copyright(c) 2014 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc;

import net.mm2d.color.ColorMethod;
import net.mm2d.wcc.HueCircle.OnHSChangeListener;
import net.mm2d.wcc.SVSection.OnSVChangeListener;
import net.mm2d.wcc.SliderPanel.OnHSVChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * メインウィンドウ
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class MainWindow extends JFrame
        implements OnHSChangeListener, OnSVChangeListener, OnHSVChangeListener, ChangeListener {
    private static final int DEFAULT_COLOR_NUM = 12;
    private final HueCircle mHueCircle; // 色相環
    private final SVSection mSVSection; // 円柱モデル
    private final SliderPanel mSliderPanel; // HSV&RGBスライダー
    private final ColorSamplePanel mSamplePanel; // カラーサンプル
    private final JTextArea mHexColorArea; // 16進数表記テキスト
    private final JTextArea mDecColorArea; // 10進数表記テキスト
    private final JSpinner mDivisionSpinner; // 分割数設定
    private final JCheckBox mReverseCheck;

    public MainWindow() {
        super();
        setTitle("色相環型ColorChooser");
        setSize(1064, 575);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        mHueCircle.setOnHSChangeListener(this);
        gbl.setConstraints(mHueCircle, gbc);
        contentPane.add(mHueCircle);
        gbc.gridheight = 1;

        gbc.gridx = 1;
        gbc.gridy = 0;
        mSVSection = new SVSection();
        mSVSection.setOnSVChangeListener(this);
        gbl.setConstraints(mSVSection, gbc);
        contentPane.add(mSVSection);

        final JPanel divisionPanel = new JPanel(new FlowLayout());
        divisionPanel.add(new JLabel("分割数"));
        final SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(2);
        model.setMaximum(360);
        mDivisionSpinner = new JSpinner(model);
        mDivisionSpinner.setValue(DEFAULT_COLOR_NUM);
        mDivisionSpinner.addChangeListener(this);
        final DefaultEditor editor = (DefaultEditor) mDivisionSpinner.getEditor();
        editor.getTextField().setColumns(3);
        editor.getTextField().setFocusable(false);
        divisionPanel.add(mDivisionSpinner);
        mReverseCheck = new JCheckBox("逆順");
        mReverseCheck.addChangeListener(this);
        divisionPanel.add(mReverseCheck);

        final JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(divisionPanel, BorderLayout.NORTH);

        mSliderPanel = new SliderPanel();
        mSliderPanel.setOnHSVChangeListener(this);
        controlPanel.add(mSliderPanel, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbl.setConstraints(controlPanel, gbc);
        contentPane.add(controlPanel);

        final Font monospace = new Font("Monospaced", Font.PLAIN, 12);
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
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new MainWindow();
    }

    /**
     * ピックアップした色をUIに反映する
     */
    private void setColors() {
        final int[] colors = mHueCircle.getColors();
        // サンプル一覧を更新
        mSamplePanel.setColors(colors);
        // 16進数表記と10進数表記まとめて作成
        final StringBuilder hexSb = new StringBuilder();
        final StringBuilder decSb = new StringBuilder();
        for (final int color : colors) {
            final int[] rgb = ColorMethod.toRGBInt(color);
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
    public void onSVChange(float hue, float saturation, float value) {
        // SV断面の変化
        mHueCircle.setHSV(hue, saturation, value);
        mSliderPanel.setHSV(hue, saturation, value);
        setColors();
    }

    @Override
    public void onHSChange(float hue, float saturation, float value) {
        // HSサークルの変化
        mSVSection.setHSV(hue, saturation, value);
        mSliderPanel.setHSV(hue, saturation, value);
        setColors();
    }

    @Override
    public void onHSVChange(float hue, float saturation, float value) {
        // スレライダーによる変化
        mHueCircle.setHSV(hue, saturation, value);
        mSVSection.setHSV(hue, saturation, value);
        setColors();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == mDivisionSpinner) {
            // 分割数用Spinnerの変化
            final int div = (int) mDivisionSpinner.getValue();
            mHueCircle.setDivision(div);
            setColors();
        } else if (e.getSource() == mReverseCheck) {
            final boolean reverse = mReverseCheck.isSelected();
            mHueCircle.setReverse(reverse);
            setColors();
        }
    }
}
