package com.example.wms

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wms.api.UserRepository
import com.example.wms.databinding.ActivityRegisterBinding
import org.mindrot.jbcrypt.BCrypt

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnCreateAccount: Button
    private lateinit var btnLogin: Button
    private lateinit var intentLogin: Intent

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        editTextUsername = registerBinding.editTextUsernameRegister
        editTextPassword = registerBinding.editTextPasswordRegister
        btnCreateAccount = registerBinding.btnCreateAccount
        btnLogin = registerBinding.btnLogin1

        // Initialize repository with context
        repository = UserRepository(this)

        register()
    }

    private fun register() {
        intentLogin = Intent(this, LoginActivity::class.java)

        btnCreateAccount.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = BCrypt.hashpw(editTextPassword.text.toString(), BCrypt.gensalt())
            // Validate input fields
            if (username.isEmpty()) {
                editTextUsername.error = "Please type your username."
            } else if (password.isEmpty()) {
                editTextPassword.error = "Please type your password."
            } else {
                val user = User(username, password, false, "", "", "")
                // Add user to database
                repository.addUser(
                    user = user,
                    onSuccess = { newUser ->
                        // Handle successful registration
                        Toast.makeText(
                            this,
                            "Register successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(intentLogin)
                    },
                    onError = { error ->
                        // Handle duplicated username
                        Toast.makeText(
                            this,
                            "$username is already taken. Try another username.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }

        }

        btnLogin.setOnClickListener {
            startActivity(intentLogin)
        }
    }
}