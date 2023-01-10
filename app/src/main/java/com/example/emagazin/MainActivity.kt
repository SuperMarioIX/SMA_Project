package com.example.emagazin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)

        val shape1 = findViewById<ImageView>(R.id.shape1)
        shape1.bringToFront()

        val greetingMessage = findViewById<TextView>(R.id.greetingMessage)
        greetingMessage.alpha = 0.47F

        val logInButton = findViewById<Button?>(R.id.logInButton)
        handleLogInButton(logInButton)

        val signUpButton = findViewById<Button?>(R.id.signUpButton)
        handleSignUpButton(signUpButton)
    }

    private fun handleLogInButton(logInButton : Button?)  {
        if(logInButton == null)
            throw NullPointerException("The log in button is null.")

        logInButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        })

    }

    private fun handleSignUpButton(signUpButton : Button?)  {
        if(signUpButton == null)
            throw NullPointerException("The log in button is null.")

        signUpButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ChooseYourDomainActivity::class.java))
        })

    }
}