data class Vec2i(val x: Int, val y: Int) {
    operator fun plus(delta: Vec2i): Vec2i {
        return Vec2i(x + delta.x, y + delta.y)
    }
}

fun List<Pair<Int, Int>>.toVec2i(): Vec2i {
    return Vec2i(this[0].first, this[0].second)
}
