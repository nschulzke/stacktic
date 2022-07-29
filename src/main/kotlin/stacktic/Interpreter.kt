package stacktic

import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class Interpreter(
    private val vocabulary: Vocabulary = Vocabulary(),
    val printLine: (String) -> Unit = ::println,
) {
    private val stack = Stack<Value>()

    fun interpret(text: String) {
        interpret(Lexer().sequence(text).iterator())
    }

    fun parse(tokens: Iterator<Token>, typeStack: Stack<Type> = Stack()): ParseTree {
        val words = mutableListOf<ParseTree>()
        while (tokens.hasNext()) {
            val token = tokens.next()
            when {
                token is IntegerToken -> {
                    words.add(ParseTree.of(Value.Integer(parseInt(token.lexeme))))
                    typeStack.push(Type.Integer)
                }
                token is DoubleToken -> {
                    words.add(ParseTree.of(Value.Double(parseDouble(token.lexeme))))
                    typeStack.push(Type.Double)
                }
                token.lexeme in vocabulary -> {
                    val definition = vocabulary.definition(token.lexeme, typeStack)
                        ?: throw RuntimeException("Error, undefined function: ${token.lexeme}")
                    words.add(definition.parseTree)
                    typeStack.take(definition.effect.before.size)
                    typeStack.addAll(definition.effect.after)
                }
                token == Dot -> {
                    words.add(prettyPrint)
                    typeStack.pop()
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
        val before = mutableListOf<Type>()
        while (true) {
            val token = tokens.next()
            if (token == LongDash) break
            before.add(Type.of(token) ?: throw Error("Unknown type: $token"))
        }
        val after = mutableListOf<Type>()
        while (true) {
            val token = tokens.next()
            if (token == RParen) break
            after.add(Type.of(token) ?: throw Error("Unknown type: $token"))
        }
        val typeStack = Stack(before)
        val parseTree = parse(tokens, typeStack)
        val actualAfter = typeStack.takeAll()
        if (actualAfter != after) {
            throw Error("ERROR Invalid stack effect: Expected to end with `${after.joinToString(" ")}`; was `${actualAfter.joinToString(" ")}`")
        }
        return Vocabulary.Definition(nameToken.lexeme, before, after, parseTree)
    }

    fun interpret(tokens: Iterator<Token>) {
        parse(tokens).execute(stack)
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