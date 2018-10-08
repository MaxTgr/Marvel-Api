package me.maxcostadev.desafio

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item.view.list_card
import kotlinx.android.synthetic.main.list_item.view.char_desc
import kotlinx.android.synthetic.main.list_item.view.char_name
import kotlinx.android.synthetic.main.list_item.view.char_img
import me.maxcostadev.desafio.model.CharacterModel

class Adapter(private val characterList: ArrayList<CharacterModel?>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return characterList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: Any?
        if (viewType == 0) { // setting for making the last item of the view a loading bar
            view = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
        } else {
            view = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_progress, parent, false))
        }

        return view
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val element = characterList[position] // full object of current character from api

        if (element != null && holder.charImg != null) {
            // load into view
            holder.charName?.text = element.name
            holder.charDesc?.text = element.description
            Glide.with(context)
                    .load(element.thumbnail)
                    .into(holder.charImg)

            // this onClick is set to the card and it will open the CharacterDetailsActivity with all the info needed
            holder.card.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val openDetails = Intent(context, CharacterDetailsActivity::class.java)
                    openDetails.putExtra("list", element) // sending the entire object using serializable
                    context.startActivity(openDetails)
                }

            })
        }
    }

    fun updateData(data: ArrayList<CharacterModel?>) {
        characterList.addAll(data)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val card = view.list_card
    val charImg = view.char_img
    val charName = view.char_name
    val charDesc = view.char_desc
}
