package com.example.chatapp.chatdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Key.Companion.DB_CHATS
import com.example.chatapp.Key.Companion.DB_CHAT_ROOMS
import com.example.chatapp.Key.Companion.DB_USERS
import com.example.chatapp.Key.Companion.FCM_SERVER_KEY
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.userlist.UserItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    private var chatRoomId: String = ""
    private var otherUserId: String = ""
    private var otherUserFcmToken : String = ""
    private var myUserId: String = ""
    private var myUserName : String = ""
    private var isInit = false

    private val chatItemList = mutableListOf<ChatItem>()

    //chatRoomId putExtra
    //otherUserId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatAdapter = ChatAdapter()
        chatRoomId = intent.getStringExtra( EXTRA_CHAT_ROOM_ID) ?: return
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserId = Firebase.auth.currentUser?.uid ?: ""

        Firebase.database.reference.child(DB_USERS).child(myUserId).get()
            .addOnSuccessListener {
                val myUserItem = it.getValue(UserItem::class.java)
                myUserName = myUserItem?.userName?:""

                getOtherUserData()
            }




        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }



        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()

            if(!isInit){
                return@setOnClickListener
            }

            if(message.isEmpty()){
                Toast.makeText(applicationContext,"빈 메세지를 전송할 수 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newChatItem = ChatItem(
                message = message,
                userId = myUserId
            )

            Firebase.database.reference.child(DB_CHATS).child(chatRoomId).push()
                .apply{
                    newChatItem.chatId=key
                    setValue(newChatItem)
                }

            val updates: MutableMap<String, Any> = hashMapOf(
                "${DB_CHAT_ROOMS}/$myUserId/$otherUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/chatRoomId" to chatRoomId,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserId" to myUserId,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserName" to myUserName,

            )
            Firebase.database.reference.updateChildren(updates)

            val client = OkHttpClient()

            val root = JSONObject()
            val notification = JSONObject()
            notification.put("title",getString(R.string.app_name))
            notification.put("body",message)

            root.put("to",otherUserFcmToken)
            root.put("priority", "high")
            root.put("notification",notification)

            val requestBody =root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().post(requestBody).url("https://fcm.googleapis.com/fcm/send")
                .header("Authorization","key=$FCM_SERVER_KEY").build()
            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                }
            })


            binding.messageEditText.text.clear()
        }




    }

    private fun getOtherUserData(){
        Firebase.database.reference.child(DB_USERS).child(otherUserId).get()
            .addOnSuccessListener {
                val otherUserItem = it.getValue(UserItem::class.java)
                otherUserFcmToken = otherUserItem?.fcmToken.orEmpty()
                chatAdapter.otherUserItem = otherUserItem

                isInit= true
                getChatData()

            }
    }

    private fun getChatData(){
        Firebase.database.reference.child(DB_CHATS).child(chatRoomId).addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatItem = snapshot.getValue(ChatItem::class.java)
                    chatItem ?: return



                    chatItemList.add(chatItem)
                    chatAdapter.submitList(chatItemList.toMutableList()) //새로운 리스트로 바꿔줌
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    companion object{
        const val EXTRA_CHAT_ROOM_ID = "CHAT_ROOM_ID"
        const val EXTRA_OTHER_USER_ID = "OTHER_USER_ID"
    }


}