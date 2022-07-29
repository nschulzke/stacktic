package stacktic

sealed interface Token {
    val lexeme: String
}
data class SymbolToken(override val lexeme: String) : Token
data class IntegerToken(override val lexeme: String) : Token
data class DoubleToken(override val lexeme: String) : Token
data class StringToken(override val lexeme: String) : Token

class Lexer {
    fun sequence(source: String): Sequence<Token> {
        return sequence {
            var currentChar = 0
            while (currentChar < source.length) {
                val char = source[currentChar]
                when {
                    char.isWhitespace() -> {
                        currentChar++
                    }
                    char.isDigit() -> {
                        val token = readNumber(source, currentChar)
                        currentChar += token.lexeme.length
                        yield(token)
                    }
                    char == '"' -> {
                        val token = readString(source, currentChar)
                        currentChar += token.lexeme.length
                        yield(token)
                    }
                    else -> {
                        val token = readSymbol(source, currentChar)
                        currentChar += token.lexeme.length
                        yield(token)
                    }
                }
            }
        }
    }

    fun read(source: String): List<Token> {
        return sequence(source).toList()
    }

    private fun readNumber(source: String, start: Int): Token {
        var currentChar = start
        var foundDot = false
        while (currentChar < source.length && (source[currentChar].isDigit() || (!foundDot && source[currentChar] == '.'))) {
            if (source[currentChar] == '.') {
                foundDot = true
            }
            currentChar++
        }
        if (currentChar < source.length && !source[currentChar].isWhitespace()) {
            throw Error("Unexpected character in ${if (foundDot) "DoubleToken" else "IntegerToken"}: `${source[currentChar]}`")
        }
        return if (foundDot) {
            DoubleToken(source.substring(start, currentChar))
        } else {
            IntegerToken(source.substring(start, currentChar))
        }
    }

    private fun readSymbol(source: String, start: Int): Token {
        var currentChar = start
        while (currentChar < source.length && !source[currentChar].isWhitespace()) {
            currentChar++
        }
        return SymbolToken(source.substring(start, currentChar))
    }

    private fun readString(source: String, start: Int): Token {
        var currentChar = start + 1
        while (currentChar < source.length && source[currentChar] != '"') {
            currentChar++
        }
        if (source[currentChar] != '"') {
            throw Error("Unclosed string")
        }
        return StringToken(source.substring(start, currentChar + 1))
    }
}