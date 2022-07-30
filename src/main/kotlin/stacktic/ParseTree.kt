package stacktic

sealed interface ParseTree {
    fun execute(stack: Stack<Value>, tokens: Iterator<Token>)

    data class Branch(val nodes: List<ParseTree>) : ParseTree {
        override fun execute(stack: Stack<Value>, tokens: Iterator<Token>) {
            for (node in nodes) {
                node.execute(stack, tokens)
            }
        }
    }

    data class Leaf(val implementation: Stack<Value>.(tokens: Iterator<Token>) -> Unit) : ParseTree {
        override fun execute(stack: Stack<Value>, tokens: Iterator<Token>) {
            stack.implementation(tokens)
        }
    }

    data class LiteralValue(val value: Value) : ParseTree {
        override fun execute(stack: Stack<Value>, tokens: Iterator<Token>) {
            stack.push(value)
        }
    }

    companion object {
        fun of(value: Value): ParseTree = LiteralValue(value)

        fun of(nodes: List<ParseTree>): ParseTree = Branch(nodes)

        fun of(implementation: Stack<Value>.(tokens: Iterator<Token>) -> Unit): ParseTree = Leaf(implementation)
    }
}

