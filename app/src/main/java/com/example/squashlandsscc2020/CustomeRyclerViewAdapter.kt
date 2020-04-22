package com.example.squashlandsscc2020

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomeRyclerViewAdapter(var songs: ArrayList<Song>, var context: Context, val clickListener: (Song) -> Unit): RecyclerView.Adapter<CustomeRyclerViewAdapter.ViewHolder>(), Filterable {
    private var songsNotFiltered : ArrayList<Song> = ArrayList(songs)
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(song: Song, clickListener: (Song) -> Unit) {
            val textViewSongItem = itemView.findViewById<TextView>(R.id.songItem)
            textViewSongItem.text = song.name
            itemView.setOnClickListener {
                clickListener(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.row,parent,false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (songs[position].uri.contains("/Music%20Videos/"))
            holder.bindItems(songs[position], clickListener)
    }

    fun getSongUri(position: Int): String{
        return songs[position].uri
    }

    interface CustomOnClickListener:(Song) -> Unit{
        fun setClickListener(song: Song)
    }

    override fun getFilter(): Filter {
        return songFilter
    }

    private var songFilter: Filter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            var filteredSong = ArrayList<Song>()
            if (p0 == null || p0.length ==0){
                filteredSong.addAll(songsNotFiltered)
            }else{
                var strQueryPattern = p0.toString().toLowerCase().trim()
                songsNotFiltered.forEach {
                    if (it.name.toLowerCase().contains(strQueryPattern))
                        filteredSong.add(it)
                }
            }
            var filterResults = FilterResults()
            filterResults.values = filteredSong
            return filterResults
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            songs.clear()
            songs.addAll(p1?.values as ArrayList<Song>)
            notifyDataSetChanged()
        }
    }
}

