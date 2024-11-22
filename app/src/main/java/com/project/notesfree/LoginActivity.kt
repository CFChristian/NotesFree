package com.project.notesfree

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.project.notesfree.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var txt_register: TextView
    private lateinit var loginBtn: Button
    private lateinit var edtEmailLogin: EditText
    private lateinit var edtPasswordLogin: EditText
    private lateinit var txtErrorLogin: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        txt_register = findViewById(R.id.register)
        loginBtn = findViewById(R.id.login_btn)
        edtEmailLogin = findViewById(R.id.edt_email_login)
        edtPasswordLogin = findViewById(R.id.edt_password_login)
        txtErrorLogin = findViewById(R.id.txt_error_login)
        auth = FirebaseAuth.getInstance()

        txt_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            // Kalau email kosong
            if (email.isEmpty()) {
                binding.edtEmailLogin.error = "Email is required"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            // Kalau password kosong
            if (password.isEmpty()) {
                binding.edtPasswordLogin.error = "Password is required"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }
            loginFirebase(email, password)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Close the app
    }

    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("User_ID", userId)
                    startActivity(intent)
                    finish()
                } else {
                    binding.txtErrorLogin.text = "Email or password is incorrect"
                }
            }
    }
}