package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.Gate
import github.j_jzk.circuitsim.gates.Switch
import github.j_jzk.circuitsim.gates.Lamp
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.event.MouseInputAdapter

class Viewport: JPanel() {
	init {
		val handler = MouseHandler()
		addMouseListener(handler)
		addMouseMotionListener(handler)
	}
	
	private val gates = mutableListOf<Gate>(Switch(10, 10), Switch(20, 100), Lamp(40, 50))
	var selectedGate: Gate? = null
	
	init {
		gates[2].inputs.add(gates[0])
	}
	
	override public fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		
		for (gate in gates) {
			renderGate(gate, g)
		}
	}
	
	private fun renderGate(gate: Gate, gr: Graphics) {
		gr.color = Color.BLACK
		gr.drawRect(gate.x, gate.y, gate.w, gate.h)
		gate.render(gr)
		
		for (input in gate.inputs) {
			//for every input of the logic gate, we draw a line from the center of the input's
			//right side to the center of the gate's left side. the color is set according to the
			//logic value of the input.
			if (input.getOutput() == true)
				gr.color = Color.RED
			else
				gr.color = Color.BLACK
			
			gr.drawLine(input.x+input.w, input.y + input.h/2, gate.x, gate.y + gate.h/2)
		}
	}
	
	private fun getGateAt(x: Int, y: Int): Gate? {
		for (gate in gates) {
			if (x >= gate.x && y >= gate.y && x <= gate.x + gate.w && y <= gate.y + gate.h)
				return gate
		}
		
		return null
	}
	
	private inner class MouseHandler: MouseInputAdapter() {
		private var dragged = false //if the mouse has been dragged
		
		override fun mouseClicked(e: MouseEvent) {
			val gate = getGateAt(e.x, e.y)
			if (gate != null) {
				gate.onClick()
				repaint()
			}
			
			dragged = false
		}
		
		override fun mousePressed(e: MouseEvent) {
			println("press")
			selectedGate = getGateAt(e.x, e.y)
		}
		
		override fun mouseDragged(e: MouseEvent) {
			println("drag")
			dragged = true
		}
		
		override fun mouseReleased(e: MouseEvent) {
			println("release")
			if (dragged) {
				println("moving")
				selectedGate?.x = e.x
				selectedGate?.y = e.y
			
				repaint()
				
				dragged = false //reset	
			}
		}
	}
}