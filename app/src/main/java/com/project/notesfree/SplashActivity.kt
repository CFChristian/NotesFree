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

        // Cek apakah user sudah login atau belum
        if (auth.currentUser != null) {
            // Jika sudah login, langsung arahkan ke MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Tutup SplashActivity agar tidak kembali ke sini
            }, 2000) // 2000 ms = 2 detik
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity agar tidak kembali ke sini
        }, 2000) // 2000 ms = 2 detik

    }
}