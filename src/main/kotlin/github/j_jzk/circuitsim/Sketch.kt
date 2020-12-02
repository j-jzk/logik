package github.j_jzk.circuitsim

import github.j_jzk.circuitsim.gates.*

object Sketch {
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
	
	fun decode(s: String): List<Gate> {
		val (nodesStr, connectionsStr) = s.split("\n\n")
		
		val nodes = arrayOfNulls<Gate>(nodesStr.lines().size)
		for ((i, nodeStr) in nodesStr.lines().withIndex()) {
			if (nodeStr == "") continue
			
			val (type, xS, yS) = nodeStr.split(" ")
			val node: Gate = when(type) {
				"And" -> And(0,0)
				"Lamp" -> Lamp(0,0)
				"Nand" -> Nand(0,0)
				"Nor" -> Nor(0,0)
				"Not" -> Not(0,0)
				"Or" -> Or(0,0)
				"Switch" -> Switch(0,0)
				"Xnor" -> Xnor(0,0)
				"Xor" -> Xor(0,0)
				else -> throw Exception("Invalid gate $type")
			}
			node.x = xS.toInt()
			node.y = yS.toInt()
			
			nodes[i] = node
		}
		
		for (connectionStr in connectionsStr.lines()) {
			val line = connectionStr.split(" ")
			if (line.size == 2) {
				val (a, b) = line.map { it.toInt() }
				
				nodes[b]?.let { nodes[a]?.inputs?.add(it) }
				nodes[a]?.let { nodes[b]?.outputs?.add(it) }
			}
		}
		
		return nodes.map { it!! } //a node won't ever be null
	}
}