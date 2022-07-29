package stacktic

sealed interface Type : Value {
    object TypeLiteral : Type {
        override val type: Type = TypeLiteral
        override fun toString() = "Type"
    }

    object String : Type {
        override val type: Type = TypeLiteral
        override fun toString() = "String"
    }
    object Integer : Type {
        override val type: Type = TypeLiteral
        override fun toString() = "Integer"
    }
    object Double : Type {
        override val type: Type = TypeLiteral
        override fun toString() = "Double"
    }

    companion object {
        fun of(token: Token): Type? =
            when (token) {
                SymbolToken("String") -> String
                SymbolToken("Integer") -> Integer
                SymbolToken("Double") -> Double
                else -> null
            }
    }
}