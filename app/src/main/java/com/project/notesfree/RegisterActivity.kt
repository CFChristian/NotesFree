package com.project.notesfree

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.notesfree.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtError: TextView
    private lateinit var txtSuccess: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val txtLogin: TextView = findViewById(R.id.txt_login)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        txtError = findViewById(R.id.txt_error)
        txtSuccess = findViewById(R.id.txt_success)


        txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener{
            val username = binding.edtUsernameRegister.text.toString()
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            val confirmPassword = binding.edtConfirmPasswordRegister.text.toString()

            // Kalau username kosong
            if (username.isEmpty()) {
                binding.edtUsernameRegister.error = "Username is required"
                binding.edtUsernameRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau email kosong
            if (email.isEmpty()) {
                binding.edtEmailRegister.error = "Email is required"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau email tidak valid
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailRegister.error = "Please enter a valid email"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau password kosong
            if (password.isEmpty()) {
                binding.edtPasswordRegister.error = "Password cannot be empty"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau confirm password kosong
            if (confirmPassword.isEmpty()) {
                binding.edtConfirmPasswordRegister.error = "Confirm Password cannot be empty"
                binding.edtConfirmPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau password kurang dari 6 karakter
            if (password.length < 6) {
                binding.edtPasswordRegister.error = "Password length should be at least 6 characters"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            // Kalau password tidak sama dengan confirm password
            if (password != confirmPassword) {
                binding.edtConfirmPasswordRegister.error = "Password not match"
                binding.edtConfirmPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            progressBar.visibility = ProgressBar.VISIBLE
            btnRegister.visibility = TextView.GONE
            registerUser(username, email, password)
        }
    }

    // Fungsi registerUser
    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = ProgressBar.GONE
                btnRegister.visibility = TextView.VISIBLE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    txtSuccess.text =
                                        "Register Success, Please check your email to verify your account"
                                    txtSuccess.visibility = TextView.VISIBLE
                                    txtError.visibility = TextView.GONE
                                } else {
                                    user.delete()
                                    txtError.text = "Failed to send verification email. Please try again."
                                    txtError.visibility = TextView.VISIBLE
                                    txtSuccess.visibility = TextView.GONE
                                }
                            }
                        } else {
                            txtError.text = "Failed to send verification email, Please try again later"
                            txtError.visibility = TextView.VISIBLE
                            txtSuccess.visibility = TextView.GONE
                            user.delete()
                        }
                    }
                } else {
                    txtError.text = "Failed to register, Please try again later"
                    txtError.visibility = TextView.VISIBLE
                    txtSuccess.visibility = TextView.GONE
                }
            }
    }
}