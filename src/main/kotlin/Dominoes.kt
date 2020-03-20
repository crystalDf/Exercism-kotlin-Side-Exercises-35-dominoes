class ChainNotFoundException(msg: String = "Chain not Found") : RuntimeException(msg)

data class Domino(val left: Int, val right: Int) {
    fun rotate() = Domino(right, left)
}

object Dominoes {

    fun formChain(inputDominoes: List<Domino>): List<Domino> {

        var remainingDominoes = inputDominoes.toMutableList()
        val loopList = mutableListOf<List<Domino>>()

        while (remainingDominoes.isNotEmpty()) {
            val pair = getLoop(remainingDominoes)
            loopList.add(pair.first)
            remainingDominoes = pair.second
        }

        return mergeLoops(loopList)
    }

    fun formChain(vararg inputDominoes: Domino): List<Domino> {
        return formChain(inputDominoes.toList())
    }

    private fun getLoop(remainingDominoes: MutableList<Domino>): Pair<MutableList<Domino>, MutableList<Domino>> {

        val loop = mutableListOf<Domino>()

        val firstDomino = remainingDominoes.removeAt(0)
        loop.add(firstDomino)

        val first = firstDomino.left
        var right = firstDomino.right

        while (right != first) {

            val nextIndex = getNextDomino(right, remainingDominoes)

            if (nextIndex == -1) throw ChainNotFoundException()

            val nextDomino = remainingDominoes.removeAt(nextIndex)

            right = if (nextDomino.left == right) {
                loop.add(nextDomino)
                nextDomino.right
            } else {
                loop.add(nextDomino.rotate())
                nextDomino.left
            }
        }

        return Pair(loop, remainingDominoes)
    }

    private fun getNextDomino(half: Int, remainingDominoes: List<Domino>, rotate: Boolean = true): Int {
        return if (rotate) remainingDominoes.indexOfFirst { it.left == half || it.right == half }
        else remainingDominoes.indexOfFirst { it.left == half}
    }

    private fun mergeLoops(loopList: MutableList<List<Domino>>): List<Domino> {

        if (loopList.isEmpty()) return emptyList()

        val remainingLoops = loopList.toMutableList()
        var chain = remainingLoops.removeAt(0)

        var numberSet = getNumberSetInLoop(chain)

        while (remainingLoops.isNotEmpty()) {

            val nextIndex = getNextLoop(numberSet, remainingLoops)

            if (nextIndex == -1) throw ChainNotFoundException()

            val nextLoop = remainingLoops.removeAt(nextIndex)

            chain = mergeTwoLoops(chain, nextLoop)
            numberSet = getNumberSetInLoop(chain)
        }

        return chain
    }

    private fun mergeTwoLoops(firstLoop: List<Domino>, secondLoop: List<Domino>): List<Domino> {

        val firstNumberSet = getNumberSetInLoop(firstLoop)
        val secondNumberSet = getNumberSetInLoop(secondLoop)

        val right = firstNumberSet.first { it in secondNumberSet }

        val nextIndexOfFirstLoop = getNextDomino(right, firstLoop, false)
        val nextIndexOfSecondLoop = getNextDomino(right, secondLoop, false)

        return firstLoop.subList(0, nextIndexOfFirstLoop) +
                secondLoop.subList(nextIndexOfSecondLoop, secondLoop.size) +
                secondLoop.subList(0, nextIndexOfSecondLoop) +
                firstLoop.subList(nextIndexOfFirstLoop, firstLoop.size)
    }

    private fun getNumberSetInLoop(loop: List<Domino>): Set<Int> {
        return loop.flatMap { setOf(it.left, it.right) }.toSet()
    }

    private fun getNextLoop(numberSet: Set<Int>, loopList: MutableList<List<Domino>>): Int {
        return loopList.indexOfFirst { getNumberSetInLoop(it).intersect(numberSet).isNotEmpty()  }
    }
}
