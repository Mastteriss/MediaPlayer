package com.example.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val songList = mutableListOf(R.raw.cipelov, R.raw.life, R.raw.pascal, R.raw.silent)
    private var currentSongIndex = 0

    private val handler = Handler()
    private var currentVolume = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMediaPlayer()


        binding.volumeSeekBar.max = 100
        binding.volumeSeekBar.progress = (currentVolume * 100).toInt()

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentVolume = progress / 100f
                setVolume(currentVolume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupMediaPlayer() {
        binding.playFAB.setOnClickListener {
            if (mediaPlayer == null) {
                playSong(songList[currentSongIndex])
            }
            mediaPlayer?.start()
        }

        binding.pauseFAB.setOnClickListener {
            mediaPlayer?.pause()
        }

        binding.stopFAB.setOnClickListener {
            stopMediaPlayer()
        }

        binding.skipLeftFAB.setOnClickListener {
            skipToPrevious()
        }

        binding.skipRightFAB.setOnClickListener {
            skipToNext()
        }

        binding.seekbarSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playSong(songResId: Int) {
        stopMediaPlayer()
        mediaPlayer = MediaPlayer.create(this, songResId).apply {
            setOnCompletionListener {
                skipToNext()
            }
            start()
            setVolume(currentVolume)
        }
        initializeSeekbar()
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        binding.seekbarSB.progress = 0
        handler.removeCallbacksAndMessages(null)
    }

    private fun initializeSeekbar() {
        mediaPlayer?.let {
            binding.seekbarSB.max = it.duration
            handler.postDelayed(object : Runnable {
                override fun run() {
                    mediaPlayer?.let { player ->
                        binding.seekbarSB.progress = player.currentPosition
                        if (player.isPlaying) {
                            handler.postDelayed(this, 1000)
                        }
                    }
                }
            }, 0)
        }
    }

    private fun skipToPrevious() {
        if (currentSongIndex > 0) {
            currentSongIndex--
            playSong(songList[currentSongIndex])
        }
    }

    private fun skipToNext() {
        if (currentSongIndex < songList.size - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        playSong(songList[currentSongIndex])
    }

    private fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMediaPlayer()
    }
}