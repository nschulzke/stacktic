package stacktic

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class InterpreterTest {
    @Test
    fun `interprets simple addition`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("1 2 + .")
        }.removeSuffix("\n")
        assertEquals("3", output)
    }

    @Test
    fun `interprets simple subtraction`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("3 2 - .")
        }.removeSuffix("\n")
        assertEquals("1", output)
    }

    @Test
    fun `interprets simple multiplication`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("2 3 * .")
        }.removeSuffix("\n")
        assertEquals("6", output)
    }

    @Test
    fun `interprets simple division`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("6 2 / .")
        }.removeSuffix("\n")
        assertEquals("3", output)
    }

    @Test
    fun `interprets addition with doubles`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("1.5 2.5 + .")
        }.removeSuffix("\n")
        assertEquals("4.0", output)
    }

    @Test
    fun `interprets subtraction with doubles`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("3.5 2.5 - .")
        }.removeSuffix("\n")
        assertEquals("1.0", output)
    }

    @Test
    fun `interprets multiplication with doubles`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("3.0 3.0 * .")
        }.removeSuffix("\n")
        assertEquals("9.0", output)
    }

    @Test
    fun `interprets division with doubles`() {
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret("9.0 3.0 / .")
        }.removeSuffix("\n")
        assertEquals("3.0", output)
    }

    @Test
    fun `can use a custom word`() {
        val source = """
            : +2 ( Integer -- Integer ) 2 + ;
            2 +2 .
        """.trimIndent()
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret(source)
        }.removeSuffix("\n")
        assertEquals("4", output)
    }

    @Test
    fun `can use two custom words`() {
        val source = """
            : +2 ( Integer -- Integer ) 2 + ;
            : -2 ( Integer -- Integer ) 2 - ;
            2 +2 -2 .
        """.trimIndent()
        val output = buildString {
            val interpreter = Interpreter { appendLine(it) }
            interpreter.interpret(source)
        }.removeSuffix("\n")
        assertEquals("2", output)
    }

    @Test
    fun `gives error if before signature doesn't match`() {
        val source = """
            : +2 ( Integer Integer -- Integer ) 2 + ;
            2 +2 -2 .
        """.trimIndent()
        val error = assertThrows<Error> {
            val interpreter = Interpreter()
            interpreter.interpret(source)
        }
        assertEquals("ERROR Invalid stack effect: Expected to end with `Integer`; was `Integer Integer`", error.message)
    }

    @Test
    fun `gives error if after signature doesn't match`() {
        val source = """
            : +2 ( Integer -- Integer Integer ) 2 + ;
            2 +2 .
        """.trimIndent()
        val error = assertThrows<Error> {
            val interpreter = Interpreter()
            interpreter.interpret(source)
        }
        assertEquals("ERROR Invalid stack effect: Expected to end with `Integer Integer`; was `Integer`", error.message)
    }

    fun Interpreter.interpret(text: String) {
        interpret(Lexer().sequence(text).iterator())
    }
}