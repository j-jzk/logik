package github.j_jzk.circuitsim.gates

import java.awt.Graphics

class Xnor(override var x: Int, override var y: Int): Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 19
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun getOutput(): Boolean {
	if (inputs.size > 0) {
			var result = false
			for (input in inputs) {
				result = result xor input.getOutput()
			}
			return !result
		} else
			return false
	}
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {
		g.drawString("X", x+5, y+20)
		g.drawOval(x + w, y + 9, 6, 6)
	}
}