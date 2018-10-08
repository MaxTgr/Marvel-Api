package me.maxcostadev.desafio.helper

import android.content.res.Resources
import me.maxcostadev.desafio.R
import me.maxcostadev.desafio.model.CharacterModel
import okhttp3.OkHttpClient
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Call
import okhttp3.Response
import okhttp3.Callback
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException
import java.security.MessageDigest

class ApiCall {

    private val apiKey = "" //TODO(Add key)
    private val secretApiKey = "" //TODO(Add secret key)
    private val baseUrl = "http://gateway.marvel.com"
    private val pageSize = 20
    private val client = OkHttpClient()

    private var subUrl: String
    private var isInitial: Boolean
    private var hasPages: Boolean
    private var pageNumber: Int

    private var callback: (data: ArrayList<CharacterModel?>, firstCall: Boolean) -> Unit
    private var errorCallback: (err:Exception) -> Unit

    constructor(subUrl: String, isInitial: Boolean, hasPages: Boolean, pageNumber: Int = 0, callback: (data: ArrayList<CharacterModel?>, firstCall: Boolean) -> Unit, errorCallback: (err:Exception) -> Unit) {
        this.subUrl = subUrl
        this.isInitial = isInitial
        this.hasPages = hasPages
        this.pageNumber = pageNumber

        this.callback = callback
        this.errorCallback = errorCallback

        makeUrl()
    }

    private fun makeUrl() {
        /*
        For building the url you will need:
        Your api keys + a unique id (i will use the current time)
        And you will need a MD5 of your (Ts + ApiKey + SecretApiKey)
        Ex : http://gateway.marvel.com/v1/public/characters?ts=<TIME>&apikey<KEY>&hash<MD5>
         */
        // Params
        val ts = System.currentTimeMillis()
        val beforeHash = ts.toString() + secretApiKey + apiKey // string that will be used for creating the hash

        val md = MessageDigest.getInstance("MD5")
        val digested = md.digest(beforeHash.toByteArray()) // making md5 hash bytes
        val hash = digested.joinToString("") {
            String.format("%02x", it) // md5 bytes to string
        }

        // Url
        val urlBuilder = HttpUrl.parse(baseUrl + subUrl)!!.newBuilder() // ...marvel.com/v1/public/<QUERY>
        urlBuilder.addQueryParameter("ts", ts.toString())
        urlBuilder.addQueryParameter("apikey", apiKey)
        urlBuilder.addQueryParameter("hash", hash)
        if (hasPages) { // the param offset will be ignored if the query does not have pages to iterate
            val offset = (pageNumber * pageSize)
            urlBuilder.addQueryParameter("offset", offset.toString()) // this will be the "skip" for showing more "pages" of the search
        }

        val url = urlBuilder.build().toString()
        makeCall(url)
    }

    private fun makeCall(url: String) {

        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { errorCallback(e) }
            override fun onResponse(call: Call, response: Response) {

                if(response.code() != 200){
                    errorCallback(MalformedURLException())
                }else{
                    val jsonObject = JSONObject(response.body()?.string()) // transforming Response string to JsonObject
                    val array = jsonObject.getJSONObject("data").getJSONArray("results")

                    //val returnObj = ArrayList<CharacterModel?>()

                    // data capture
                    val returning = dataProcess(array)

                    callback(returning, isInitial)
                }

            }
        })
    }

    private fun dataProcess(data: JSONArray): ArrayList<CharacterModel?>{

        val array = ArrayList<CharacterModel?>()

        for (i in 0..(data.length() - 1)) {
            val element = data.getJSONObject(i)

            val name = element.getString("name")
            var desc = element.getString("description")
            val comics = element.getJSONObject("comics").get("available").toString()
            val events = element.getJSONObject("events").get("available").toString()
            val details = element.getString("resourceURI")
            val linksArray = element.getJSONArray("urls") // marvel + wiki links
            val thumbnail = element.getJSONObject("thumbnail").getString("path") // www.example.com/xyz
            val thumbnailExt = element.getJSONObject("thumbnail").getString("extension") // .jpg

            val links = HashMap<String, String>()
            for (urlIndex in 0..(linksArray.length() - 1)) {
                val type = linksArray.getJSONObject(urlIndex).getString("type")
                val resUrl = linksArray.getJSONObject(urlIndex).getString("url")

                links[type] = resUrl
            }

            desc = if (desc == "") "No description available" else desc // having something to show when there's no description

            array.add(CharacterModel(name, desc, "$thumbnail.$thumbnailExt", details, comics, events, links))
        }

        return array
    }
}