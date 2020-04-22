package com.example.squashlandsscc2020

import android.content.DialogInterface
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import kotlinx.android.synthetic.main.activity_choose_a_song.*


class ChooseASongActivity : AppCompatActivity() {
    private var strQueue = ""
    private lateinit var adapter: CustomeRyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_a_song)
        var rotation = AnimationUtils.loadAnimation(this,R.anim.rotate)
        rotation.interpolator = LinearInterpolator()
        rotation.fillAfter = true
        var logo = imageViewLogoChooseSong
        logo.startAnimation(rotation)
        var libTmp: ArrayList<Song> = ArrayList(MainActivity.library)
        adapter = CustomeRyclerViewAdapter(libTmp,this,{song -> onSongClick(song)})
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var searchView = this.findViewById<SearchView>(R.id.searchView)
        textViewQueueDisplay.setSelected(true);
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }

        })
        MainActivity.queue.forEach {
            //TODO foreach loop on static "queue"
            if(it.uri.contains("/Music%20Videos/")){
                if (MainActivity.queue.indexOf(it) == 0)
                    strQueue += "Playing: " + it.name + "      "
                else if (MainActivity.queue.indexOf(it) == 1)
                    strQueue += "Next: " + it.name + "      "
                else
                    strQueue += (MainActivity.queue.indexOf(it) - 1).toString() + ". " + it.name + "      "
            }else
                MainActivity.queue.remove(it)
        }
        runOnUiThread{
            textViewQueueDisplay.text = strQueue
        }

    }

    private fun onSongClick(song: Song){
        //TODO when song in RecyclerView is selected
        var alertDialogNotConnected: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogNotConnected.setCancelable(true)
        alertDialogNotConnected.setTitle("I want to play " + song.name + "!!!!!")
        alertDialogNotConnected.setMessage("Are you sure?????")
        alertDialogNotConnected.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        alertDialogNotConnected.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })
        alertDialogNotConnected.show()
    }

}


