package github.j_jzk.circuitsim.gates

import java.awt.Graphics

class Toggle: Gate {
	override public val w = 25
	override public val h = 25
	
	//this variable tells us whether the state (output) should change when the input's value is 1.
	//if the input is set to 1 and it stays that way, we don't want the output to change when updateValue() is called.
	//if, however, the input changes to 0, we want to toggle the output value the next time the input changes to 1 again.
	private var changeState = true
	
	override public fun updateValue() {
		if (inputs.size > 0) {
			if (inputs[0].value == true && changeState) {
				value = !value
				changeState = false
				
				for (output in outputs) {
					output.updateValue()
				}
			} else if (inputs[0].value == false) {
				//the next time the input changes to 1, we want to toggle the state
				changeState = true
			}
		}
	}
	
	override public fun render(g: Graphics) {
		g.drawRect(x, y, w, h)
		
		//the switch's pivot
		g.fillOval(x+10, y+10, 5, 5)
		
		//the switch's handle
		if (value == true)
			g.drawLine(x+4, y+4, x+21, y+21)
		else
			g.drawLine(x+4, y+21, x+21, y+4)
	}
		
	constructor(s: String) : super(0, 0) {
		val (x_, y_, value_, changeState_) = s.split(' ')
		x = x_.toInt()
		y = y_.toInt()
		value = value_ == "1"
		changeState = changeState_ == "1"
	}
	
	override fun toString() =
		"Toggle $x $y " +
		if (value) "1" else "0" +
		" " + if (changeState) "1" else "0"
	
	constructor(x: Int, y: Int) : super(x, y)
}