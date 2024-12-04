package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wms.apis.UserRepository
import com.example.wms.databinding.ActivityLoginBinding
import com.example.wms.models.LoginRequest
import com.example.wms.helpers.StoredDataHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var intentHome: Intent
    private lateinit var intentRegister: Intent

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        val username: String = StoredDataHelper.get(this)
        if (!username.isEmpty()) startActivity(Intent(this, HomepageActivity::class.java))
        editTextUsername = loginBinding.editTextUsernameLogin
        editTextPassword = loginBinding.editTextPasswordLogin
        btnLogin = loginBinding.btnLogin
        btnRegister = loginBinding.btnRegister

        // Initialize repository with context
        repository = UserRepository(this)

        login()
    }

    private fun login() {
        intentHome = Intent(this, HomepageActivity::class.java)
        btnLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim { it <= ' ' }
            val password = editTextPassword.text.toString().trim { it <= ' ' }
            if (username.isEmpty()) editTextUsername.error = "Please type your username."
            else if (password.isEmpty()) editTextPassword.error = "Please type your password."
            else {
                repository.login(
                    loginRequest = LoginRequest(username, password),
                    onSuccess = { user ->
                        Toast.makeText(this, "Login successfully.", Toast.LENGTH_SHORT).show()
                        StoredDataHelper.save(this, username)
                        startActivity(intentHome)
                    }, onError = { error ->
                        Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT)
                            .show()
                    })
            }
        }
        btnRegister.setOnClickListener {
            intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }
    }
}