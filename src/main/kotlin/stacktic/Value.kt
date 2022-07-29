package stacktic

sealed interface Value {
    val type: Type
    @JvmInline
    value class String(val value: kotlin.String) : Value {
        override val type: Type get() = Type.String
    }
    @JvmInline
    value class Integer(val value: Int) : Value {
        override val type: Type get() = Type.Integer
        override fun toString() = value.toString()
        operator fun plus(other: Integer): Integer = Integer(value + other.value)
        operator fun minus(other: Integer): Integer = Integer(value - other.value)
        operator fun times(other: Integer): Integer = Integer(value * other.value)
        operator fun div(other: Integer): Integer = Integer(value / other.value)
        operator fun rem(other: Integer): Integer = Integer(value % other.value)
        operator fun unaryPlus(): Integer = Integer(+value)
        operator fun unaryMinus(): Integer = Integer(-value)
        operator fun inc(): Integer = Integer(value + 1)
        operator fun dec(): Integer = Integer(value - 1)
    }
    @JvmInline
    value class Double(val value: kotlin.Double) : Value {
        override val type: Type get() = Type.Double
        override fun toString() = value.toString()
        operator fun plus(other: Double): Double = Double(value + other.value)
        operator fun minus(other: Double): Double = Double(value - other.value)
        operator fun times(other: Double): Double = Double(value * other.value)
        operator fun div(other: Double): Double = Double(value / other.value)
        operator fun rem(other: Double): Double = Double(value % other.value)
        operator fun unaryPlus(): Double = Double(+value)
        operator fun unaryMinus(): Double = Double(-value)
        operator fun inc(): Double = Double(value + 1.0)
        operator fun dec(): Double = Double(value - 1.0)
    }
}