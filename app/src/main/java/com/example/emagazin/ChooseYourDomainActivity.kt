package com.example.emagazin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView

class ChooseYourDomainActivity : AppCompatActivity() {

    private lateinit var naturalLogo : ImageView
    private lateinit var legalLogo : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_your_domain_activity)

        naturalLogo = findViewById(R.id.naturalLogo)
        naturalLogo.bringToFront()

        legalLogo = findViewById(R.id.legalLogo)
        legalLogo.bringToFront()


        naturalLogo.setOnClickListener {
            startActivity(Intent(this, NormalPersonSignUpActivity::class.java))
        }

        legalLogo.setOnClickListener {
            startActivity(Intent(this, LegalPersonSignUpActivity::class.java))
        }
    }
}