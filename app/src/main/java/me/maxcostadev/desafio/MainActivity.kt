package me.maxcostadev.desafio

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.maxcostadev.desafio.helper.ApiCall
import me.maxcostadev.desafio.model.CharacterModel
import java.net.MalformedURLException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private var adapter: Adapter? = null
    private var pageIterator: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Normal character api loader (characters in list)
         */
        loadList(null) // calling the api the first time for loading the view

        main_list.addOnScrollListener(object : RecyclerView.OnScrollListener() { // listener for when the user reaches the end of the list
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                pageIterator++ // every time the view gets in the bottom it will increase the number of "current page"

                if (!recyclerView!!.canScrollVertically(1)) {
                    // calling the api with the initial set to false, so it can load the update data function
                    ApiCall("/v1/public/characters", false, true, pageIterator, ::apiCallback, ::errorCallback)
                }
            }
        })

    }

    // adding the reload button to the bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reload, menu)
        return true
    }

    //clears and loads the list
    fun loadList(item: MenuItem?){
        adapter = null
        main_list.adapter = null
        ApiCall("/v1/public/characters", true, true, 0, ::apiCallback, ::errorCallback)
    }

    private fun apiCallback(data:ArrayList<CharacterModel?>, firstCall:Boolean){
        runOnUiThread {
            if (firstCall) { // if is the first call it will create the list
                main_list.visibility = View.VISIBLE
                loading_holder.visibility = View.GONE
                main_list.layoutManager = LinearLayoutManager(applicationContext)

                adapter = Adapter(data, applicationContext)
                main_list.adapter = adapter
            } else { // updating the list
                adapter!!.updateData(data)
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun errorCallback(err: Exception){
        runOnUiThread {
            var toastMsg = "An error has occurred"
            if(err is UnknownHostException){
                toastMsg = "No connection, please try again later"
            }else if(err is MalformedURLException){
                toastMsg = "Failed to connect to marvel"
            }
            Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_SHORT).show()
            loading_holder.visibility = View.VISIBLE
        }
    }

}
