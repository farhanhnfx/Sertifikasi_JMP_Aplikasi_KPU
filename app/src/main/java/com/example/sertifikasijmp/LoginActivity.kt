package com.example.sertifikasijmp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sertifikasijmp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnLogin.setOnClickListener {
                val username = inpUsername.text.toString()
                val password = inpPassword.text.toString()

                if (authenticationValid(username, password)) {
                    Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    val intentToMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intentToMainActivity)
                }
                else {
                    Toast.makeText(this@LoginActivity, "Username atau password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun authenticationValid(username: String, password: String): Boolean {
        val validUsername = "petugas123"
        val validPassword = "qwerty"

        return username == validUsername && password == validPassword
    }
}