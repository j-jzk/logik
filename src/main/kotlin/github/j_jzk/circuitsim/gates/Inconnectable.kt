package github.j_jzk.circuitsim.gates

abstract class Inconnectable: Gate {
	override fun onConnectInput() {
		if (inputs.size > 0) {
			inputs[0].outputs.remove(this)
			inputs.removeAt(0)
		}
	}
	
	override fun onConnectOutput() {
		if (outputs.size > 0) {
			outputs[0].inputs.remove(this)
			outputs.removeAt(0)
		}
	}
	
	constructor(s: String) : super(s)
	constructor(x: Int, y: Int) : super(x, y)
}