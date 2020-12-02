package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.*
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
		val addInputBtn = JButton("Add input")
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
		add(JLabel("Nodes"))
		space()
		val createSwitchBtn = JButton("Create input node")
			createSwitchBtn.addActionListener(ActionListener { vp.toolbar.addGate(Switch(0,0)) })
			add(createSwitchBtn)
			space()
		val createLampBtn = JButton("Create output node")
			createLampBtn.addActionListener(ActionListener { vp.toolbar.addGate(Lamp(0,0)) })
			add(createLampBtn)
			space()
		val createNotBtn = JButton("Create NOT gate")
			createNotBtn.setMnemonic(KeyEvent.VK_N)
			createNotBtn.addActionListener(ActionListener { vp.toolbar.addGate(Not(0,0)) })
			add(createNotBtn)
			space()
		
		
	}
}