package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Lamp(override var x: Int, override var y: Int): Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	
	
	override public fun getOutput() = inputs[0].getOutput() ?: false
	
	override public fun onClick() {	}
	
	override public fun render(g: Graphics) {		
		if (getOutput() == true)
			g.color = Color.YELLOW
		else
			g.color = Color.BLACK
		
		g.fillRect(x+1, y+1, w-1, h-1)
		
		g.color = Color.BLACK
		g.drawLine(x, y, x+w, y+h)
		g.drawLine(x+w, y, x, y+h)
	}
}