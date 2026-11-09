package cn.edu.sicnu.cs.stu.yuxingcai.a444.model

/**
 * A single playing card.
 *
 * A card is [isChosen] while it is face-up on the board, and [isMatched] once
 * it has been successfully matched as part of a three-card group.
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
     * Scores this card together with [others] as a three-card group:
     * all cards sharing the same rank scores [SCORE_RANK_MATCH], all sharing
     * the same suit scores [SCORE_SUIT_MATCH], otherwise zero.
     */
    fun match(others: List<Card>): Int {
        val group = listOf(this) + others
        return when {
            group.all { it.rank == rank } -> SCORE_RANK_MATCH
            group.all { it.suit == suit } -> SCORE_SUIT_MATCH
            else -> 0
        }
    }

    companion object {
        const val SCORE_RANK_MATCH = 4
        const val SCORE_SUIT_MATCH = 1
    }
}
