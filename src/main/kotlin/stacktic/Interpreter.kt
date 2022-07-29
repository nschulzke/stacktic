package stacktic

import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class Interpreter(
    private val vocabulary: Vocabulary = Vocabulary(),
    val printLine: (String) -> Unit = ::println,
) {
    private val stack = Stack<Value>()

    fun interpret(text: String) {
        interpret(Lexer().read(text))
    }

    fun parse(tokens: List<Token>, start: Int = 0, typeStack: Stack<Type> = Stack()): Pair<ParseTree, Int> {
        val words = mutableListOf<ParseTree>()
        var currentToken = start
        while (currentToken < tokens.size) {
            val token = tokens[currentToken]
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
                    val (definition, newToken) = parseWord(tokens, currentToken + 1)
                    currentToken = newToken
                    vocabulary.define(definition)
                }
                token == Semicolon -> {
                    return ParseTree.of(words) to currentToken
                }
                else -> {
                    throw Error("Unknown word: ${token.lexeme}")
                }
            }
            currentToken++
        }
        return ParseTree.of(words) to currentToken
    }

    fun parseWord(tokens: List<Token>, start: Int = 0): Pair<Vocabulary.Definition, Int> {
        var currentToken = start
        val nameToken = tokens[currentToken++]
        if (nameToken !is SymbolToken) {
            throw Error("Expected symbol, got: ${nameToken.lexeme}")
        }
        val lParen = tokens[currentToken++]
        if (lParen != LParen) {
            throw Error("Expected (, got: ${lParen.lexeme}")
        }
        val before = mutableListOf<Type>()
        while (tokens[currentToken] != LongDash) {
            before.add(Type.of(tokens[currentToken++]) ?: throw Error("Unknown type: ${tokens[currentToken - 1].lexeme}"))
        }
        currentToken++ // Skip long dash
        val after = mutableListOf<Type>()
        while (tokens[currentToken] != RParen) {
            after.add(Type.of(tokens[currentToken++]) ?: throw Error("Unknown type: ${tokens[currentToken - 1].lexeme}"))
        }
        currentToken++ // Skip right paren
        val typeStack = Stack(before)
        val (parseTree, newToken) = parse(tokens, currentToken, typeStack)
        val actualAfter = typeStack.takeAll()
        if (actualAfter != after) {
            throw Error("ERROR Invalid stack effect: Expected to end with `${after.joinToString(" ")}`; was `${actualAfter.joinToString(" ")}`")
        }
        if (newToken < tokens.size && tokens[newToken] == Semicolon) {
            return Vocabulary.Definition(nameToken.lexeme, before, after, parseTree) to newToken
        }
        throw Error("Expected ';'")
    }

    fun interpret(tokens: List<Token>) {
        val (tree) = parse(tokens)
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