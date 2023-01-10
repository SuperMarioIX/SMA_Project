package com.example.emagazin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    private lateinit var loginSubtraction1 : ImageView
    private lateinit var loginSubtraction2 : ImageView
    private lateinit var loginSubtraction3 : ImageView

    private lateinit var clickHereButton : Button
    private lateinit var forgotPasswordButton : Button
    private lateinit var loginSignInButton : Button

    private lateinit var loginUsernameInputLayout : TextInputLayout
    private lateinit var loginPasswordInputLayout : TextInputLayout

    private lateinit var loginUsernameInputText : TextInputEditText
    private lateinit var loginPasswordInputText : TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_activity)

        loginSubtraction1 = findViewById(R.id.loginSubtraction1)
        loginSubtraction1.bringToFront()

        loginSubtraction2 = findViewById(R.id.loginSubtraction2)
        loginSubtraction2.bringToFront()

        loginSubtraction3 = findViewById(R.id.loginSubtraction3)
        loginSubtraction3.bringToFront()

        clickHereButton = findViewById(R.id.clickHereButton)
        handleClickHereButton()

        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
        handleForgotPasswordButton()

        loginUsernameInputText = findViewById(R.id.loginUsernameInputText)
        loginPasswordInputText = findViewById(R.id.loginPasswordInputText)

        loginUsernameInputLayout = findViewById(R.id.loginUsernameInputLayout)
        loginPasswordInputLayout = findViewById(R.id.loginPasswordInputLayout)

        loginSignInButton = findViewById((R.id.loginSignInButton))
        handleSignInButton()
    }

    private fun handleClickHereButton() {
        clickHereButton.setOnClickListener {
            startActivity(Intent(this, ChooseYourDomainActivity::class.java))
        }
    }

    private fun handleForgotPasswordButton() {
        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun handleSignInButton() {
        loginSignInButton.setOnClickListener {
            val username = loginUsernameInputText.text.toString()
            val password = loginPasswordInputText.text.toString()

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref = database.getReference("Users")

            ref.addValueEventListener(object : ValueEventListener {
                @SuppressLint("CommitPrefEdits")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                        for (u in snapshot.children) {
                            val un = u.child("username").value.toString()
                            val pw = u.child("password").value.toString()
                            val domain = u.child("domain").value.toString()
                            if (username == un && password == pw) {
                                loginUsernameInputLayout.isErrorEnabled = false

                                if(domain == "N") {
                                    val intent = Intent(
                                        this@LogInActivity,
                                        CompanyListActivity::class.java
                                    )
                                    intent.putExtra("username", username)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(
                                        this@LogInActivity,
                                        AddProductsActivity::class.java
                                    )
                                    val companyName = u.child("companyName").value.toString()

                                    intent.putExtra("companyName", companyName)
                                    startActivity(intent)
                                }
                                break
                            } else
                                loginUsernameInputLayout.error = "Something are incorrect"
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Debug", "Failed to read value.", error.toException())
                }
            })

        }
    }
}