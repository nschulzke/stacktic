package stacktic

class Vocabulary {
    data class Effect(val input: List<Value>, val output: List<Value>) {
        fun appliesTo(stack: Stack<Value>): Boolean =
            stack.peek(input.size) == input
    }

    data class Definition(
        val name: String,
        val effect: Effect,
        val parseTree: ParseTree,
    ) {
        constructor(name: String, input: List<Value>, output: List<Value>, parseTree: ParseTree) :
            this(name, Effect(input, output), parseTree)

        fun execute(stack: Stack<Value>): Unit =
            parseTree.execute(stack)

        constructor(name: String, effect: Effect, implementation: Stack<Value>.() -> Unit) : this(
            name,
            effect,
            ParseTree.Leaf(implementation)
        )
    }

    private val definitions: MutableMap<String, MutableList<Definition>> = mutableMapOf(
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

    fun define(definition: Definition): Boolean =
        definitions.getOrPut(definition.name) { mutableListOf() }.add(definition)

    operator fun contains(name: String): Boolean =
        name in definitions

    fun definition(name: String, stack: Stack<Value>): Definition? =
        definitions[name]?.firstOrNull { it.effect.appliesTo(stack) }
}