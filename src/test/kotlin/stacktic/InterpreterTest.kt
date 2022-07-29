package stacktic

import org.junit.jupiter.api.Test
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
}