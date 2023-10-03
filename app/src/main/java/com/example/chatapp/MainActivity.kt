package com.example.chatapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chatapp.chatlist.ChatListFragment
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.mypage.myPageFragment
import com.example.chatapp.userlist.UserFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val userFragment = UserFragment()
    private val chatListFragment = ChatListFragment()
    private val myPageFragment = myPageFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = Firebase.auth.currentUser

        if(currentUser == null){
            //로그인 안되어있음
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        askNotificationPermission()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.userList ->{
                    replaceFragment(userFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.chatRoomList ->{
                    replaceFragment(chatListFragment)
                    return@setOnItemSelectedListener  true
                }
                R.id.myPage -> {
                    replaceFragment(myPageFragment)
                    return@setOnItemSelectedListener  true
                }
                else ->{
                    return@setOnItemSelectedListener  false
                }
            }
        }
        replaceFragment(userFragment)

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply{
                replace(R.id.frameLayout,fragment)
                    .commit()
            }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            //알림권한 없음
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationalDiaglog()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationalDiaglog(){
        AlertDialog.Builder(this)
            .setMessage("알림 권한이 없으면 알림을 받을 수 없습니다")
            .setPositiveButton("권한 허용하기"){_,_ ->
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }.setNegativeButton("취소"){dialogInterface,_->dialogInterface.cancel()}.show()

    }

}