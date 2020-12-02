package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.*
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*

class Toolbar(val vp: Viewport): JPanel() {
	init {
		setLayout(BoxLayout(this, BoxLayout.Y_AXIS))

		fun space() = add(Box.createVerticalStrut(7))
		fun addCreateBtn(btn: JButton, gate: () -> Gate, key: Int? = null) {
			if (key != null)
				btn.setMnemonic(key)
			btn.addActionListener(ActionListener { vp.toolbar.addGate(gate()) })
			add(btn)
			space()
		}
		
		space()
		add(JLabel("Sketch"))
		space()
		val saveBtn = JButton("Save (Ctrl+S)")
			saveBtn.addActionListener(ActionListener { vp.toolbar.save() })
			add(saveBtn)
			space()
		val saveAsBtn = JButton("Save as...")
			saveAsBtn.addActionListener(ActionListener { vp.toolbar.saveAs() })
			add(saveAsBtn)
			space()
		val loadBtn = JButton("Load (Ctrl+O)")
			loadBtn.addActionListener(ActionListener { vp.toolbar.load() })
			add(loadBtn)
			space()
		
		add(JLabel("Gate"))
		space()
		val addInputBtn = JButton("Add input (right click)")
			addInputBtn.setMnemonic(KeyEvent.VK_I)
			addInputBtn.addActionListener(ActionListener { vp.toolbar.addGateInput() })
			add(addInputBtn)
			space()
		val delInputBtn = JButton("Delete input")
			delInputBtn.addActionListener(ActionListener { vp.toolbar.delGateInput() })
			add(delInputBtn)
			space()
		val delGateBtn = JButton("Delete (Alt+Del)")
			delGateBtn.setMnemonic(KeyEvent.VK_DELETE)
			delGateBtn.addActionListener(ActionListener { vp.toolbar.delGate() })
			add(delGateBtn)
			space()
		add(JLabel("Nodes"))
		space()
		
		addCreateBtn(JButton("Create input node"), { Switch(0,0) })
		addCreateBtn(JButton("Create output node"), { Lamp(0,0) })
		addCreateBtn(JButton("Create NOT gate"), { Not(0,0) }, KeyEvent.VK_N)
		addCreateBtn(JButton("Create AND gate"), { And(0,0) }, KeyEvent.VK_A)
		addCreateBtn(JButton("Create OR gate"), { Or(0,0) }, KeyEvent.VK_O)
		addCreateBtn(JButton("Create XOR gate"), { Xor(0,0) }, KeyEvent.VK_X)
		addCreateBtn(JButton("Create NAND gate"), { Nand(0,0) }, KeyEvent.VK_D)
		addCreateBtn(JButton("Create NOR gate"), { Nor(0,0) }, KeyEvent.VK_R)
		addCreateBtn(JButton("Create XNOR gate"), { Xnor(0,0) })
		
	}
}