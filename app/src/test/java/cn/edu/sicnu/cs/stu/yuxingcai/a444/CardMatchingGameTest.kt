package cn.edu.sicnu.cs.stu.yuxingcai.a444

import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.Card
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {

    private val suits = listOf("♥", "♦", "♠", "♣")
    private val ranks = listOf("A", "K", "Q", "J")

    @Test
    fun deckSizeIsSuitsTimesRanks() {
        val game = CardMatchingGame(suits, ranks)
        assertEquals(16, game.cardCount)
        assertEquals(0, game.score)
    }

    @Test
    fun cardMatchScoresRankHigherThanSuit() {
        val aceHearts = Card("♥", "A")
        val aceDiamonds = Card("♦", "A")
        val kingHearts = Card("♥", "K")
        val kingSpades = Card("♠", "K")
        assertEquals(4, aceHearts.match(aceDiamonds)) // same rank
        assertEquals(1, aceHearts.match(kingHearts))  // same suit
        assertEquals(0, aceHearts.match(kingSpades))  // neither
    }

    @Test
    fun firstChooseFlipsCardUpAndChargesCost() {
        val game = CardMatchingGame(suits, ranks)
        val turn = game.chooseCardAtIndex(0)
        assertEquals(CardMatchingGame.Outcome.CHOSEN, turn.outcome)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertEquals(-CardMatchingGame.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun tappingChosenCardUnchoosesIt() {
        val game = CardMatchingGame(suits, ranks)
        game.chooseCardAtIndex(0)
        val turn = game.chooseCardAtIndex(0)
        assertEquals(CardMatchingGame.Outcome.UNCHOSEN, turn.outcome)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertEquals(-CardMatchingGame.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun matchingByRankMatchesBothCards() {
        val game = CardMatchingGame(listOf("♥", "♦"), listOf("A", "K"))
        val first = indexOfCard(game, "A", "♥")
        val second = indexOfCard(game, "A", "♦")
        game.chooseCardAtIndex(first)
        val turn = game.chooseCardAtIndex(second)
        assertEquals(CardMatchingGame.Outcome.MATCHED, turn.outcome)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
        // -1 (choose) + 4*4 (rank match) - 1 (cost) = 14
        assertEquals(14, game.score)
    }

    @Test
    fun matchingBySuitScoresLessThanRank() {
        val game = CardMatchingGame(listOf("♥", "♦"), listOf("A", "K"))
        val first = indexOfCard(game, "A", "♥")
        val second = indexOfCard(game, "K", "♥")
        game.chooseCardAtIndex(first)
        val turn = game.chooseCardAtIndex(second)
        assertEquals(CardMatchingGame.Outcome.MATCHED, turn.outcome)
        // -1 (choose) + 1*4 (suit match) - 1 (cost) = 2
        assertEquals(2, game.score)
    }

    @Test
    fun mismatchFlipsPreviousCardBackDown() {
        val game = CardMatchingGame(listOf("♥", "♦"), listOf("A", "K"))
        val first = indexOfCard(game, "A", "♥")
        val second = indexOfCard(game, "K", "♦")
        game.chooseCardAtIndex(first)
        val turn = game.chooseCardAtIndex(second)
        assertEquals(CardMatchingGame.Outcome.MISMATCHED, turn.outcome)
        assertEquals(first, turn.flippedBack)
        assertFalse(game.cardAtIndex(first).isChosen)
        assertTrue(game.cardAtIndex(second).isChosen)
        // -1 (choose) - 2 (penalty) - 1 (cost) = -4
        assertEquals(-4, game.score)
    }

    @Test
    fun completingAllMatchesWinsGame() {
        val game = CardMatchingGame(suits, ranks)
        for (rank in ranks) {
            val a = indexOfCard(game, rank, "♥", onlyUnmatched = true)
            val b = indexOfCard(game, rank, "♦", onlyUnmatched = true)
            game.chooseCardAtIndex(a)
            game.chooseCardAtIndex(b)

            val c = indexOfCard(game, rank, "♠", onlyUnmatched = true)
            val d = indexOfCard(game, rank, "♣", onlyUnmatched = true)
            game.chooseCardAtIndex(c)
            game.chooseCardAtIndex(d)
        }
        assertTrue(game.isGameOver)
        assertTrue(game.cardsView.all { it.isMatched })
        // 8 rank-matches, each netting 16 - 1 - 1 = 14 points.
        assertEquals(8 * 14, game.score)
    }

    @Test
    fun resetRestoresInitialState() {
        val game = CardMatchingGame(suits, ranks)
        game.chooseCardAtIndex(0)
        game.reset()
        assertEquals(0, game.score)
        assertFalse(game.isGameOver)
        assertEquals(16, game.cardCount)
        assertTrue(game.cardsView.none { it.isChosen || it.isMatched })
    }

    private fun indexOfCard(
        game: CardMatchingGame,
        rank: String,
        suit: String,
        onlyUnmatched: Boolean = false
    ): Int = game.cardsView.indexOfFirst {
        it.rank == rank && it.suit == suit && (!onlyUnmatched || !it.isMatched)
    }
}
