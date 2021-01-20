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
			nodesStr += node.toString() + "\n"                                        // ...
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
			
			val (type, properties) = nodeStr.split(" ", limit=2)
			val node: Gate = when(type) {
				"And" -> And(properties)
				"Lamp" -> Lamp(properties)
				"Nand" -> Nand(properties)
				"Nor" -> Nor(properties)
				"Not" -> Not(properties)
				"Or" -> Or(properties)
				"Switch" -> Switch(properties)
				"Xnor" -> Xnor(properties)
				"Xor" -> Xor(properties)
				"Label" -> Label(properties)
				else -> throw Exception("Invalid gate $type")
			}
			/*node.x = xS.toInt()
			node.y = yS.toInt()*/
			
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