package com.project.notesfree
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Check if a user is logged in
        if (currentUser != null) {
            // Check if the user account still exists
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful && auth.currentUser != null) {
                    // If the user is still logged in, navigate to MainActivity
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close SplashActivity to prevent returning to it
                    }, 2000) // 2000 ms = 2 seconds
                } else {
                    // If the user account no longer exists, sign out and navigate to LoginActivity
                    auth.signOut()
                    navigateToLogin()
                }
            }
        } else {
            // If no user is logged in, navigate to LoginActivity
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity agar tidak kembali ke sini
        }, 2000) // 2000 ms = 2 detik
    }
}