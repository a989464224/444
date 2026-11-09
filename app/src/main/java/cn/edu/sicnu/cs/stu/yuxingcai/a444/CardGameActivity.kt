package cn.edu.sicnu.cs.stu.yuxingcai.a444

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.yuxingcai.a444.view.CardAdapter

/**
 * The game's controller: it owns the [CardMatchingGame] model, feeds cards to a
 * [RecyclerView] via [CardAdapter], and reacts to taps to keep the model and
 * view in sync.
 *
 * The card count and column count come from integer resources, so the board
 * adapts automatically between portrait and landscape orientations.
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var game: CardMatchingGame
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var scoreValue: TextView
    private lateinit var matchesValue: TextView
    private lateinit var remainingValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)

        recycler = findViewById(R.id.recycler_cards)
        scoreValue = findViewById(R.id.tv_score_value)
        matchesValue = findViewById(R.id.tv_matches_value)
        remainingValue = findViewById(R.id.tv_remaining_value)

        val suits = resources.getStringArray(R.array.card_suits).toList()
        val allRanks = resources.getStringArray(R.array.card_ranks).toList()
        val cardCount = resources.getInteger(R.integer.game_card_count)
        val columns = resources.getInteger(R.integer.game_columns)

        // Build the deck from the first `cardCount / suits.size` ranks.
        val ranks = allRanks.take(cardCount / suits.size)
        game = CardMatchingGame(suits, ranks)

        adapter = CardAdapter(
            redSuits = suits.take(2).toSet(),
            backSymbol = getString(R.string.card_back_symbol)
        ) { index -> onCardTapped(index) }

        recycler.layoutManager = GridLayoutManager(this, columns)
        recycler.adapter = adapter
        adapter.submitList(game.cardsView)

        findViewById<Button>(R.id.btn_restart).setOnClickListener { restartGame() }

        updateStats()
    }

    private fun onCardTapped(index: Int) {
        val turn = game.chooseCardAtIndex(index)
        if (turn.outcome == CardMatchingGame.Outcome.INVALID) return
        adapter.submitList(game.cardsView)
        if (turn.outcome == CardMatchingGame.Outcome.GAME_WON) {
            onGameWon()
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
        adapter.submitList(game.cardsView)
        updateStats()
    }

    private fun updateStats() {
        scoreValue.text = game.score.toString()
        matchesValue.text = game.matchedGroupCount.toString()
        remainingValue.text = game.remainingCount.toString()
    }
}
