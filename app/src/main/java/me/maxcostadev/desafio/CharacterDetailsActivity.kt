package me.maxcostadev.desafio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_character_details.details_name
import kotlinx.android.synthetic.main.activity_character_details.details_description
import kotlinx.android.synthetic.main.activity_character_details.link1
import kotlinx.android.synthetic.main.activity_character_details.link2
import kotlinx.android.synthetic.main.activity_character_details.link3
import kotlinx.android.synthetic.main.activity_character_details.comic_count
import kotlinx.android.synthetic.main.activity_character_details.event_count
import kotlinx.android.synthetic.main.activity_character_details.profile_img
import me.maxcostadev.desafio.model.CharacterModel
import android.R.id.toggle
import android.view.MenuItem


class CharacterDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        // back button on this activity toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getSerializableExtra("list") as CharacterModel // gets the serialized class
        loadData(data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId
        when (itemId) {
            android.R.id.home -> super.onBackPressed()
        }

        return true
    }

    private fun loadData(data: CharacterModel) {
        details_name.text = data.name
        details_description.text = data.description

        val links: HashMap<String, Button> = HashMap()
        links["detail"] = link1
        links["wiki"] = link2
        links["comiclink"] = link3

        for ((key, value) in data.links) { // will set each button a text, a link and only if its available it will set its visibility
            links[key]!!.visibility = View.VISIBLE
            links[key]!!.text = key
            links[key]!!.setOnClickListener {
                val uri = Uri.parse(value)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        comic_count.text = data.comics
        event_count.text = data.events

        Glide.with(this)
                .load(data.thumbnail)
                .into(profile_img)
    }
}
