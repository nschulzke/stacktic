package stacktic

class Vocabulary {
    data class Effect(val before: List<Type>, val after: List<Type>) {
        fun appliesTo(stack: Stack<Value>): Boolean =
            stack.peek(before.size).map { it.type } == before
    }

    sealed interface Definition {
        val effect: Effect
        fun execute(stack: Stack<Value>)

        data class User(
            val name: String,
            override val effect: Effect,
            val words: List<Definition>,
        ) : Definition {
            override fun execute(stack: Stack<Value>) {
                words.forEach { it.execute(stack) }
            }
        }
        data class BuiltIn(
            val name: String,
            override val effect: Effect,
            val implementation: Stack<Value>.() -> Unit,
        ) : Definition {
            override fun execute(stack: Stack<Value>) {
                stack.implementation()
            }
        }
    }

    private val definitions: Map<String, MutableList<Definition>> = mutableMapOf(
        "+" to mutableListOf(
            Definition.BuiltIn("+", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first + second)
            },
            Definition.BuiltIn("+", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first + second)
            },
        ),
        "-" to mutableListOf(
            Definition.BuiltIn("-", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first - second)
            },
            Definition.BuiltIn("-", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first - second)
            },
        ),
        "*" to mutableListOf(
            Definition.BuiltIn("*", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first * second)
            },
            Definition.BuiltIn("*", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first * second)
            },
        ),
        "/" to mutableListOf(
            Definition.BuiltIn("/", Effect(listOf(Type.Integer, Type.Integer), listOf(Type.Integer))) {
                val second = pop() as Value.Integer
                val first = pop() as Value.Integer
                push(first / second)
            },
            Definition.BuiltIn("/", Effect(listOf(Type.Double, Type.Double), listOf(Type.Double))) {
                val second = pop() as Value.Double
                val first = pop() as Value.Double
                push(first / second)
            },
        ),
    )

    operator fun contains(name: String): Boolean =
        name in definitions

    fun definition(name: String, stack: Stack<Value>): Definition? =
        definitions[name]?.firstOrNull { it.effect.appliesTo(stack) }
}