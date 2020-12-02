package github.j_jzk.circuitsim

import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*

class Toolbar(val vp: Viewport): JPanel() {
	init {
		setLayout(BoxLayout(this, BoxLayout.Y_AXIS))

		fun space() = add(Box.createVerticalStrut(7)) 
		
		space()
		add(JLabel("Gate"))
		space()
		val addInputBtn = JButton("Add input (Alt+i)")
			addInputBtn.setMnemonic(KeyEvent.VK_I)
			addInputBtn.addActionListener(ActionListener { vp.toolbar.addGateInput(addInputBtn) })
			add(addInputBtn)
			space()
		val delInputBtn = JButton("Delete input")
			delInputBtn.addActionListener(ActionListener { vp.toolbar.delGateInput(delInputBtn) })
			add(delInputBtn)
			space()
		val delGateBtn = JButton("Delete (Alt+Del)")
			delGateBtn.setMnemonic(KeyEvent.VK_DELETE)
			delGateBtn.addActionListener(ActionListener { vp.toolbar.delGate() })
			add(delGateBtn)
			space()
		add(JSeparator())
		space()
		
	}
}