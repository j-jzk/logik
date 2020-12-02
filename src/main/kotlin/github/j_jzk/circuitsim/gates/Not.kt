package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Not(override var x: Int, override var y: Int): Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 19
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun getOutput() = if (inputs.size > 0) !inputs[0].getOutput() else true
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {		
		g.drawOval(x + w, y + 9, 6, 6)
	}
}