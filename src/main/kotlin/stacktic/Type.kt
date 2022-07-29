package stacktic

sealed interface Type {
    object String : Type
    object Integer : Type
    object Double : Type
}