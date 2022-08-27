package net.mm2d.wcc

import java.awt.Color
import java.awt.Window
import javax.swing.*

class Toast(message: String) : JDialog() {
    init {
        isUndecorated = true
        isAlwaysOnTop = true
        focusableWindowState = false
        val label = JLabel(message).also {
            it.border = BorderFactory.createEmptyBorder(4, 8, 4, 8)
        }
        add(JPanel().also {
            it.border = BorderFactory.createLineBorder(Color.LIGHT_GRAY)
            it.background = Color.WHITE
            it.add(label)
        })
        pack()
    }

    companion object {
        fun show(parent: JComponent, message: String) {
            val toast = Toast(message)
            val window: Window = SwingUtilities.getWindowAncestor(parent)
            val x = window.locationOnScreen.x + window.width / 2 - toast.width / 2
            val y = window.locationOnScreen.y + window.height / 2 - toast.height / 2
            toast.setLocation(x, y)
            toast.isVisible = true

            Thread {
                Thread.sleep(2000)
                toast.isVisible = false
            }.start()
        }
    }
}
