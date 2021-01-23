package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Switch: Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
//	private var state = false
	
	override public fun updateValue() {}
	
	override public fun onClick() {
		value = !value
				
		for (output in outputs) {
			output.updateValue()
		}
	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)	
		if (value) {
			g.color = Color.RED
			g.fillRect(x+1, y+1, w-2, h-2)
			g.drawRect(x+1, y+1, w-2, h-2) //there was white space on the edges
			g.color = Color.BLACK
		}
	}
	
	constructor(s: String) : super(0, 0) {
		val (x_, y_, state_) = s.split(' ')
		x = x_.toInt()
		y = y_.toInt()
		value = state_ == "1"
	}
	constructor(x: Int, y: Int) : super(x, y)
	
	override fun toString() = this.javaClass.simpleName + " $x $y " + if (value) "1" else "0"
}