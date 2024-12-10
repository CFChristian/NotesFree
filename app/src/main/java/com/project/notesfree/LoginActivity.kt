package com.project.notesfree
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.notesfree.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
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


    // Login to Firebase
    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.txtErrorLogin.visibility = TextView.VISIBLE
                        binding.txtErrorLogin.text = "Please verify your email address"
                    }
                } else {
                    binding.txtErrorLogin.visibility = TextView.VISIBLE
                    binding.txtErrorLogin.text = "Email or password is incorrect"
                }
            }
    }
}