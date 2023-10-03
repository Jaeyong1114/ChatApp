package com.example.chatapp.userlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentUserlistBinding

class UserFragment : Fragment(R.layout.fragment_userlist) {

    private lateinit var binding :FragmentUserlistBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserlistBinding.bind(view) //view가 있으므로 inflate 로 새로안만들어줘도됨

        val userListAdapter = UserAdapter()
        binding.userListRecyclerView.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter

        }
    }
}