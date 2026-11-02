package cn.edu.sicnu.cs.stu.yuxingcai.a444.model

/**
 * Model of the card-matching game.
 *
 * The board is built from every combination of [ranks] x [suits] and shuffled.
 * A player flips (chooses) cards two at a time: if the two cards share a rank
 * or a suit they are matched and score points, otherwise the earlier card is
 * turned back down and the new card stays selected.
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
        val flippedBack: Int? = null,
        val matchedPair: Pair<Int, Int>? = null
    )

    companion object {
        const val MATCH_BONUS = 4
        const val MISMATCH_PENALTY = 2
        const val COST_TO_CHOOSE = 1
    }

    var score = 0
        private set

    var isGameOver = false
        private set

    private var cards: MutableList<Card> = buildDeck(suits, ranks)

    /** Read-only snapshot of the deck, for the view to render. */
    val cardsView: List<Card> get() = cards

    val cardCount: Int get() = cards.size

    val matchedPairCount: Int get() = cards.count { it.isMatched } / 2

    val remainingCount: Int get() = cards.count { !it.isMatched }

    fun cardAtIndex(index: Int): Card = cards[index]

    /**
     * Chooses (flips) the card at [index] and resolves it against any card
     * that is already chosen.
     */
    fun chooseCardAtIndex(index: Int): Turn {
        if (isGameOver || index !in cards.indices) return Turn(Outcome.INVALID)
        val card = cards[index]
        if (card.isMatched) return Turn(Outcome.INVALID)

        // Tapping an already-chosen card flips it back down.
        if (card.isChosen) {
            card.isChosen = false
            return Turn(Outcome.UNCHOSEN)
        }

        var flippedBack: Int? = null
        var matchedPair: Pair<Int, Int>? = null
        for (i in cards.indices) {
            val other = cards[i]
            if (other.isChosen && !other.isMatched) {
                val matchScore = card.match(other)
                if (matchScore > 0) {
                    score += matchScore * MATCH_BONUS
                    other.isMatched = true
                    card.isMatched = true
                    matchedPair = i to index
                    if (cards.all { it.isMatched }) isGameOver = true
                } else {
                    score -= MISMATCH_PENALTY
                    other.isChosen = false
                    flippedBack = i
                }
                break
            }
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true

        val outcome = when {
            isGameOver -> Outcome.GAME_WON
            matchedPair != null -> Outcome.MATCHED
            flippedBack != null -> Outcome.MISMATCHED
            else -> Outcome.CHOSEN
        }
        return Turn(outcome, flippedBack, matchedPair)
    }

    /** Builds and shuffles a fresh deck and resets the score. */
    fun reset() {
        cards = buildDeck(suits, ranks)
        score = 0
        isGameOver = false
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
