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

    fun interpret(tokens: Iterable<Token>) {
        for (token in tokens) {
            when {
                token is IntegerToken -> {
                    stack.push(Value.Integer(parseInt(token.lexeme)))
                }
                token is DoubleToken -> {
                    stack.push(Value.Double(parseDouble(token.lexeme)))
                }
                token.lexeme in vocabulary -> {
                    val definition = vocabulary.definition(token.lexeme, stack) ?: throw RuntimeException("Error, undefined function for stack: ${token.lexeme}")
                    definition.execute(stack)
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