import java.util.concurrent.atomic.AtomicInteger

fun solveDay4(input: List<String>): Pair<Int, Int> {
    val rows = input.size
    val cols = input[0].length
    val xmasWordCount = countXmasWord(input, rows, cols)
    val xmasCount = countXmas(input, rows, cols)
    return Pair(xmasWordCount, xmasCount)
}

private const val word = "XMAS"

private val directions8 = listOf(
    Vec2i(-1, -1),
    Vec2i( 0, -1),
    Vec2i( 1, -1),
    Vec2i(-1,  0),
    Vec2i( 1,  0),
    Vec2i(-1,  1),
    Vec2i( 0,  1),
    Vec2i( 1,  1),
)

private val directions4d = listOf(
    Vec2i(-1, -1),
    Vec2i( 1, -1),
    Vec2i( 1,  1),
    Vec2i(-1,  1),
)

private fun countXmasWord(input: List<String>, rows: Int, cols: Int): Int {
    val result = AtomicInteger(0)
    searchKeywords(result, input, rows, cols)
    return result.get()
}

private fun searchKeywords(counter: AtomicInteger, input: List<String>, rows: Int, cols: Int) {
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (input[r][c] == word[0]) {
                directions8.forEach {
                    if (getWordInDirection(input, rows, cols, r, c, it)) {
                        counter.incrementAndGet()
                    }
                }
            }
        }
    }
}

private fun getWordInDirection(input: List<String>, rows: Int, cols: Int, r: Int, c: Int, dir: Vec2i): Boolean {
    for (charIndex in 1 until word.length) {
        val nr = r + charIndex * dir.y
        val nc = c + charIndex * dir.x
        if (nr !in 0 until rows || nc !in 0 until cols) {
            return false
        }
        if (input[nr][nc] != word[charIndex]) {
            return false
        }
    }
    return true
}

private fun countXmas(input: List<String>, rows: Int, cols: Int): Int {
    val result = AtomicInteger(0)
    searchX(result, input, rows, cols)
    return result.get()
}

private fun searchX(counter: AtomicInteger, input: List<String>, rows: Int, cols: Int) {
    for (r in 1 until rows - 1) {
        for (c in 1 until cols - 1) {
            if (input[r][c] == 'A') {
                val masCount = directions4d.count { input[r + it.y][c + it.x] == 'M' && input[r - it.y][c - it.x] == 'S' }
                if (masCount == 2) {
                    counter.incrementAndGet()
                }
            }
        }
    }
}
