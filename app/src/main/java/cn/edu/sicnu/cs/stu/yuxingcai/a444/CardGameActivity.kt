package cn.edu.sicnu.cs.stu.yuxingcai.a444

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.yuxingcai.a444.view.GameBoardView

/**
 * The game's controller: it owns the [CardMatchingGame] model, reacts to taps
 * from the [GameBoardView], and keeps both model and view in sync.
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var game: CardMatchingGame
    private lateinit var board: GameBoardView
    private lateinit var scoreValue: TextView
    private lateinit var matchesValue: TextView
    private lateinit var remainingValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)

        board = findViewById(R.id.game_board)
        scoreValue = findViewById(R.id.tv_score_value)
        matchesValue = findViewById(R.id.tv_matches_value)
        remainingValue = findViewById(R.id.tv_remaining_value)

        val suits = resources.getStringArray(R.array.card_suits).toList()
        val ranks = resources.getStringArray(R.array.card_ranks).toList()
        game = CardMatchingGame(suits, ranks)

        board.redSuits = suits.take(2).toSet()
        board.cards = game.cardsView
        board.onCardTap = { index -> onCardTapped(index) }

        findViewById<Button>(R.id.btn_restart).setOnClickListener { restartGame() }

        updateStats()
    }

    private fun onCardTapped(index: Int) {
        val turn = game.chooseCardAtIndex(index)
        when (turn.outcome) {
            CardMatchingGame.Outcome.INVALID -> return
            CardMatchingGame.Outcome.CHOSEN -> board.flipTo(index, true)
            CardMatchingGame.Outcome.UNCHOSEN -> board.flipTo(index, false)
            CardMatchingGame.Outcome.MATCHED -> board.flipTo(index, true)
            CardMatchingGame.Outcome.MISMATCHED -> {
                board.flipTo(index, true)
                turn.flippedBack?.let { board.flipTo(it, false) }
            }
            CardMatchingGame.Outcome.GAME_WON -> {
                board.flipTo(index, true)
                onGameWon()
            }
        }
        updateStats()
    }

    private fun onGameWon() {
        val isNewRecord = game.score > PlayerStore.highestScore
        if (isNewRecord) {
            PlayerStore.highestScore = game.score
        }
        updateStats()
        showWinDialog(isNewRecord)
    }

    private fun showWinDialog(isNewRecord: Boolean) {
        val message = if (isNewRecord) {
            getString(R.string.win_message) + "\n" + getString(R.string.win_new_record)
        } else {
            getString(R.string.win_message)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.win_title)
            .setMessage(message)
            .setPositiveButton(R.string.btn_restart) { _, _ -> restartGame() }
            .setNegativeButton(R.string.btn_back) { _, _ -> finish() }
            .show()
    }

    private fun restartGame() {
        game.reset()
        board.cards = game.cardsView
        updateStats()
    }

    private fun updateStats() {
        scoreValue.text = game.score.toString()
        matchesValue.text = game.matchedPairCount.toString()
        remainingValue.text = game.remainingCount.toString()
    }
}
