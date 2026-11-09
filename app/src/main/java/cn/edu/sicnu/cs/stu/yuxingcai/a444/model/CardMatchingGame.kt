package cn.edu.sicnu.cs.stu.yuxingcai.a444.model

/**
 * Model of the three-card matching game.
 *
 * The board is built from every combination of [ranks] x [suits] and shuffled.
 * A player flips cards three at a time: a group of three scores when all three
 * cards share a rank (highest score) or all three share a suit; otherwise the
 * two earlier cards flip back down and a penalty is applied.
 *
 * Pure Kotlin so it can be unit tested on the JVM.
 */
class CardMatchingGame(
    private val suits: List<String>,
    private val ranks: List<String>
) {

    enum class Outcome { CHOSEN, UNCHOSEN, MATCHED, MISMATCHED, GAME_WON, INVALID }

    /** Result of a single [chooseCardAtIndex] call. */
    data class Turn(
        val outcome: Outcome,
        val flippedUp: Int? = null,
        val flippedBack: List<Int> = emptyList(),
        val matched: List<Int> = emptyList()
    )

    companion object {
        const val GROUP_SIZE = 3
        const val MATCH_BONUS = 4
        const val MISMATCH_PENALTY = 2
        const val COST_TO_CHOOSE = 1
    }

    var score = 0
        private set

    var isGameOver = false
        private set

    private var cards: MutableList<Card> = buildDeck(suits, ranks)

    /** Indices of the cards currently chosen but not yet matched. */
    private val chosen = mutableListOf<Int>()

    /** Read-only snapshot of the deck, for the view to render. */
    val cardsView: List<Card> get() = cards

    val cardCount: Int get() = cards.size

    val matchedGroupCount: Int get() = cards.count { it.isMatched } / GROUP_SIZE

    val remainingCount: Int get() = cards.count { !it.isMatched }

    fun cardAtIndex(index: Int): Card = cards[index]

    /**
     * Chooses (flips) the card at [index]. Once [GROUP_SIZE] cards are chosen
     * they are evaluated: all sharing a rank or a suit scores a bonus and
     * matches the group; otherwise the earlier cards flip back down and a
     * penalty is applied, leaving the newest card chosen.
     */
    fun chooseCardAtIndex(index: Int): Turn {
        if (isGameOver || index !in cards.indices) return Turn(Outcome.INVALID)
        val card = cards[index]
        if (card.isMatched) return Turn(Outcome.INVALID)

        // Tapping an already-chosen card flips it back down.
        if (card.isChosen) {
            card.isChosen = false
            chosen.remove(index)
            return Turn(Outcome.UNCHOSEN, flippedBack = listOf(index))
        }

        card.isChosen = true
        chosen.add(index)

        if (chosen.size < GROUP_SIZE) {
            score -= COST_TO_CHOOSE
            return Turn(Outcome.CHOSEN, flippedUp = index)
        }

        // A full group is on the table: score it.
        score -= COST_TO_CHOOSE
        val group = chosen.map { cards[it] }
        val matchScore = group.first().match(group.drop(1))

        return if (matchScore > 0) {
            score += matchScore * MATCH_BONUS
            group.forEach { it.isMatched = true }
            val matched = chosen.toList()
            chosen.clear()
            if (cards.all { it.isMatched }) isGameOver = true
            Turn(if (isGameOver) Outcome.GAME_WON else Outcome.MATCHED, flippedUp = index, matched = matched)
        } else {
            score -= MISMATCH_PENALTY
            val flippedBack = chosen.dropLast(1)
            flippedBack.forEach { cards[it].isChosen = false }
            chosen.removeAll(flippedBack)
            Turn(Outcome.MISMATCHED, flippedUp = index, flippedBack = flippedBack)
        }
    }

    /** Builds and shuffles a fresh deck and resets the score. */
    fun reset() {
        cards = buildDeck(suits, ranks)
        score = 0
        isGameOver = false
        chosen.clear()
    }

    private fun buildDeck(suits: List<String>, ranks: List<String>): MutableList<Card> {
        val deck = mutableListOf<Card>()
        for (rank in ranks) {
            for (suit in suits) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
        return deck
    }
}
