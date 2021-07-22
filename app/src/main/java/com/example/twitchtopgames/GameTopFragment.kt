package com.example.twitchtopgames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitchtopgames.api.TwitchServices
import com.example.twitchtopgames.api.games.model.GameId
import com.squareup.picasso.Picasso

private const val TAG = "GamesTopFragment"

private const val TOKEN_TAG = "token_tag"

lateinit var gamesList : String

class GameTopFragment : Fragment(){

    private lateinit var clientId : String
    private lateinit var clientSecret : String

    private lateinit var accessesLiveData : LiveData<String>
    private var accessesToken : String = String()

    private lateinit var gameRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clientId = getString(R.string.client_id)
        clientSecret = getString(R.string.client_secret)

        val services = TwitchServices

        if(savedInstanceState!=null){
            accessesToken = savedInstanceState.getString(TOKEN_TAG)!!
            getGames(accessesToken)
        }

        if (accessesToken.isEmpty()){
            accessesLiveData = services.accessesService.getAccessesToken(clientId, clientSecret)
            accessesLiveData.observe(
                this,
                Observer {
                        response ->
                        accessesToken = response
                        Log.d(TAG, accessesToken)
                        getGames(accessesToken)
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_top, container, false)

        gameRecyclerView = view.findViewById(R.id.game_recycler_view)
        gameRecyclerView.layoutManager = GridLayoutManager(context, 1)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOKEN_TAG, accessesToken)
    }

    private fun getGames(token: String) {
        val services = TwitchServices.gamesService.getGames(clientId,token)
        services.observe(
            this,
            Observer { gameItems ->
                gameRecyclerView.adapter = GameAdapter(gameItems)
            }
        )
    }

    private class GameHolder(item: View) : RecyclerView.ViewHolder(item){
        var gameName: TextView? = null
        var gamePoster: ImageView? = null

        init {
            gameName = item.findViewById(R.id.game_item_name)
            gamePoster = item.findViewById(R.id.game_item_poster)
        }
    }

    private class GameAdapter(private val gamesIds: List<GameId>) : RecyclerView.Adapter<GameHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
            return GameHolder(itemView)
        }

        override fun onBindViewHolder(holder: GameHolder, position: Int) {
            gamesIds[position].setRes(256,342)
            holder.gameName?.text = gamesIds[position].name
            Picasso.get().load(gamesIds[position].posterUrl).into(holder.gamePoster)
        }

        override fun getItemCount(): Int = gamesIds.size

    }

    companion object {
        fun newInstance() = GameTopFragment()
    }

}