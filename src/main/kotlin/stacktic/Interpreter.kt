package stacktic

import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class Interpreter(
    val printLine: (String) -> Unit = ::println
) {
    data class Effect(val before: List<Type>, val after: List<Type>) {
        fun appliesTo(stack: Stack<Value>): Boolean =
            stack.peek(before.size).map { it.type } == before
    }
    private val stack = Stack<Value>()

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

    fun interpret(text: String) {
        interpret(Lexer().read(text))
    }

    fun interpret(tokens: Iterable<Token>) {
        for (token in tokens) {
            when {
                token is IntegerToken -> {
                    stack.push(Value.Integer(parseInt(token.lexeme)))
                }
                token is DoubleToken -> {
                    stack.push(Value.Double(parseDouble(token.lexeme)))
                }
                token.lexeme in definitions -> {
                    definitions[token.lexeme]!!.first { it.effect.appliesTo(stack) }.execute(stack)
                }
                token == Dot -> {
                    printLine(stack.pop().toString())
                }
                else -> {
                    throw Error("Unknown word: ${token.lexeme}")
                }
            }
        }
    }

    companion object {
        val Dot = SymbolToken(".")
        val Colon = SymbolToken(":")
        val Semicolon = SymbolToken(";")
        val LParen = SymbolToken("(")
    }
}