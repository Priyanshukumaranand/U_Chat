package com.example.uchat
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

private lateinit var mAuth: FirebaseAuth
class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        mAuth = FirebaseAuth.getInstance()
        super.onStart()
        Handler().postDelayed({
            Toast.makeText(this@splashScreen, "alpha-build-V1.0.0012", Toast.LENGTH_SHORT).show()
            if(mAuth.currentUser != null){
                val intent = Intent(this@splashScreen, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@splashScreen, "Login in Or Create new Account", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@splashScreen,Login::class.java)
                    startActivity(intent)
            }
            finish()
        },1000)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}