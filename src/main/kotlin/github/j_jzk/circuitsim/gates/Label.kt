package github.j_jzk.circuitsim.gates

import java.awt.Graphics
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import javax.swing.JOptionPane

class Label: Inconnectable {
	override public val inputs = mutableListOf<Gate>()
	override public val w
		get() = (fontMetrics?.stringWidth(text) ?: 0) + 4
	override public val h = 16
	override public val outputs = mutableListOf<Gate>()
	public var text = "Label text"
	
	private val font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
	private var fontMetrics: FontMetrics? = null
		
	override public fun onClick() {
		text = JOptionPane.showInputDialog("Please enter a text value: ", text) ?: text
	}
	
	override public fun onCreate() = onClick()
	
	override public fun render(g: Graphics) {
		fontMetrics = g.getFontMetrics(font)
		g.drawString(text, x+2, y+14)
	}
	
	override public fun updateValue() {}
	
	override public fun toString(): String = this.javaClass.simpleName + " $x $y $text"
	
	constructor(s: String): super(0, 0) {
		val (x_, y_, text_) = s.split(' ', limit=3)
		x = x_.toInt()
		y = y_.toInt()
		text = text_
	}
	
	constructor(x: Int, y: Int) : super(x, y) 
}