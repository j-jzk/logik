package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Switch(override var x: Int, override var y: Int): Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	
	private var state = false
	
	override public fun getOutput() = state
	
	override public fun onClick() {
		state = !state
	}
	
	override public fun render(g: Graphics) {		
		if (state)
			g.color = Color.RED
		else
			g.color = Color.WHITE
		
		g.fillRect(x+1, y+1, w-1, h-1)
		
		g.color = Color.BLACK
	}
}