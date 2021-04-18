/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package github.j_jzk.circuitsim

import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.UIManager
import com.formdev.flatlaf.FlatLightLaf

class App: JFrame("Logic Circuit Simulator") {
	lateinit var vp: Viewport
	
	fun initGui() {		
		setLayout(BorderLayout())
		defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
		setSize(800, 600)
		
		val statusBar = JLabel("")
		vp = Viewport(statusBar)
		add(vp, BorderLayout.CENTER)
		add(Toolbar(vp), BorderLayout.LINE_END)
		add(statusBar, BorderLayout.PAGE_END)
		
		pack()
		
		setVisible(true)
	}
	
	override fun processWindowEvent(e: WindowEvent) {
		if (e.id == WindowEvent.WINDOW_CLOSING && vp.confirmClose())
			this.dispose()
	}
}

fun main(args: Array<String>) {
	val app = App()
	
	//use anti-aliased fonts - doesn't work for some reason
	System.setProperty("awt.useSystemAAFontSettings", "on")
	System.setProperty("swing.aatext", "true")
	
	
	
	try {
		UIManager.setLookAndFeel(FlatLightLaf())
//		SwingUtilities.updateComponentTreeUI(app)
	} catch (e: Exception) {
		//do nothing, continue with the default look and feel
		println("FlatLAF couldn't be loaded, continuing with default LaF")
	}
	
	// configure look and feel
	UIManager.put("Component.hideMnemonics", false)
	
	
	EventQueue.invokeLater { app.initGui() }
}
