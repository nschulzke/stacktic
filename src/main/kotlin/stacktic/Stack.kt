package stacktic

class Stack<T> {
    private val stack = mutableListOf<T>()
    fun push(element: T) = stack.add(element)
    fun pop(): T = stack.removeLast()
    fun peek(): T = stack[stack.size - 1]
    fun peek(number: Int): List<T> = stack.takeLast(number)
    fun isEmpty() = stack.isEmpty()
    val size get() = stack.size
}