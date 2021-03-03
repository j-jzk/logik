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
import java.awt.event.MouseWheelEvent
import javax.swing.SwingUtilities
import java.lang.IndexOutOfBoundsException
import javax.swing.JOptionPane

class Viewport(val statusBar: JLabel): JPanel() {
	private var gates = mutableListOf<Gate>()
	private val action = Action()
	public val toolbar = ToolbarHandler()
	private var lastSave: File? = null
	private var changedSinceSave = false
	
	private val selectedGates = mutableSetOf<Gate>()
	
	public var zoom: Double = 1.0
	private var pan = Pair<Int, Int>(0, 0)
	
	//initiate various listeners
	init {
		val handler = MouseHandler()
		addMouseListener(handler)
		addMouseMotionListener(handler)
		addMouseWheelListener(handler)
		
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "save")
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control O"), "load")
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control D"), "duplicate")
		getActionMap().put("save", object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) { toolbar.save() }
		})
		
		getActionMap().put("load", object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) = toolbar.load()
		})
		
		getActionMap().put("duplicate", object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) = toolbar.duplicate()
		})
	}
	

	
	override public fun paintComponent(g: Graphics) {
		if (g is Graphics2D) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			
		}
		
		super.paintComponent(g)
		
		//zoom and pan
		if (g is Graphics2D) {
			g.scale(zoom, zoom)
			g.translate(pan.first, pan.second)
		}
		
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

	/** returns a gate at given coordinates */	
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
	
	/** computes the "real" coordinates from the ones on the screen (taking into account the zoom and pan) */
	private fun getRealCoords(x: Int, y: Int) = Pair((x/zoom - pan.first).toInt(), (y/zoom - pan.second).toInt())
	
	fun saveSketch(): Boolean {
		try {
			lastSave?.writeText(Sketch.encode(gates.toTypedArray()))
			changedSinceSave = false
			statusBar.text = "File successfully saved."
			return true
		} catch (e: Exception) {
			statusBar.text = "ERROR SAVING FILE: ${e.message} (see terminal output for details)"
			e.printStackTrace()
			return false
		}
	}
	
	fun confirmClose(): Boolean {
		if (changedSinceSave) {
			val result = JOptionPane.showConfirmDialog(this,
				"You have made changes to the sketch since the last save.\nDo you want to save them?",
				"Confirm closing",
				JOptionPane.YES_NO_CANCEL_OPTION)
			
			return when(result) {
				0 -> toolbar.save() //save
				1 -> true //don't save
				else -> false //cancel / window closed
			}
		} else
			return true
	}
	
	private inner class MouseHandler: MouseInputAdapter() {
		
		override fun mouseClicked(e: MouseEvent) {
			val (x, y) = getRealCoords(e.x, e.y)
			val gate = getGateAt(x, y)
			
			//if the user clicked on a gate while shift is not down, clear the selection
			// and select only the gate the user clicked on
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
					action.ADD_INPUT ->
						//the user wants to add an input to a gate
						if (gate != null && action.subject !== gate) {
							//if the user clicked on a gate and it isn't the one he
							// wants to add an input to
							
							action.subject?.inputs?.add(gate)
							action.subject?.let { gate.outputs.add(it) }
							gate.onConnectOutput()
							action.subject?.onConnectInput()
							action.subject?.updateValue()
							action.x1 = 0; action.x2 = 0; action.y1 = 0; action.y2 = 0
							
							repaint()
							changedSinceSave = true
						}
				
					action.DEL_INPUT -> //remove an input from a gate
						if (gate != null) {
							action.subject?.let {
								it.inputs.remove(gate)
								gate.outputs.remove(it)
								it.updateValue()
								
								if (gate is ConnectionMidpoint) {
									var g = gate
									while (g is ConnectionMidpoint) {
										gates.remove(g)
										try {
											g = g.inputs[0]
										} catch (e: IndexOutOfBoundsException) {
											break
										}
									}
								}
							}
							
							changedSinceSave = true
						}
					
					action.ADD_GATE -> action.subject?.let {
						it.x = x
						it.y = y
						gates.add(it)
						it.onCreate()
						it.updateValue()
						changedSinceSave = true
					}
				}
				
				action.setCurrent(action.NOTHING)
				statusBar.text = ""
				//dragged = false
			} else if (e.getButton() == MouseEvent.BUTTON3) { //right button
				if (gate != null) {
					//the user wants to add an input to a gate
					selectedGates.removeAll() {true}
					selectedGates.add(gate)
					toolbar.addGateInput()
				}
			} else if (e.getButton() == MouseEvent.BUTTON2) { //middle button
				if (action.current == action.ADD_INPUT && action.subject != null) {
					//add a connection midpoint
					val midpoint = ConnectionMidpoint(x, y)
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
				//the user is connecting an input
				action.subject?.let {
					action.x1 = it.x
					action.y1 = it.y + it.h/2
				}
				
				//values for the paint function
				val (x, y) = getRealCoords(e.x, e.y)
				action.x2 = x
				action.y2 = y
				
				repaint()
			}
		}
		
		override fun mousePressed(e: MouseEvent) {
			val (x, y) = getRealCoords(e.x, e.y)
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				val gate = getGateAt(x, y)
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
			
			if (e.button != MouseEvent.BUTTON2) {
				//values for the paint function
				action.x1 = x
				action.y1 = y
			} else {
				//we can't use the coordinates from getRealCoords for panning
				// because it causes weird behavior
				action.x1 = (e.x/zoom).toInt()
				action.y1 = (e.y/zoom).toInt()				
			}
		}
		
		override fun mouseDragged(e: MouseEvent) {
			if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e)) { //left and right mouse buttons for moving/selecting
				//set the right mode
				if (action.current != action.SELECTING && action.current != action.MOVING)
					action.setCurrent(action.MOVING)
				if (e.isShiftDown() && action.current != action.SELECTING)
					action.setCurrent(action.SELECTING)
				
				val (x, y) = getRealCoords(e.x, e.y)
				
				if (action.current == action.MOVING) {
					//move the gates
					for (it in selectedGates) {
						it.x += x - action.x1
						it.y += y - action.y1
					}
					
					action.x1 = x
					action.y1 = y
					
					changedSinceSave = true
				} else if (action.current == action.SELECTING) {
					//set the coordinates of the selection box
					action.x2 = x
					action.y2 = y
				}
			} else { //middle mouse button for panning
				val panX = (e.x/zoom).toInt()
				val panY = (e.y/zoom).toInt()
				
				pan = Pair(pan.first + panX - action.x1, pan.second + panY - action.y1)

				//set the coordinates for the next movement
				action.x1 = panX
				action.y1 = panY
			}
			repaint()
		}
		
		override fun mouseReleased(e: MouseEvent) {
			if (action.current == action.MOVING) {
				//reset
				action.setCurrent(action.NOTHING)
				repaint()
			} else if (action.current == action.SELECTING) {
				//add the gates in the selection box to the set of selected gates
				val justSelected = getGatesInRect(
					min(action.x1, action.x2), min(action.y1, action.y2),
					max(action.x1, action.x2), max(action.y1, action.y2)
				)
				selectedGates.addAll(justSelected)
				action.setCurrent(action.NOTHING)
				
				repaint()
			}	
		}
		
		override fun mouseWheelMoved(e: MouseWheelEvent) {
			toolbar.zoom(e.wheelRotation)
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
				//remove the gate and update various triggers
				gates.remove(it)
				for (out in it.outputs) {
					out.inputs.remove(it)
					out.updateValue()
				}
				it.onDelete()
				statusBar.text = "Item deleted."
			}
			
			//clear the selection
			selectedGates.removeAll() {true}
			
			changedSinceSave = true
			repaint()
		}
		
		fun addGate(gate: Gate) {
			action.setCurrent(action.ADD_GATE)
			action.subject = gate
			statusBar.text = "Click to select position"
		}
		
		fun save(): Boolean {
			//if the last save location is known, save there
			if (lastSave != null) {
				return saveSketch()
			}
			
			//otherwise show a file chooser
			val fileChooser = JFileChooser()
			fileChooser.dialogTitle = "Specify a save path"
			fileChooser.setFileFilter(FileNameExtensionFilter("Sketch files (*.lgk)", "lgk"))
			val result = fileChooser.showSaveDialog(parent)
			
			if (result == JFileChooser.APPROVE_OPTION) { //the user didn't cancel the file save
				var file = fileChooser.getSelectedFile()
				//automatically add a .lgk extension if the user didn't specify it
				if (file.extension == "")
					file = File(file.getPath() + ".lgk")
				
				lastSave = file
				return saveSketch()
			} else {
				statusBar.text = "Save cancelled."
				return false
			}
		}
		
		fun saveAs() {
			//reset the last save location and proceed as if
			// regular "Save" was pressed
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
					
					//maybe add a more efficient method for larger files? (directed graph algorithms)
					for (gate in gates) {
						gate.updateValue()
					}
					
					statusBar.text = "Sketch loaded successfully."
					changedSinceSave = false
				} catch (e: Exception) {
					statusBar.text = "ERROR LOADING FILE: ${e.message} (see terminal output for details)"
					e.printStackTrace()
				}
				
				repaint()
			}
		}
		
		fun zoom(amount: Int, factor: Double = 1.1) {
			if (amount > 0)
				zoom /= amount * factor
			else
				zoom *= amount * -factor
			
			repaint()
		}
		
		fun duplicate() {
			statusBar.text = "Please wait..."
			
			val newGates = arrayListOf<Gate>()
			//the hash map is used for fast lookup when connecting the newly created
			// gates together
			val gateMap = HashMap<Gate, Gate>(selectedGates.size)
			
			//first create the new gates
			for (gate in selectedGates) {
				val new = Sketch.gateFromString(gate.toString())
				newGates.add(new)
				gateMap[gate] = new
			}
			
			//then connect them,
			for (gate in selectedGates) { //we can't loop over newGates, because they don't have any connections set
				val newGate = gateMap[gate]!!
				for (input in gate.inputs) {
					if (gateMap[input] != null) {
						//if the gate's input is in the gates that were duplicated, replace
						// the connection to the old gate with a connection to the newly created one
						newGate.inputs.add(gateMap[input]!!)
						gateMap[input]!!.outputs.add(newGate)
						
						//activate triggers
						newGate.onConnectInput()
						gateMap[input]!!.onConnectOutput()
					} else {
						newGate.inputs.add(input)
						input.outputs.add(newGate)
						
						newGate.onConnectInput()
						input.onConnectOutput()
					}
				}
			}
			
			//update their values
			for (gate in newGates)
				gate.updateValue()
			
			//and finally, add them to the circuit and set them as the selection so the user can
			// easily move them
			gates.addAll(newGates)
			selectedGates.removeAll {true}
			selectedGates.addAll(newGates)
			
			changedSinceSave = true
			statusBar.text = "Items duplicated."
			repaint()
			
		}
	}
	
	private inner class Action {
		val NOTHING = 0
		val ADD_INPUT = 1 //the user is adding an input to a gate
		val DEL_INPUT = 2 //the user wants to delete an input from a gate
		val ADD_GATE = 3  //the user wants to add a new gate to the sketch
		val MOVING = 4    //the user is moving the selected gates
		val SELECTING = 5 //the user is doing a box selection (shift+drag)
		
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
		
		//coordinates for drawing various things, such as connection lines and selection boxes
		var x1 = 0
		var y1 = 0
		var x2 = 0
		var y2 = 0
	}
}