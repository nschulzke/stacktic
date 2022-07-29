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
}