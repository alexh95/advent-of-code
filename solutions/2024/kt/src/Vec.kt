data class Vec2i(val x: Int, val y: Int) {
    operator fun plus(other: Vec2i): Vec2i {
        return Vec2i(x + other.x, y + other.y)
    }

    operator fun minus(other: Vec2i): Vec2i {
        return Vec2i(x - other.x, y - other.y)
    }

    operator fun times(scalar: Int): Vec2i {
        return Vec2i(scalar * x, scalar * y)
    }
}

fun List<Pair<Int, Int>>.pairToVec2i(): Vec2i {
    return Vec2i(this[0].first, this[0].second)
}

fun List<String>.parseToVec2i(): Vec2i {
    return Vec2i(this[0].toInt(), this[1].toInt())
}
