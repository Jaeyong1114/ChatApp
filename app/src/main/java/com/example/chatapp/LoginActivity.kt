package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            auth = Firebase.auth
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"이메일 또는 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){//회원가입 성공
                        Toast.makeText(this,"회원가입 성공",Toast.LENGTH_SHORT).show()

                    } else {
                        //회원가입 실패
                        Toast.makeText(this,"회원가입 실패",Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.signInButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"이메일 또는 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener (this) {task ->
                    if(task.isSuccessful){
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else {
                        Log.e("LoginActivity",task.exception.toString())
                        Toast.makeText(this,"로그인에 실패했습니다",Toast.LENGTH_SHORT).show()
                    }

                }

        }







    }

}