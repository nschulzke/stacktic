package stacktic

import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class Interpreter(
    private val vocabulary: Vocabulary = Vocabulary(),
    val printLine: (String) -> Unit = ::println,
) {
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
                    words.add(definition.parseTree)
                    stack.take(definition.effect.input.size)
                    stack.addAll(definition.effect.output)
                }
                token == Dot -> {
                    words.add(prettyPrint)
                    stack.pop()
                }
                token == Colon -> {
                    val definition = parseWord(tokens)
                    vocabulary.define(definition)
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

    fun parseWord(tokens: Iterator<Token>): Vocabulary.Definition {
        val nameToken = tokens.next()
        if (nameToken !is SymbolToken) {
            throw Error("Expected symbol, got: ${nameToken.lexeme}")
        }
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
        if (actualOutput != declaredOutput) {
            throw Error("ERROR Invalid stack effect: Expected to end with `${declaredOutput.joinToString(" ")}`; was `${actualOutput.joinToString(" ")}`")
        }
        return Vocabulary.Definition(nameToken.lexeme, declaredInput, declaredOutput, parseTree)
    }

    fun interpret(tokens: Iterator<Token>) {
        stackStack.push(Stack())
        val tree = parse(tokens)
        stackStack.pop()
        tree.execute(stack)
    }

    companion object {
        val Dot = SymbolToken(".")
        val Colon = SymbolToken(":")
        val Semicolon = SymbolToken(";")
        val LParen = SymbolToken("(")
        val RParen = SymbolToken(")")
        val LongDash = SymbolToken("--")
    }

    val prettyPrint: ParseTree = ParseTree.of {
        printLine(this.pop().toString())
    }
}