package com.example.twitchtopgames

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.twitchtopgames.api.TwitchServices
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.Stats
import com.squareup.picasso.Picasso

private const val TAG = "GamesTopFragment"
private const val TOKEN_TAG = "token_tag"

class GameTopFragment : Fragment(){

    private lateinit var clientId : String
    private lateinit var clientSecret : String
    var accessesToken : String = String()

    var offlineMod : Boolean = true

    private lateinit var accessesLiveData : LiveData<String>

    private lateinit var gameRecyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var backgroundDownloader: BackgroundDownloader<GameHolder>

    private val viewModel: GameTopFragmentViewModel by lazy {
        ViewModelProvider(this).get(GameTopFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        clientId = getString(R.string.client_id)
        clientSecret = getString(R.string.client_secret)

        val services = TwitchServices

        val responseHandler = Handler(Looper.getMainLooper())
        backgroundDownloader = BackgroundDownloader(clientId, responseHandler) {
            gameHolder, streams ->
            gameHolder.setStreams(streams)
        }
        lifecycle.addObserver(backgroundDownloader.fragmentLifecycleObserver)

        if(savedInstanceState!=null){
            accessesToken = savedInstanceState.getString(TOKEN_TAG)!!
        }

        if (accessesToken.isEmpty()){
            accessesLiveData = services.accessesService.getAccessesToken(clientId, clientSecret)
            accessesLiveData.observe(
                this,
                {   response ->
                    accessesToken = response
                    Log.d(TAG, accessesToken)
                    viewModel.gamesStatsLiveData.removeObservers(this)
                    viewModel.gamesIdsLiveData.removeObservers(this)
                    clearDataBase()
                    viewModel.games.clear()
                    viewModel.stats.clear()
                    gameAdapter.notifyDataSetChanged()
                    loadGamesPage(accessesToken)
                }
            )
            loadFromDataBase()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.twitch_top_games_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.send_review -> sendReview()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(
            backgroundDownloader.viewLifecycleObserver
        )

        val view = inflater.inflate(R.layout.fragment_game_top, container, false)

        gameRecyclerView = view.findViewById(R.id.game_recycler_view)
        gameRecyclerView.layoutManager = GridLayoutManager(context, 1)
        gameAdapter = GameAdapter(viewModel.games)
        gameRecyclerView.adapter = gameAdapter

        gameRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)){
                    loadGamesPage(accessesToken, viewModel.lastCursor)
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOKEN_TAG, accessesToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            backgroundDownloader.fragmentLifecycleObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            backgroundDownloader.fragmentLifecycleObserver
        )
    }



    fun loadGamesPage(token: String, cursor: String = String()) {
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

    fun clearDataBase(){
        val repository = GameRepository.get()
        viewModel.stats.forEach{
            stat->
            repository.deleteStat(stat)
        }
        viewModel.games.forEach{
            game->
            repository.deleteGame(game)
        }
    }

    fun loadFromDataBase(){
        viewModel.gamesIdsLiveData.observe(
            this,
            { list->
                if (viewModel.games.isEmpty()){
                    viewModel.games.addAll(list)
                    Log.d(TAG, list.size.toString())
                    gameAdapter.notifyDataSetChanged()
                }
            }
        )
        viewModel.gamesStatsLiveData.observe(
            this,
            { list->
                if (viewModel.stats.isEmpty()){
                    viewModel.stats.addAll(list)
                    gameAdapter.notifyDataSetChanged()
                }
            }
        )
    }

    fun sendReview(){
        val recipient = Array(1) {"avemortis4@gmail.com"}
        val email = Intent(Intent.ACTION_SEND, Uri.parse("mailto: "))
        email.type= ("plain/text")
        email.putExtra(Intent.EXTRA_EMAIL, recipient)
        email.putExtra(Intent.EXTRA_SUBJECT, "App review")

        startActivity(Intent.createChooser(email, "Choose an Email client:"))
    }

    inner class GameHolder(item: View) : RecyclerView.ViewHolder(item){
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
        fun setStreams(stat : Stats){
            val pos = gameRecyclerView.getChildAdapterPosition(itemView)
            if (pos != -1) {
                stat.pos = pos
                if (viewModel.stats[pos].channels == 0){
                    val repository = GameRepository.get()
                    repository.saveStats(stat)
                }

                viewModel.stats[pos] = stat
                gameAdapter.notifyItemChanged(pos)
            }
        }
    }

    private inner class GameAdapter (val gamesIds: List<GameId>) : RecyclerView.Adapter<GameHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
            return GameHolder(itemView)
        }

        override fun onBindViewHolder(holder: GameHolder, position: Int) {
            gamesIds[position].setRes(256,342)

            holder.gameName?.text = gamesIds[position].name

            if (position < viewModel.stats.size){
                holder.gameViewers?.text = " " + viewModel.stats[position].viewers.toString()
                holder.gameStreams?.text = " " + viewModel.stats[position].channels.toString()
                Picasso.get().load(gamesIds[position].posterUrl).into(holder.gamePoster)


                if (viewModel.stats[position].channels==0){
                    backgroundDownloader.queueDownload(holder, viewModel.games[position].name)
                }
            }


        }

        override fun getItemCount(): Int = gamesIds.size

    }

    companion object {
        fun newInstance() = GameTopFragment()
    }

}