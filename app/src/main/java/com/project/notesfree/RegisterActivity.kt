package com.project.notesfree

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.project.notesfree.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
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

            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.btnRegister.visibility = TextView.GONE
            registerUser(username, email, password)
        }
    }

    // Function to register user
    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = ProgressBar.GONE
                binding.btnRegister.visibility = TextView.VISIBLE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    binding.txtSuccess.text = "Register Success, Please check your email to verify your account"
                                    binding.txtSuccess.visibility = TextView.VISIBLE
                                    binding.txtError.visibility = TextView.GONE
                                } else {
                                    user.delete()
                                    binding.txtError.text = "Failed to send verification email. Please try again."
                                    binding.txtError.visibility = TextView.VISIBLE
                                    binding.txtSuccess.visibility = TextView.GONE
                                }
                            }
                        } else {
                            binding.txtError.text = "Failed to send verification email, Please try again later"
                            binding.txtError.visibility = TextView.VISIBLE
                            binding.txtSuccess.visibility = TextView.GONE
                            user.delete()
                        }
                    }
                } else {
                    binding.txtError.text = "Failed to register, Please try again later"
                    binding.txtError.visibility = TextView.VISIBLE
                    binding.txtSuccess.visibility = TextView.GONE
                }
            }
    }
}