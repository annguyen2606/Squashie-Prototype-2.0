package com.example.squashlandsscc2020

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.beust.klaxon.*
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.StringReader


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var rotation = AnimationUtils.loadAnimation(this,R.anim.rotate)
        rotation.interpolator = LinearInterpolator()
        var textViewPlayingSong = findViewById<TextView>(R.id.textViewPlayingSong) //get textview to display current song
        var textViewNextSong = findViewById<TextView>(R.id.textViewNextSong)
        var logo = findViewById<ImageView>(R.id.imageView) //get imageView contains logo

        socket.once("connect",{
            socket.emit("sync library and queue", "request to get library and queue")
        })

        socket.on("respond to sync with library and queue",{
            var tmp = it[0] as JSONArray
            JsonReader(StringReader(tmp[0].toString())).use {
                it.beginArray {
                    while (it.hasNext()){
                        val song = Klaxon().parse<Song>(it) as Song
                        queue.add(song)
                    }
                }
            }

            JsonReader(StringReader(tmp[1].toString())).use {
                it.beginArray {
                    while (it.hasNext()){
                        val song = Klaxon().parse<Song>(it) as Song
                        library.add(song)
                    }
                }
            }
            runOnUiThread {
                if (queue.size > 1)
                    textViewNextSong.text = queue.get(1).name
            }
        })
        //once socket fire "respond to sync" event from server side
        socket.once("respond to sync",{
            rotation.fillAfter = true
            logo.startAnimation(rotation)
        })

        runOnUiThread {
            socket.on("respond to sync", {
                currentSong = it[0].toString()
                if (queue.size > 0){
                    if (queue[0].name != currentSong)
                        queue.removeAt(0)
                }
            })

            socket.on("sync for repopulated queue",{
                var tmp = it[0] as JSONArray
                queue.removeAll(queue)
                JsonReader(StringReader(tmp.toString())).use {
                    it.beginArray {
                        while (it.hasNext()){
                            val song = Klaxon().parse<Song>(it) as Song
                            if(song.uri.contains("/Music%20Videos/"))
                                queue.add(song)
                        }
                    }
                }
                textViewNextSong.text = queue[1].name
            })
        }

        //WHENEVER socket fires "respond to sync" from server side
        socket.on("respond to sync",{
            runOnUiThread {
                textViewPlayingSong.text = currentSong
                var currentSongTmp = queue.find{
                    it.name == currentSong
                }
                if (queue.indexOf(currentSongTmp) < queue.size-1)
                    textViewNextSong.text = queue[queue.indexOf(currentSongTmp) + 1].name
            }
        })

        buttonChooseASong.setOnClickListener {
            var intent = Intent(this, ChooseASongActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        var alertDialogNotConnected: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogNotConnected.setCancelable(true)
        alertDialogNotConnected.setTitle("The app is not connected")
        alertDialogNotConnected.setMessage("Try to connect")
        alertDialogNotConnected.setNeutralButton("Ok",DialogInterface.OnClickListener { dialogInterface, i ->
            socket.connect()
            dialogInterface.cancel()
        })

        if(!socket.connected()){
            alertDialogNotConnected.show()
        }

    }
    companion object{
        val socket: Socket = IO.socket("http://192.168.0.3:5000/")
        var currentSong: String = ""
        var queue =  arrayListOf<Song>()
        var library = arrayListOf<Song>()
    }

}
