package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.Gate
import github.j_jzk.circuitsim.gates.Switch
import github.j_jzk.circuitsim.gates.Lamp
import github.j_jzk.circuitsim.gates.ConnectionMidpoint
import github.j_jzk.circuitsim.Sketch
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.event.MouseInputAdapter
import java.awt.Cursor
import javax.swing.JButton
import javax.swing.JLabel
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.awt.event.KeyListener
import java.awt.event.KeyEvent
import javax.swing.KeyStroke
import javax.swing.AbstractAction
import java.awt.event.ActionEvent
import java.awt.RenderingHints
import java.awt.Graphics2D

class Viewport(val statusBar: JLabel): JPanel() {
	private var gates = mutableListOf<Gate>()
	private val action = Action()
	public val toolbar = ToolbarHandler()
	private var lastSave: File? = null
	
	//private var selectedGate: Gate? = null //used in case one gate is selected
	private val selectedGates = mutableListOf<Gate>() //used in case multiple gates are selected
	
	init {
		val handler = MouseHandler()
		addMouseListener(handler)
		addMouseMotionListener(handler)
		
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "save")
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control O"), "load")
		getActionMap().put("save", object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) = toolbar.save()
		})
		
		getActionMap().put("load", object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) = toolbar.load()
		})
	}
	

	
	override public fun paintComponent(g: Graphics) {
		if (g is Graphics2D)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		super.paintComponent(g)
		
		for (gate in gates) {
			renderGate(gate, g)
		}
		
		//draw a blue box around the selected nodes
		g.setColor(Color.BLUE)
		for (it in selectedGates) {
			g.drawRect(it.x-1, it.y-1, it.w+2, it.h+2)
		}
		g.setColor(Color.BLACK)
		
		//draw a line if the user is connecting an input
		if (action.current == action.ADD_INPUT) {
			g.drawLine(action.x1, action.y1, action.x2, action.y2)
		} else if (action.current == action.SELECTING) {
			//draw a box if the user is selecting multiple items (with shift)
			g.setColor(Color.BLUE)
			g.drawRect(
				min(action.x1, action.x2), min(action.y1, action.y2),
				abs(action.x2 - action.x1), abs(action.y2 - action.y1)
			)
			g.setColor(Color.BLACK)
		}
		
	}
	
	private fun renderGate(gate: Gate, gr: Graphics) {
		gr.color = Color.BLACK
		gate.render(gr)
		
		for (input in gate.inputs) {
			//for every input of the logic gate, we draw a line from the center of the input's
			//right side to the center of the gate's left side. the color is set according to the
			//logic value of the input.
			if (input.value == true)
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
	
	/**
	 * returns a list of gates in a given rectangle. x1 < x2 and y1 < y2
	 */
	private fun getGatesInRect(x1: Int, y1: Int, x2: Int, y2: Int): List<Gate> {
		val gatesInRect = mutableListOf<Gate>()
		
		for (gate in gates) {
			if (gate.x >= x1 && gate.x <= x2
				&& gate.y >= y1 && gate.y <= y2) {
				gatesInRect.add(gate)
			}
		}
		
		return gatesInRect.toList()
	}
	
	fun saveSketch() {
		try {
			lastSave?.writeText(Sketch.encode(gates.toTypedArray()))
			statusBar.text = "File successfully saved."
		} catch (e: Exception) {
			statusBar.text = "ERROR SAVING FILE: ${e.message} (see terminal output for details)"
			e.printStackTrace()
		}
	}
	
	private inner class MouseHandler: MouseInputAdapter() {
		private var dragged = false //if the mouse has been dragged
		
		override fun mouseClicked(e: MouseEvent) {
			val gate = getGateAt(e.x, e.y)
			
			if (gate != null && !e.isShiftDown()) {
				selectedGates.removeAll() {true}
				selectedGates.add(gate)
			}
			
			if (e.getButton() == MouseEvent.BUTTON1) { //left button
				
				when (action.current) {
					action.NOTHING -> if (gate != null) {
							gate.onClick()
							repaint()
						} else {
							//deselect
							selectedGates.removeAll() {true}
							repaint()
						}
					action.ADD_INPUT -> if (gate != null && action.subject !== gate) {
							action.subject?.inputs?.add(gate)
							action.subject?.let { gate.outputs.add(it) }
							gate.onConnectOutput()
							action.subject?.onConnectInput()
							action.subject?.updateValue()
							action.x1 = 0; action.x2 = 0; action.y1 = 0; action.y2 = 0
							repaint()
						}
				
					action.DEL_INPUT -> if (gate != null) {
						action.subject?.let {
							it.inputs.remove(gate)
							gate.outputs.remove(it)
							it.updateValue()
						}
					}
					
					action.ADD_GATE -> action.subject?.let {
						it.x = e.x
						it.y = e.y
						gates.add(it)
						it.onCreate()
						it.updateValue()
					}
				}
				
				action.setCurrent(action.NOTHING)
				statusBar.text = ""
				//dragged = false
			} else if (e.getButton() == MouseEvent.BUTTON3) { //right button
				if (gate != null) {
					selectedGates.removeAll() {true}
					selectedGates.add(gate)
					toolbar.addGateInput()
				}
			} else if (e.getButton() == MouseEvent.BUTTON2) { //middle button
				if (action.current == action.ADD_INPUT && action.subject != null) {
					//add a connection midpoint
					val midpoint = ConnectionMidpoint(e.x, e.y)
					action.subject?.let {
						midpoint.outputs.add(it)
						it.inputs.add(midpoint)
						it.onConnectInput()
					}
					
					action.subject = midpoint
					gates.add(midpoint)
				}
			}
		}
		
		override fun mouseMoved(e: MouseEvent) {
			if (action.current == action.ADD_INPUT) {
				action.subject?.let {
					action.x1 = it.x
					action.y1 = it.y + it.h/2
				}

				action.x2 = e.x
				action.y2 = e.y
				
				/*repaint(min(action.x1, action.x2) - 20,
					min(action.y1, action.y2) - 20,
					abs(action.x1 - action.x2) + 20,
					abs(action.y1 - action.y2) + 20)*/
				repaint()
			}
		}
		
		override fun mousePressed(e: MouseEvent) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				val gate = getGateAt(e.x, e.y)
				if (gate != null) {
					//if the gate has already been selected, assume that the user
					// wants to move the selected gates and don't clear the selection;
					// if shift is down assume the user wants to add a gate to the
					// selection - don't clear it
					// otherwise do
					if (!(gate in selectedGates) && !e.isShiftDown())
						selectedGates.removeAll() {true}
					
					selectedGates.add(gate)
				} else
					selectedGates.removeAll() {true}
					
				
			}
			
			action.x1 = e.x
			action.y1 = e.y
		}
		
		override fun mouseDragged(e: MouseEvent) {
			if (action.current != action.SELECTING && action.current != action.MOVING)
				action.setCurrent(action.MOVING)
			if (e.isShiftDown() && action.current != action.SELECTING)
				action.setCurrent(action.SELECTING)
			
			if (action.current == action.MOVING) {
				for (it in selectedGates) {
					it.x = e.x - it.w / 2
					it.y = e.y - it.h / 2
				}
			} else if (action.current == action.SELECTING) {
				action.x2 = e.x
				action.y2 = e.y
			}
			repaint()
		}
		
		override fun mouseReleased(e: MouseEvent) {
			if (action.current == action.MOVING) {
				for (it in selectedGates) {
					it.x = e.x - it.w / 2
					it.y = e.y - it.h / 2
				}
				
				action.setCurrent(action.NOTHING)
				repaint()
				
				//dragged = false //reset	
			} else if (action.current == action.SELECTING) {
				val justSelected = getGatesInRect(
					min(action.x1, action.x2), min(action.y1, action.y2),
					max(action.x1, action.x2), max(action.y1, action.y2)
				)
				selectedGates.addAll(justSelected)
				action.setCurrent(action.NOTHING)
				
				repaint()
			}
			
			
		}
	}
	
	public inner class ToolbarHandler {
		fun addGateInput() {
			if (selectedGates.size > 0) {
				action.setCurrent(action.ADD_INPUT)
				action.subject = selectedGates.first()
				statusBar.text = "Click on an item to connect. Press the middle mouse button to add a midpoint."
			} else {
				statusBar.text = "Please select an item first."
			}
		}
		
		fun delGateInput() {
			if (selectedGates.size > 0) {
				action.setCurrent(action.DEL_INPUT)
				action.subject = selectedGates.first()
				statusBar.text = "Click on an item to delete connection"
			} else {
				statusBar.text = "Please select an item first."
			}
		}
		
		fun delGate() {
			for (it in selectedGates) {
				gates.remove(it)
				for (out in it.outputs) {
					out.inputs.remove(it)
					out.updateValue()
				}
				it.onDelete()
				statusBar.text = "Item deleted."
				repaint()
			}
			selectedGates.removeAll() {true}
		}
		
		fun addGate(gate: Gate) {
			action.setCurrent(action.ADD_GATE)
			action.subject = gate
			statusBar.text = "Click to select position"
		}
		
		fun save() {
			if (lastSave != null) {
				saveSketch()
				return
			}
			
			val fileChooser = JFileChooser()
			fileChooser.dialogTitle = "Specify a save path"
			fileChooser.setFileFilter(FileNameExtensionFilter("Sketch files (*.lgk)", "lgk"))
			val result = fileChooser.showSaveDialog(parent)
			
			if (result == JFileChooser.APPROVE_OPTION) { //the user didn't cancel the file save
				var file = fileChooser.getSelectedFile()
				if (file.extension == "")
					file = File(file.getPath() + ".lgk")
				
				lastSave = file
				saveSketch()
			} else {
				statusBar.text = "Save cancelled."
			}
		}
		
		fun saveAs() {
			lastSave = null
			save()
		}
		
		fun load() {
			val fileChooser = JFileChooser()
			fileChooser.dialogTitle = "Select a sketch file"
			fileChooser.setFileFilter(FileNameExtensionFilter("Sketch files (*.lgk)", "lgk"))
			val result = fileChooser.showOpenDialog(parent)
			
			if (result == JFileChooser.APPROVE_OPTION) {
				val file = fileChooser.getSelectedFile()
				try {
					gates = Sketch.decode(file.readText()).toMutableList()
					lastSave = file
					statusBar.text = "Sketch loaded successfully."
				} catch (e: Exception) {
					statusBar.text = "ERROR LOADING FILE: ${e.message} (see terminal output for details)"
					e.printStackTrace()
				}
				
				repaint()
			}
		}
	}
	
	private inner class Action {
		val NOTHING = 0
		val ADD_INPUT = 1
		val DEL_INPUT = 2
		val ADD_GATE = 3
		val MOVING = 4
		val SELECTING = 5
		
		var current: Int = 0
			private set
		
		fun setCurrent(value: Int) {
			cursor = Cursor(when (value) {
				NOTHING -> Cursor.DEFAULT_CURSOR
				MOVING -> Cursor.MOVE_CURSOR
				SELECTING -> Cursor.CROSSHAIR_CURSOR
				else -> Cursor.HAND_CURSOR
			})
			current = value
		}
		
		var subject: Gate? = null
		
		//connection line or selection box
		var x1 = 0
		var y1 = 0
		var x2 = 0
		var y2 = 0
	}
}