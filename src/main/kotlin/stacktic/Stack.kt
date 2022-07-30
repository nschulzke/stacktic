package stacktic

class Stack<T>(
    initial: List<T> = emptyList()
) {
    private val stack = mutableListOf<T>()
    init {
        stack.addAll(initial)
    }
    fun push(element: T) = stack.add(element)
    fun addAll(after: List<T>) =
        stack.addAll(after)
    fun pop(): T = stack.removeLast()
    fun take(n: Int): List<T> {
        val last = stack.takeLast(n)
        repeat(last.size) {
            pop()
        }
        return last
    }
    fun takeAll(): List<T> {
        val last = stack.toList()
        stack.clear()
        return last
    }
    fun peek(): T = stack[stack.size - 1]
    fun peek(number: Int): List<T> = stack.takeLast(number)
    fun isEmpty() = stack.isEmpty()
    val size get() = stack.size

    fun swap() {
        val a = pop()
        val b = pop()
        push(a)
        push(b)
    }

    fun dup() {
        val a = peek()
        push(a)
    }
}