package cn.edu.sicnu.cs.stu.yuxingcai.a444.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.yuxingcai.a444.R
import cn.edu.sicnu.cs.stu.yuxingcai.a444.model.Card
import com.google.android.material.card.MaterialCardView

/**
 * RecyclerView adapter that renders the cards of a [CardMatchingGame].
 *
 * A card is shown face-down until it is chosen (flipped up) or matched, at
 * which point its face (rank + suit) is revealed.
 */
class CardAdapter(
    private val redSuits: Set<String>,
    private val backSymbol: String,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var cards: List<Card> = emptyList()

    fun submitList(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView: MaterialCardView = itemView as MaterialCardView
        private val faceView: TextView = itemView.findViewById(R.id.tv_card_face)

        fun bind(card: Card) {
            val context = itemView.context
            when {
                card.isMatched -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_matched))
                    cardView.strokeColor = ContextCompat.getColor(context, R.color.card_matched_border)
                    cardView.strokeWidth = 2
                    faceView.text = card.faceText
                    faceView.setTextColor(ContextCompat.getColor(context, R.color.card_matched_border))
                }
                card.isChosen -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_face))
                    cardView.strokeColor = ContextCompat.getColor(context, R.color.card_border)
                    cardView.strokeWidth = 1
                    faceView.text = card.faceText
                    faceView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            if (card.suit in redSuits) R.color.suit_red else R.color.suit_black
                        )
                    )
                }
                else -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_back))
                    cardView.strokeColor = ContextCompat.getColor(context, R.color.card_back)
                    cardView.strokeWidth = 0
                    faceView.text = backSymbol
                    faceView.setTextColor(ContextCompat.getColor(context, R.color.card_back_text))
                }
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCardClick(position)
                }
            }
        }
    }
}
