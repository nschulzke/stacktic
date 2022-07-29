package stacktic

sealed interface Type {
    object String : Type {
        override fun toString() = "String"
    }
    object Integer : Type {
        override fun toString() = "Integer"
    }
    object Double : Type {
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