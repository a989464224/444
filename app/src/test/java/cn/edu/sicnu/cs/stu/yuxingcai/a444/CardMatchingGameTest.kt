package cn.edu.sicnu.cs.stu.yuxingcai.a444

import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.Card
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {

    private val suits = listOf("♥", "♦", "♠", "♣")
    private val ranks = listOf("A", "K", "Q", "J", "10", "9")

    @Test
    fun deckSizeIsSuitsTimesRanks() {
        val game = CardMatchingGame(suits, ranks)
        assertEquals(24, game.cardCount)
        assertEquals(0, game.score)
    }

    @Test
    fun cardMatchScoresRankHigherThanSuit() {
        val aceHearts = Card("♥", "A")
        assertEquals(4, aceHearts.match(listOf(Card("♦", "A"), Card("♠", "A")))) // same rank
        assertEquals(1, aceHearts.match(listOf(Card("♥", "K"), Card("♥", "Q")))) // same suit
        assertEquals(0, aceHearts.match(listOf(Card("♦", "K"), Card("♠", "Q")))) // neither
    }

    @Test
    fun firstTwoChoosesFlipCardsUp() {
        val game = CardMatchingGame(suits, ranks)
        val first = game.chooseCardAtIndex(0)
        val second = game.chooseCardAtIndex(1)
        assertEquals(CardMatchingGame.Outcome.CHOSEN, first.outcome)
        assertEquals(CardMatchingGame.Outcome.CHOSEN, second.outcome)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
        assertEquals(-2 * CardMatchingGame.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun tappingChosenCardUnchoosesIt() {
        val game = CardMatchingGame(suits, ranks)
        game.chooseCardAtIndex(0)
        val turn = game.chooseCardAtIndex(0)
        assertEquals(CardMatchingGame.Outcome.UNCHOSEN, turn.outcome)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun matchingThreeOfSameRankMatchesAll() {
        val game = CardMatchingGame(listOf("♥", "♦", "♠"), listOf("A", "K"))
        val a1 = indexOfCard(game, "A", "♥")
        val a2 = indexOfCard(game, "A", "♦")
        val a3 = indexOfCard(game, "A", "♠")
        game.chooseCardAtIndex(a1)
        game.chooseCardAtIndex(a2)
        val turn = game.chooseCardAtIndex(a3)
        assertEquals(CardMatchingGame.Outcome.MATCHED, turn.outcome)
        assertTrue(game.cardAtIndex(a1).isMatched)
        assertTrue(game.cardAtIndex(a2).isMatched)
        assertTrue(game.cardAtIndex(a3).isMatched)
        // -1 -1 -1 (choose costs) + 4*4 (rank match) = 13
        assertEquals(13, game.score)
    }

    @Test
    fun matchingThreeOfSameSuitScoresLess() {
        val game = CardMatchingGame(listOf("♥", "♦", "♠", "♣"), listOf("A", "K", "Q"))
        val h1 = indexOfCard(game, "A", "♥")
        val h2 = indexOfCard(game, "K", "♥")
        val h3 = indexOfCard(game, "Q", "♥")
        game.chooseCardAtIndex(h1)
        game.chooseCardAtIndex(h2)
        val turn = game.chooseCardAtIndex(h3)
        assertEquals(CardMatchingGame.Outcome.MATCHED, turn.outcome)
        // -1 -1 -1 (choose costs) + 1*4 (suit match) = 1
        assertEquals(1, game.score)
    }

    @Test
    fun mismatchFlipsFirstTwoBackAndKeepsThird() {
        val game = CardMatchingGame(listOf("♥", "♦", "♠"), listOf("A", "K"))
        val a1 = indexOfCard(game, "A", "♥")
        val a2 = indexOfCard(game, "A", "♦")
        val k1 = indexOfCard(game, "K", "♠")
        game.chooseCardAtIndex(a1)
        game.chooseCardAtIndex(a2)
        val turn = game.chooseCardAtIndex(k1)
        assertEquals(CardMatchingGame.Outcome.MISMATCHED, turn.outcome)
        assertFalse(game.cardAtIndex(a1).isChosen)
        assertFalse(game.cardAtIndex(a2).isChosen)
        assertTrue(game.cardAtIndex(k1).isChosen)
        // -1 -1 -1 (choose costs) - 2 (penalty) = -5
        assertEquals(-5, game.score)
    }

    @Test
    fun completingAllGroupsWinsGame() {
        val game = CardMatchingGame(listOf("♥", "♦", "♠"), listOf("A", "K"))
        // match the three A's
        game.chooseCardAtIndex(indexOfCard(game, "A", "♥"))
        game.chooseCardAtIndex(indexOfCard(game, "A", "♦"))
        game.chooseCardAtIndex(indexOfCard(game, "A", "♠"))
        // match the three K's
        game.chooseCardAtIndex(indexOfCard(game, "K", "♥"))
        game.chooseCardAtIndex(indexOfCard(game, "K", "♦"))
        val turn = game.chooseCardAtIndex(indexOfCard(game, "K", "♠"))
        assertEquals(CardMatchingGame.Outcome.GAME_WON, turn.outcome)
        assertTrue(game.isGameOver)
        assertTrue(game.cardsView.all { it.isMatched })
        // 2 rank-matches, each netting 13.
        assertEquals(2 * 13, game.score)
    }

    @Test
    fun resetRestoresInitialState() {
        val game = CardMatchingGame(suits, ranks)
        game.chooseCardAtIndex(0)
        game.reset()
        assertEquals(0, game.score)
        assertFalse(game.isGameOver)
        assertEquals(24, game.cardCount)
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
