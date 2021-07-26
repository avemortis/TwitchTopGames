package com.example.twitchtopgames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitchtopgames.api.TwitchServices
import com.example.twitchtopgames.api.games.model.GameId
import com.squareup.picasso.Picasso

private const val TAG = "GamesTopFragment"

private const val TOKEN_TAG = "token_tag"
private const val STATE_TAG = "state_tag"

class GameTopFragment : Fragment(){

    private lateinit var clientId : String
    private lateinit var clientSecret : String
    var accessesToken : String = String()

    private lateinit var accessesLiveData : LiveData<String>

    private lateinit var gameRecyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter

    val lab = GamesStatLab

    private val viewModel: GameTopFragmentViewModel by lazy {
        ViewModelProvider(this).get(GameTopFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clientId = getString(R.string.client_id)
        clientSecret = getString(R.string.client_secret)

        val services = TwitchServices

        if(savedInstanceState!=null){
            accessesToken = savedInstanceState.getString(TOKEN_TAG)!!
        }

        if (accessesToken.isEmpty()){
            accessesLiveData = services.accessesService.getAccessesToken(clientId, clientSecret)
            accessesLiveData.observe(
                this,
                {
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
        gameAdapter = GameAdapter(viewModel.games)
        gameRecyclerView.adapter = gameAdapter

        gameRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)){
                    getGames(accessesToken, viewModel.lastCursor)
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOKEN_TAG, accessesToken)
        outState.putParcelable(STATE_TAG, gameRecyclerView.layoutManager?.onSaveInstanceState())
    }

    fun getGames(token: String, cursor: String = String()) {
        val services = TwitchServices.gamesService.getGames(clientId, token, cursor)
        services.observe(
            this,
            { gameItems ->
                viewModel.lastCursor = gameItems.cursor.cursor
                viewModel.addNewPage(gameItems.mGameIds)
                gameAdapter.notifyDataSetChanged()
            }
        )
    }

    private class GameHolder(item: View) : RecyclerView.ViewHolder(item){
        var gameName: TextView? = null
        var gamePoster: ImageView? = null
        var gameViewers: TextView? = null
        var gameStreams: TextView? = null

        init {
            gameName = item.findViewById(R.id.game_item_name)
            gamePoster = item.findViewById(R.id.game_item_poster)
            gameViewers = item.findViewById(R.id.game_item_viewers)
            gameStreams = item.findViewById(R.id.game_item_channels)
        }
    }

    private class GameAdapter (val gamesIds: List<GameId>) : RecyclerView.Adapter<GameHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
            return GameHolder(itemView)
        }

        override fun onBindViewHolder(holder: GameHolder, position: Int) {
            gamesIds[position].setRes(256,342)
            holder.gameName?.text = gamesIds[position].name
            holder.gameViewers?.text = gamesIds[position].viewers.toString()
            holder.gameStreams?.text = gamesIds[position].streams.toString()

            Picasso.get().load(gamesIds[position].posterUrl).into(holder.gamePoster)
        }

        override fun getItemCount(): Int = gamesIds.size
    }

    companion object {
        fun newInstance() = GameTopFragment()
    }

}