package cn.edu.sicnu.cs.stu.yuxingcai.a444.model

/**
 * A single playing card.
 *
 * A card is [isChosen] while it is face-up on the board, and [isMatched] once
 * it has been successfully paired with another card.
 */
class Card(
    val suit: String,
    val rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {
    /** Face text shown on the card, e.g. "A♥". */
    val faceText: String
        get() = rank + suit

    /**
     * Scores a match against [other]: a rank match is worth more than a suit
     * match, and zero is returned when neither matches.
     */
    fun match(other: Card): Int = when {
        rank == other.rank -> SCORE_RANK_MATCH
        suit == other.suit -> SCORE_SUIT_MATCH
        else -> 0
    }

    companion object {
        const val SCORE_RANK_MATCH = 4
        const val SCORE_SUIT_MATCH = 1
    }
}
