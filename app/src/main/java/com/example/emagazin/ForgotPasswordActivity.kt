package com.example.emagazin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var sendButton : Button

    private lateinit var enterYourEmailLayout : TextInputLayout

    private lateinit var enterYourEmailText : TextInputEditText

    private lateinit var email : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        enterYourEmailLayout = findViewById(R.id.enterYourEmailLayout)

        enterYourEmailText = findViewById(R.id.enterYourEmailText)

        sendButton = findViewById(R.id.sendButton)
        handleSendButton()
    }

    private fun handleSendButton() {
        sendButton.setOnClickListener {
            email = enterYourEmailText.text.toString()
            val subject = "Recover your password"

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref = database.getReference("Users")

            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        for (u in snapshot.children) {
                            val m = u.child("email").value.toString()
                            val password = u.child("password").value.toString()
                            if (m == email) {
                                enterYourEmailLayout.error = ""
                                val mailIntent = Intent(Intent.ACTION_SEND)
                                mailIntent.data = Uri.parse("mailto:")
                                mailIntent.type = "text/plain"
                                mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                                mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                                mailIntent.putExtra(Intent.EXTRA_TEXT, password)
                                startActivity(
                                    Intent.createChooser(
                                        mailIntent,
                                        "Choose your email client..."
                                    )
                                )
                                break
                            } else if(m != email)
                                enterYourEmailLayout.error = "Your email is incorrect"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Debug", "Failed to read value.", error.toException())
                }
            })
        }

    }

}