package stacktic

sealed interface ParseTree {
    fun execute(stack: Stack<Value>)
    
    data class Branch(val nodes: List<ParseTree>) : ParseTree {
        override fun execute(stack: Stack<Value>) {
            for (node in nodes) {
                node.execute(stack)
            }
        }
    }

    data class Leaf(val implementation: Stack<Value>.() -> Unit) : ParseTree {
        override fun execute(stack: Stack<Value>) {
            stack.implementation()
        }
    }
}

