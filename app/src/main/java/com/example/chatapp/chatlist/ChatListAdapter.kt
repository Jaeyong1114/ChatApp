package com.example.chatapp.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemChatroomBinding
import com.example.chatapp.databinding.ItemUserBinding

class ChatListAdapter(private val onClick:(ChatRoomItem)-> Unit) : ListAdapter<ChatRoomItem,ChatListAdapter.ViewHolder>(diff){


    inner class ViewHolder(private val binding: ItemChatroomBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatRoomItem){
            binding.nicknameTextView.text=item.otherUserName
            binding.lastMessageTextView.text = item.lastMessage
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }


    companion object{
        private val diff = object : DiffUtil.ItemCallback<ChatRoomItem>(){
            override fun areItemsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem.chatRoomId ==newItem.chatRoomId
            }

            override fun areContentsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatroomBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


}