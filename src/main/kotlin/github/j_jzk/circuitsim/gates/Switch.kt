package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color

class Switch: Gate {
	override public val inputs = mutableListOf<Gate>()
	override public val w = 25
	override public val h = 25
	override public val outputs = mutableListOf<Gate>()
	
	private var state = false
	
	override public fun getOutput() = state
	
	override public fun onClick() {
		state = !state
	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)	
		if (state) {
			g.color = Color.RED
			g.fillRect(x+1, y+1, w-1, h-1)
			g.color = Color.BLACK
		}
	}
	
	constructor(s: String) : super(0, 0) {
		val (x_, y_, state_) = s.split(' ')
		x = x_.toInt()
		y = y_.toInt()
		state = state_ == "1"
	}
	constructor(x: Int, y: Int) : super(x, y)
	
	override fun toString() = this.javaClass.simpleName + " $x $y " + if (state) "1" else "0"
}