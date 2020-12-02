package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Font

class And(override var x: Int, override var y: Int): Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 20
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun getOutput(): Boolean {
	if (inputs.size > 0) {
			var result = true
			for (input in inputs) {
				result = result && input.getOutput()
			}
			return result
		} else
			return false
	}
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {		
		//g.getFont().get
		g.drawString("&", x+5, y+20)
	}
}