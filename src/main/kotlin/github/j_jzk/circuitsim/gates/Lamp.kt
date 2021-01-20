package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Lamp: Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun getOutput() = if (inputs.size > 0) inputs[0].getOutput() else false
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)	
		if (getOutput() == true)
			g.color = Color.YELLOW
		else
			g.color = Color.BLACK
		
		g.fillRect(x+1, y+1, w-1, h-1)
		
		g.color = Color.BLACK
		g.drawLine(x, y, x+w, y+h)
		g.drawLine(x+w, y, x, y+h)
	}
	
	constructor(s: String) : super(s)
	constructor(x: Int, y: Int) : super(x, y)
}