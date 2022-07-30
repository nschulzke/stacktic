package stacktic

import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class Interpreter(
    val printLine: (String) -> Unit = ::println,
) {
    private val vocabulary: Vocabulary = Vocabulary()
    private val stackStack = Stack<Stack<Value>>(listOf(Stack()))
    private val stack get() = stackStack.peek()

    fun parse(tokens: Iterator<Token>): ParseTree {
        val words = mutableListOf<ParseTree>()
        while (tokens.hasNext()) {
            val token = tokens.next()
            when {
                token is IntegerToken -> {
                    words.add(ParseTree.of(Value.Integer(parseInt(token.lexeme))))
                    stack.push(Type.Integer)
                }
                token is DoubleToken -> {
                    words.add(ParseTree.of(Value.Double(parseDouble(token.lexeme))))
                    stack.push(Type.Double)
                }
                token.lexeme in vocabulary -> {
                    val definition = vocabulary.definition(token.lexeme, stack)
                        ?: throw RuntimeException("Error, undefined function: ${token.lexeme}")
                    if (definition.immediate) {
                        definition.parseTree.execute(stack, tokens)
                    } else {
                        words.add(definition.parseTree)
                        stack.take(definition.effect.input.size)
                        stack.addAll(definition.effect.output)
                    }
                }
                token == Dot -> {
                    words.add(prettyPrint)
                    stack.pop()
                }
                token == Semicolon -> {
                    return ParseTree.of(words)
                }
                else -> {
                    throw Error("Unknown word: ${token.lexeme}")
                }
            }
        }
        return ParseTree.of(words)
    }

    fun interpret(tokens: Iterator<Token>) {
        stackStack.push(Stack())
        val tree = parse(tokens)
        stackStack.pop()
        tree.execute(stack, tokens)
    }

    companion object {
        val Dot = SymbolToken(".")
        val Semicolon = SymbolToken(";")
        val LParen = SymbolToken("(")
        val RParen = SymbolToken(")")
        val LongDash = SymbolToken("--")
    }

    private val prettyPrint: ParseTree = ParseTree.of {
        printLine(this.pop().toString())
    }

    data class Effect(val input: List<Value>, val output: List<Value>) {
        fun appliesTo(stack: Stack<Value>): Boolean =
            stack.peek(input.size) == input
    }

    data class Definition(
        val name: String,
        val effect: Effect,
        val immediate: Boolean = false,
        val parseTree: ParseTree,
    ) {
        constructor(name: String, input: List<Value>, output: List<Value>, parseTree: ParseTree) :
                this(name, Effect(input, output), false, parseTree)

        constructor(name: String, effect: Effect, immediate: Boolean = false, implementation: Stack<Value>.(tokens: Iterator<Token>) -> Unit) : this(
            name = name,
            effect = effect,
            immediate = immediate,
            parseTree = ParseTree.Leaf(implementation)
        )
    }

    inner class Vocabulary {
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
            ":" to mutableListOf(
                Definition(":", Effect(emptyList(), emptyList()), immediate = true) { tokens ->
                    val nameToken = tokens.next()
                    if (nameToken !is SymbolToken) {
                        throw Error("Expected symbol, got: ${nameToken.lexeme}")
                    }
                    stack.push(Value.String(nameToken.lexeme))

                    val lParen = tokens.next()
                    if (lParen != LParen) {
                        throw Error("Expected (, got: ${lParen.lexeme}")
                    }

                    stackStack.push(Stack())

                    while (true) {
                        val token = tokens.next()
                        if (token == LongDash) break
                        stack.push(Type.of(token) ?: throw Error("Unknown type: $token"))
                    }

                    stackStack.push(Stack())

                    while (true) {
                        val token = tokens.next()
                        if (token == RParen) break
                        stack.push(Type.of(token) ?: throw Error("Unknown type: $token"))
                    }

                    stackStack.swap()
                    stackStack.dup()

                    val parseTree = parse(tokens)
                    val actualOutput = stackStack.pop().takeAll()
                    val declaredInput = stackStack.pop().takeAll()
                    val declaredOutput = stackStack.pop().takeAll()
                    val name = stack.pop() as Value.String
                    if (actualOutput != declaredOutput) {
                        throw Error("ERROR Invalid stack effect: Expected to end with `${declaredOutput.joinToString(" ")}`; was `${actualOutput.joinToString(" ")}`")
                    }
                    define(Definition(name.value, declaredInput, declaredOutput, parseTree))
                }
            )
        )

        fun define(definition: Definition): Boolean =
            definitions.getOrPut(definition.name) { mutableListOf() }.add(definition)

        operator fun contains(name: String): Boolean =
            name in definitions

        fun definition(name: String, stack: Stack<Value>): Definition? =
            definitions[name]?.firstOrNull { it.effect.appliesTo(stack) }
    }
}