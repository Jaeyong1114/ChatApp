package com.example.chatapp.chatlist

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Key.Companion.DB_CHAT_ROOMS
import com.example.chatapp.R
import com.example.chatapp.chatdetail.ChatActivity
import com.example.chatapp.databinding.FragmentChatlistBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

    private lateinit var binding : FragmentChatlistBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatlistBinding.bind(view) //view가 있으므로 inflate 로 새로안만들어줘도됨

        val chatListAdapter = ChatListAdapter{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_OTHER_USER_ID,it.otherUserId)
            intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID,it.chatRoomId)

            startActivity(intent)
        }
        binding.chatListRecyclerView.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter

        }
        val currentUserId = Firebase.auth.currentUser?.uid ?: return
        val chatRoomsDB = Firebase.database.reference.child(DB_CHAT_ROOMS).child(currentUserId)

        chatRoomsDB.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomList = snapshot.children.map{
                    it.getValue(ChatRoomItem::class.java)
                }
                chatListAdapter.submitList(chatRoomList)

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}