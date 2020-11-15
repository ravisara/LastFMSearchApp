package com.example.lastfmsearchapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/*
API key: 2f6d1c3a77d7364fe9c8b7d955a56e11
Shared secret: 6f2327ff8b4d7ca6813f250b4aa35984
Registered to: NowOneFineUser
Sample URL format: http://ws.audioscrobbler.com/2.0/?method=album.search&album=believe&api_key=YOUR_API_KEY&format=json
Artist search:
    https://ws.audioscrobbler.com/2.0/?method=artist.search&artist=cher&api_key=2f6d1c3a77d7364fe9c8b7d955a56e11&format=json
Album search:
    https://ws.audioscrobbler.com/2.0/?method=album.search&album=believe&api_key=2f6d1c3a77d7364fe9c8b7d955a56e11&format=json
Song(track) search:
    https://ws.audioscrobbler.com/2.0/?method=track.search&track=Believe&api_key=2f6d1c3a77d7364fe9c8b7d955a56e11&format=json
*/

const val API_KEY = "2f6d1c3a77d7364fe9c8b7d955a56e11"

class InitialSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _responseReceived = MutableLiveData<MutableList<MutableMap<String, String>>>()
    val responseReceived: LiveData<MutableList<MutableMap<String, String>>> = _responseReceived

    // TODO find a clever way of reporting errors.

    fun fetchSearchResults(keyWordsToSearchFor: String = "believe") { // TODO remove this default text

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(getApplication()) // TODO review

        val url =
            "https://ws.audioscrobbler.com/2.0/?method=artist.search&artist=${keyWordsToSearchFor}&api_key=${API_KEY}&format=json"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                println("Response: %s".format(response.toString()))
                //_responseReceived.value = response.toString()
                parseJasonObjectAndSetLiveDataForDisplaying(response)

            },
            { error ->
                // TODO: Handle error
                println("That didn't work!")
                //_responseReceived.value = "That didn't work!\r" + error.message
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)

    }

    fun parseJasonObjectAndSetLiveDataForDisplaying(jsonObj: JSONObject) {

        val artists =  jsonObj.getJSONArray("artistmatches")
        lateinit var allArtistsInfoToShow: MutableList<MutableMap<String, String>> // TODO REVIEW LATEINIT
        var infoOfAnArtistToShow: MutableMap<String, String>
        var artistItem: JSONObject
        for (i in 0 until artists.length()) { // until excludes the last value. As there is no iterator for JSONArray, using this approach.
            artistItem = artists.getJSONObject(i)
            infoOfAnArtistToShow = mutableMapOf("name" to artistItem.getString("name"), "listeners" to artistItem.getString("listeners"))
            allArtistsInfoToShow.add(infoOfAnArtistToShow)
        }

        _responseReceived.value = allArtistsInfoToShow // instead of postvalue as we are calling from the main thread.

    }

    // TODO handle errors
    /*fun parseJasonObjectAndSetLiveDataForDisplaying(jsonObj: JSONObject) {

        val albums =  jsonObj.getJSONArray("albummatches")
        lateinit var allAlbumsInfoToShow: MutableList<MutableMap<String, String>> // TODO REVIEW LATEINIT
        var infoOfGivenAlbumToShow: MutableMap<String, String>
        var albumItem: JSONObject
        for (i in 0 until albums.length()) { // until excludes the last value. As there is no iterator for JSONArray, using this approach.
            albumItem = albums.getJSONObject(i)
            infoOfGivenAlbumToShow = mutableMapOf("name" to albumItem.getString("name"), "artist" to albumItem.getString("artist"))
            allAlbumsInfoToShow.add(infoOfGivenAlbumToShow)
        }

    }*/

}