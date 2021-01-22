package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class ConnectionMidpoint: Gate {
	override public val w = 4
	override public val h = 4

	override public fun updateValue() {
		value = if (inputs.size > 0) inputs[0].value else false
				
		for (output in outputs) {
			output.updateValue()
		}
	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)
	}
	
	constructor(s: String) : super(s)
	constructor(x: Int, y: Int) : super(x, y)
	
	//don't allow more than one output or input
	override fun onConnectInput() {
		if (inputs.size > 1)
			inputs.removeAt(1)
	}
	
	override fun onConnectOutput() {
		if (outputs.size > 1)
			outputs.removeAt(1)
	}
	
	override fun onDelete() {
		if (inputs.size > 0 && outputs.size > 0) {
			inputs[0].outputs.add(outputs[0])
			outputs[0].inputs.add(inputs[0])
		}
	}
}