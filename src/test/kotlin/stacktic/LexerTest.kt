package stacktic

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LexerTest {
    @Test
    fun `lexes a series of symbols`() {
        val source = "a series of symbols"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                SymbolToken("a"),
                SymbolToken("series"),
                SymbolToken("of"),
                SymbolToken("symbols")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of strings`() {
        val source = """
            "a series" "of strings"
        """.trimIndent()
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                StringToken("\"a series\""),
                StringToken("\"of strings\"")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of strings and symbols`() {
        val source = """
            "a series" "of strings" a series of symbols
        """.trimIndent()
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                StringToken("\"a series\""),
                StringToken("\"of strings\""),
                SymbolToken("a"),
                SymbolToken("series"),
                SymbolToken("of"),
                SymbolToken("symbols")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of integers`() {
        val source = "1 2 3 4 5"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                IntegerToken("1"),
                IntegerToken("2"),
                IntegerToken("3"),
                IntegerToken("4"),
                IntegerToken("5")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of big integers`() {
        val source = "1000000000 10000000000 100000000000 1000000000000 10000000000000"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                IntegerToken("1000000000"),
                IntegerToken("10000000000"),
                IntegerToken("100000000000"),
                IntegerToken("1000000000000"),
                IntegerToken("10000000000000")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of doubles`() {
        val source = "1.0 2.0 3.0 4.0 5.0"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                DoubleToken("1.0"),
                DoubleToken("2.0"),
                DoubleToken("3.0"),
                DoubleToken("4.0"),
                DoubleToken("5.0")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of big doubles`() {
        val source = "1000000000.0 10000000000.0 100000000000.0 1000000000000.0 10000000000000.0"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                DoubleToken("1000000000.0"),
                DoubleToken("10000000000.0"),
                DoubleToken("100000000000.0"),
                DoubleToken("1000000000000.0"),
                DoubleToken("10000000000000.0")
            ),
            actual = tokens
        )
    }

    @Test
    fun `lexes a series of big doubles with lots of decimals`() {
        val source = "1000000000.123456789 10000000000.123456789 100000000000.123456789 1000000000000.123456789 10000000000000.123456789"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                DoubleToken("1000000000.123456789"),
                DoubleToken("10000000000.123456789"),
                DoubleToken("100000000000.123456789"),
                DoubleToken("1000000000000.123456789"),
                DoubleToken("10000000000000.123456789")
            ),
            actual = tokens
        )
    }

    @Test
    fun `symbols may contain special characters`() {
        val source = "a-b_c.d_e"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                SymbolToken("a-b_c.d_e")
            ),
            actual = tokens
        )
    }

    @Test
    fun `math symbols are interpreted as symbols`() {
        val source = "* / - +"
        val tokens = Lexer().read(source)
        assertEquals(
            expected = listOf(
                SymbolToken("*"),
                SymbolToken("/"),
                SymbolToken("-"),
                SymbolToken("+")
            ),
            actual = tokens
        )
    }
}