package stacktic

import java.io.BufferedReader
import java.io.InputStream

sealed interface Token {
    val lexeme: String
}
data class SymbolToken(override val lexeme: String) : Token
data class IntegerToken(override val lexeme: String) : Token
data class DoubleToken(override val lexeme: String) : Token
data class StringToken(override val lexeme: String) : Token

class Lexer(
    stream: InputStream,
) {
    private val reader: BufferedReader = stream.bufferedReader()
    fun readLine(): List<Token> {
        val line = reader.readLine() ?: return emptyList()
        var currentChar = 0
        val tokens = mutableListOf<Token>()
        while (currentChar < line.length) {
            val char = line[currentChar]
            when {
                char.isWhitespace() -> {
                    currentChar++
                }
                char.isDigit() -> {
                    val token = readNumber(line, currentChar)
                    currentChar += token.lexeme.length
                    tokens.add(token)
                }
                char == '"' -> {
                    val token = readString(line, currentChar)
                    currentChar += token.lexeme.length
                    tokens.add(token)
                }
                else -> {
                    val token = readSymbol(line, currentChar)
                    currentChar += token.lexeme.length
                    tokens.add(token)
                }
            }
        }
        return tokens
    }

    private fun readNumber(line: String, start: Int): Token {
        var currentChar = start
        var foundDot = false
        while (currentChar < line.length && (line[currentChar].isDigit() || (!foundDot && line[currentChar] == '.'))) {
            if (line[currentChar] == '.') {
                foundDot = true
            }
            currentChar++
        }
        if (currentChar < line.length && !line[currentChar].isWhitespace()) {
            throw Error("Unexpected character in ${if (foundDot) "DoubleToken" else "IntegerToken"}: `${line[currentChar]}`")
        }
        return if (foundDot) {
            DoubleToken(line.substring(start, currentChar))
        } else {
            IntegerToken(line.substring(start, currentChar))
        }
    }

    private fun readSymbol(line: String, start: Int): Token {
        var currentChar = start
        while (currentChar < line.length && !line[currentChar].isWhitespace()) {
            currentChar++
        }
        return SymbolToken(line.substring(start, currentChar))
    }

    private fun readString(line: String, start: Int): Token {
        var currentChar = start + 1
        while (currentChar < line.length && line[currentChar] != '"') {
            currentChar++
        }
        if (line[currentChar] != '"') {
            throw Error("Unclosed string")
        }
        return StringToken(line.substring(start, currentChar + 1))
    }
}