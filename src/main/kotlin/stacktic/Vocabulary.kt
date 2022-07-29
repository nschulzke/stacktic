package stacktic

class Vocabulary {
    data class Effect(val before: List<Type>, val after: List<Type>) {
        fun appliesTo(stack: Stack<Type>): Boolean =
            stack.peek(before.size) == before
    }

    data class Definition(
        val name: String,
        val effect: Effect,
        val parseTree: ParseTree,
    ) {
        fun execute(stack: Stack<Value>): Unit =
            parseTree.execute(stack)

        constructor(name: String, effect: Effect, implementation: Stack<Value>.() -> Unit) : this(
            name,
            effect,
            ParseTree.Leaf(implementation)
        )
    }

    private val definitions: Map<String, MutableList<Definition>> = mutableMapOf(
        "+" to mutableListOf(
            Definition("+", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first + second)
            },
            Definition("+", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first + second)
            },
        ),
        "-" to mutableListOf(
            Definition("-", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first - second)
            },
            Definition("-", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first - second)
            },
        ),
        "*" to mutableListOf(
            Definition("*", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first * second)
            },
            Definition("*", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first * second)
            },
        ),
        "/" to mutableListOf(
            Definition("/", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first / second)
            },
            Definition("/", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first / second)
            },
        ),
    )

    operator fun contains(name: String): Boolean =
        name in definitions

    fun definition(name: String, stack: Stack<Type>): Definition? =
        definitions[name]?.firstOrNull { it.effect.appliesTo(stack) }
}