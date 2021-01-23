package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Lamp: Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	
	override public fun updateValue() {
		value = if (inputs.size > 0) inputs[0].value else false
				
		for (output in outputs) {
			output.updateValue()
		}
	}
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)	
		if (value == true)
			g.color = Color.YELLOW
		else
			g.color = Color.BLACK
		
		g.fillRect(x+1, y+1, w-2, h-2)
		g.drawRect(x+1, y+1, w-2, h-2) //there was white space on the edges
		
		g.color = Color.BLACK
		g.drawLine(x+1, y+1, x+w-1, y+h-1)
		g.drawLine(x+w-1, y+1, x+1, y+h-1)
	}
	
	constructor(s: String) : super(s)
	constructor(x: Int, y: Int) : super(x, y)
}