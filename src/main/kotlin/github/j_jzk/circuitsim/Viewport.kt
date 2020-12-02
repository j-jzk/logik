package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.Gate
import github.j_jzk.circuitsim.gates.Switch
import github.j_jzk.circuitsim.gates.Lamp
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.event.MouseInputAdapter
import java.awt.Cursor
import javax.swing.JButton
import javax.swing.JLabel

class Viewport(val statusBar: JLabel): JPanel() {
	init {
		val handler = MouseHandler()
		addMouseListener(handler)
		addMouseMotionListener(handler)
	}
	
	private val gates = mutableListOf<Gate>()
	private var selectedGate: Gate? = null
	private val action = Action()
	public val toolbar = ToolbarHandler()
	
	override public fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		
		for (gate in gates) {
			renderGate(gate, g)
		}
		
		selectedGate?.let { // if (selectedGate != null)
			g.setColor(Color.BLUE)
			g.drawRect(it.x-1, it.y-1, it.w+2, it.h+2)
			g.setColor(Color.BLACK)
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
			when (action.current) {
				action.NOTHING -> if (gate != null) {
						gate.onClick()
						repaint()
					}
				action.ADD_INPUT -> if (gate != null && action.subject !== gate) {
						action.subject?.inputs?.add(gate)
						action.subject?.let { gate.outputs.add(it) }
						repaint()
					}
			
				action.DEL_INPUT -> if (gate != null) {
					action.subject?.let {
						it.inputs.remove(gate)
						gate.outputs.remove(it)
					}
				}
				
				action.ADD_GATE -> action.subject?.let {
					it.x = e.x
					it.y = e.y
					gates.add(it)
				}
			}
			
			action.setCurrent(action.NOTHING)
			statusBar.text = ""
			dragged = false
		}
		
		override fun mousePressed(e: MouseEvent) {
			selectedGate = getGateAt(e.x, e.y)
		}
		
		override fun mouseDragged(e: MouseEvent) {
			dragged = true
		}
		
		override fun mouseReleased(e: MouseEvent) {
			if (dragged) {
				selectedGate?.x = e.x
				selectedGate?.y = e.y
			
				repaint()
				
				dragged = false //reset	
			}
		}
	}
	
	public inner class ToolbarHandler {
		fun addGateInput(btn: JButton) {
			if (selectedGate != null) {
				action.setCurrent(action.ADD_INPUT)
				action.subject = selectedGate
				statusBar.text = "Click on an item to connect"
			} else {
				statusBar.text = "Please select an item first."
			}
		}
		
		fun delGateInput(btn: JButton) {
			if (selectedGate != null) {
				action.setCurrent(action.DEL_INPUT)
				action.subject = selectedGate
				statusBar.text = "Click on an item to delete connection"
			} else {
				statusBar.text = "Please select an item first."
			}
		}
		
		fun delGate() {
			selectedGate?.let { //if (selectedGate != null) ... gives an error because selectedGate is mutable
				gates.remove(it)
				for (out in it.outputs)
					out.inputs.remove(it)
				statusBar.text = "Item deleted."
				selectedGate = null
				repaint()
			}
		}
		
		fun addGate(gate: Gate) {
			action.setCurrent(action.ADD_GATE)
			action.subject = gate
			statusBar.text = "Click to select position"
		}
	}
	
	private inner class Action {
		val NOTHING = 0
		val ADD_INPUT = 1
		val DEL_INPUT = 2
		val ADD_GATE = 3
		
		var current: Int = 0
			private set
		
		fun setCurrent(value: Int) {
			cursor = Cursor(when (value) {
				NOTHING -> Cursor.DEFAULT_CURSOR
				else -> Cursor.HAND_CURSOR
			})
			current = value
		}
		
		var subject: Gate? = null
	}
}