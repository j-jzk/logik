package github.j_jzk.circuitsim.gates

import java.awt.Graphics

interface Gate {
	public val inputs: MutableList<Gate>
	public fun getOutput(): Boolean
	
	public var x: Int
	public var y: Int
	public val w: Int
	public val h: Int
	public fun onClick()
	public fun render(g: Graphics)
}