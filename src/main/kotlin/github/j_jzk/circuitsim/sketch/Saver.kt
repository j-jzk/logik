package github.j_jzk.circuitsim.sketch

import github.j_jzk.circuitsim.gates.Gate

object Saver {
	fun encode(nodes: Array<Gate>): String {
		var nodesStr = ""
		var connectionsStr = ""
		
		val indices = HashMap<Gate, Int>(nodes.size) //for speed improvement        ------+
		for (i in nodes.indices) {                                                    //  |
			indices[nodes[i]] = i                                                     // ...
		}
		
		for ((i, node) in nodes.withIndex()) {
			nodesStr += node.javaClass.getSimpleName() + " ${node.x} ${node.y}\n"     // ...
			for (input in node.inputs) {                                              //  |
				connectionsStr += "$i ${indices[input]}\n"             // <-----here------+
			}
		}
		
		return nodesStr + "\n" + connectionsStr
	}
}