package github.j_jzk.circuitsim.gates

import java.awt.Graphics

abstract class Gate {
	open public val inputs: MutableList<Gate> = mutableListOf<Gate>()
	open public val outputs: MutableList<Gate> = mutableListOf<Gate>()
	abstract public fun getOutput(): Boolean
	
	open public var x: Int = 0
	open public var y: Int = 0
	abstract public val w: Int
	abstract public val h: Int
	abstract public fun render(g: Graphics)
	
	open public fun onClick() { }
	open public fun onDelete() { }
	open public fun onConnectInput() { }
	open public fun onConnectOutput() { }
	open public fun onCreate() { }
	
	override public fun toString() = "${this.javaClass.getSimpleName()} $x $y"
	
	public constructor(x: Int, y: Int) {
		this.x = x
		this.y = y
	}
	
	public constructor(s: String) {
		val (x_, y_) = s.split(' ').map { it.toInt() }
		x = x_
		y = y_
	}
	
}