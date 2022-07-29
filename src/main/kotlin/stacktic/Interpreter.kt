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

    fun parse(tokens: Iterable<Token>): ParseTree {
        val words = mutableListOf<ParseTree>()
        val typeStack = Stack<Type>()
        for (token in tokens) {
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
                }
                token == Dot -> {
                    words.add(prettyPrint)
                    typeStack.pop()
                }
                else -> {
                    throw Error("Unknown word: ${token.lexeme}")
                }
            }
        }
        return ParseTree.of(words)
    }

    fun interpret(tokens: Iterable<Token>) {
        parse(tokens).execute(stack)
    }

    companion object {
        val Dot = SymbolToken(".")
        val Colon = SymbolToken(":")
        val Semicolon = SymbolToken(";")
        val LParen = SymbolToken("(")
    }

    val prettyPrint: ParseTree = ParseTree.of {
        printLine(this.pop().toString())
    }
}