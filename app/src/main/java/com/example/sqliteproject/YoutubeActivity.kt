package com.example.sqliteproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sqliteproject.databinding.ActivityYoutubeBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YoutubeActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityYoutubeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val youtube= binding.youTubePlayerView

        youtube.initialize("AIzaSyBCq23wEG_Vqxgwpc3HfqPhRdtxRLOkaGY",object : YouTubePlayer.OnInitializedListener{


            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                if(p1==null){
                    return
                }
                if(p2){
                    p1.play()
                }
                else{
                    p1.cueVideo("UlMzzwl7OpA")
                    p1.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                }
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {

            }

        })
    }


}