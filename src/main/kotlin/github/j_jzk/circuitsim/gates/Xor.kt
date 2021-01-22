package github.j_jzk.circuitsim.gates

import java.awt.Graphics

class Xor: Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 20
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun updateValue() {
	if (inputs.size > 0) {
			var result = false
			for (input in inputs) {
				result = result xor input.value
			}
			value = result
		} else
			value = false
				
		for (output in outputs) {
			output.updateValue()
		}
	}
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)
		g.drawString("X", x+5, y+20)
	}
	
	constructor(s: String) : super(s)
	constructor(x: Int, y: Int) : super(x, y)
}