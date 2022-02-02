package com.livelike.demo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.livelike.demo.R
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo


class PinMessageAdapter(private val messageList: ArrayList<PinMessageInfo>) : RecyclerView.Adapter<PinMessageAdapter.ViewHolder>() {

    // holder class to hold reference
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var messageTextView: TextView = view.findViewById(R.id.message) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pin_message_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.messageTextView.text = messageList[position].messagePayload?.message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessageToList(newMessage: PinMessageInfo) {
        messageList.add(newMessage)
        notifyDataSetChanged()
    }

    fun removeMessageFromList(messageId: String) {
        val index = messageList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messageList.removeAt(index)
            notifyDataSetChanged()
        }

    }

    fun addMessages(newMessageList: ArrayList<PinMessageInfo>) {
        messageList.clear()
        notifyDataSetChanged()
        messageList.addAll(newMessageList)
        notifyDataSetChanged()

    }
}