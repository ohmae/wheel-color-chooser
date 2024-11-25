/*
 * Copyright (c) 2014 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.wcc

import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JSpinner
import javax.swing.JSpinner.DefaultEditor
import javax.swing.SpinnerNumberModel
import kotlin.math.roundToInt

/**
 * 連動したスライダー＋スピナーを表示する
 *
 * @param label ラベル
 * @param value 初期値
 * @param min   最小値
 * @param max   最大値
 */
class SliderSpinnerPanel(
    label: String,
    value: Int,
    private val min: Int,
    private val max: Int,
) : JPanel() {
    private val slider: JSlider = JSlider().also {
        it.minimum = min
        it.maximum = max
    }
    private val spinner: JSpinner = JSpinner(
        SpinnerNumberModel().also {
            it.minimum = min
            it.maximum = max
        },
    )

    @Volatile
    private var notify = true
    private var _value: Float = 0f
    var value: Float
        get() = _value
        set(value) {
            _value = value
            applyWithoutNotify((value * (max - min) + min).roundToInt())
        }
    var onColorChangeListener: (() -> Unit)? = null

    init {
        layout = FlowLayout()
        (spinner.editor as? DefaultEditor)?.also { it.textField.columns = 3 }
        _value = value.toThisValue()
        applyWithoutNotify(value)
        add(JLabel(label))
        add(slider)
        add(spinner)
        slider.addChangeListener {
            if (notify) {
                val v = slider.value
                withoutNotify { spinner.value = v }
                this._value = v.toThisValue()
                onColorChangeListener?.invoke()
            }
        }
        spinner.addChangeListener {
            if (notify) {
                val v = spinner.value as Int
                withoutNotify { slider.value = v }
                this._value = v.toThisValue()
                onColorChangeListener?.invoke()
            }
        }
    }

    private fun applyWithoutNotify(value: Int) {
        withoutNotify {
            slider.value = value
            spinner.value = value
        }
    }

    private inline fun withoutNotify(block: () -> Unit) {
        notify = false
        block.invoke()
        notify = true
    }

    private fun Int.toThisValue() = (this - min).toFloat() / (max - min).toFloat()
}
