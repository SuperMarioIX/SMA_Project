package com.example.emagazin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.example.emagazin.classes.NaturalUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NormalPersonSignUpActivity : AppCompatActivity() {
    private lateinit var userLogo : ImageView
    private lateinit var emailLogo : ImageView
    private lateinit var ring : ImageView
    private lateinit var ring2 : ImageView
    private lateinit var passwordLogo1 : ImageView
    private lateinit var passwordLogo2 : ImageView

    private lateinit var data1 : TextView
    private lateinit var  data2 : TextView

    private lateinit var signUpButton: Button

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    private lateinit var dataCheckBox: CheckBox

    // Main Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal_person_sign_up_activity)

        userLogo = findViewById(R.id.userLogo)
        emailLogo = findViewById(R.id.emailLogo)
        ring = findViewById(R.id.ring)
        ring2 = findViewById(R.id.ring2)
        passwordLogo1 = findViewById(R.id.passwordLogo1)
        passwordLogo2 = findViewById(R.id.passwordLogo2)
        data1 = findViewById(R.id.data1)
        data2 = findViewById(R.id.data2)

        bringNodesToBack()

        signUpButton = findViewById(R.id.signUpButton)

        handleSignUpButton()
    }

    private fun bringNodesToBack() {
        userLogo.bringToFront()
        emailLogo.bringToFront()
        ring.bringToFront()
        ring2.bringToFront()
        passwordLogo1.bringToFront()
        passwordLogo2.bringToFront()
        data1.bringToFront()
        data2.bringToFront()
    }

    private fun handleSignUpButton() {
        signUpButton.setOnClickListener {
            firstName = findViewById<TextInputEditText>(R.id.firstNameText).text.toString()
            lastName = findViewById<TextInputEditText>(R.id.lastNameText).text.toString()
            username = findViewById<TextInputEditText>(R.id.usernameText).text.toString()
            email = findViewById<TextInputEditText>(R.id.emailText).text.toString()
            password = findViewById<TextInputEditText>(R.id.passwordText).text.toString()
            confirmPassword = findViewById<TextInputEditText>(R.id.confirmPasswordText).text.toString()

            firstNameLayout = findViewById(R.id.firstNameLayout)
            lastNameLayout = findViewById(R.id.lastNameLayout)
            usernameLayout = findViewById(R.id.usernameLayout)
            emailLayout = findViewById(R.id.emailLayout)
            passwordLayout = findViewById(R.id.passwordLayout)
            dataCheckBox = findViewById(R.id.dataCheckBox)

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref = database.getReference("Users")

            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var existsUsername = false
                    var existsEmail = false

                    if(snapshot.exists()) {
                        for(u in snapshot.children) {
                            val uname = u.child("username").value.toString()
                            val mail = u.child("email").value.toString()

                            if(email == mail || uname == username) {
                                if (email == mail)
                                    existsEmail = true
                                if (username == uname)
                                    existsUsername = true

                                break
                            }
                        }
                    }

                    val a = filterFirstName()
                    val b = filterLastName()
                    val c = filterUsername(existsUsername)
                    val d = filterEmail(existsEmail)
                    val e = filterPassword()
                    val f = filterDataCheckBox()
                    val isDataCorrect = a && b && c && d && e && f

                    if(isDataCorrect) {
                        val naturalUserId = username + "_natural"
                        val naturalUser = NaturalUser(firstName, lastName, username, email, password, "N")
                        ref.child(naturalUserId).setValue(naturalUser)
                        val intent = Intent(
                            this@NormalPersonSignUpActivity,
                            CompanyListActivity::class.java
                        )
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Debug", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun filterFirstName(): Boolean {
        if(firstName.contains(" ")) {
            firstNameLayout.error = "Whitespaces are not allowed"
            return false
        } else if(firstName.length > 20) {
            firstNameLayout.error = "Length must not exceed 20 characters"
            return false
        } else if(firstName.isEmpty()) {
            firstNameLayout.error = "This field must be completed"
            return false
        } else if(!containsOnlyLetters(firstName)) {
            firstNameLayout.error = "This field must contain only letters"
            return false
        } else
            firstNameLayout.error = ""

        return true
    }

    private fun filterLastName(): Boolean{
        if(lastName.contains(" ")) {
            lastNameLayout.error = "Whitespaces are not allowed"
            return false
        } else if(lastName.length > 20) {
            lastNameLayout.error = "Length must not exceed 20 characters"
            return false
        } else if(lastName.isEmpty()) {
            lastNameLayout.error = "This field must be completed"
            return false
        } else if(!containsOnlyLetters(lastName)) {
            lastNameLayout.error = "This field must contain only letters"
            return false
        } else
            lastNameLayout.error = ""

        return true
    }

    private fun filterUsername(existsUsername: Boolean): Boolean {
        if(username.contains(" ")) {
            usernameLayout.error = "Whitespaces are not allowed"
            return false
        } else if(username.length > 15) {
            usernameLayout.error = "Length must not exceed 20 characters"
            return false
        } else if(username.isEmpty()) {
            usernameLayout.error = "This field must be completed"
            return false
        } else if(!containsAdmisibleCharacters(username)) {
            usernameLayout.error = "This field contains wrong characters"
            return false
        } else if (existsUsername) {
            usernameLayout.error = "This username is already taken"
            return false
        } else
            usernameLayout.error = ""

        return true
    }

    private fun filterEmail(existsEmail: Boolean): Boolean {
        if(email.contains(" ")) {
            emailLayout.error = "Whitespaces are not allowed"
            return false
        } else if(email.isEmpty()) {
            emailLayout.error = "This field must be completed"
            return false
        } else if(!validEmail(email)) {
            emailLayout.error = "The email is not correct"
            return false
        } else if(existsEmail) {
            emailLayout.error = "This email has been used"
            return false
        } else
            emailLayout.error = ""

        return true
    }

    private fun filterPassword(): Boolean {
        if(password.contains(" ")) {
            passwordLayout.error = "Whitespaces are not allowed"
            return false
        } else if(password.length < 6) {
            passwordLayout.error = "Password must have at least 6 characters"
            return false
        } else if(password.length > 20) {
            passwordLayout.error = "Length must not exceed 20 characters"
            return false
        } else if(password.isEmpty()) {
            passwordLayout.error = "This field must be completed"
            return false
        } else if(password != confirmPassword) {
            passwordLayout.error = "The passwords do not match"
            return false
        } else
            passwordLayout.error = ""

        return true
    }

    private fun filterDataCheckBox() : Boolean {
        if(!dataCheckBox.isChecked) {
            data1.setTextColor(Color.parseColor("#B90E0A"))
            data2.setTextColor(Color.parseColor("#B90E0A"))
            return false
        } else {
            data1.setTextColor(Color.parseColor("#253A4B"))
            data2.setTextColor(Color.parseColor("#253A4B"))
        }

        return true
    }

    private fun containsOnlyLetters(string: String): Boolean {
        return string.filter { it in 'A'..'Z' || it in 'a'..'z' }.length == string.length
    }

    private fun containsAdmisibleCharacters(string: String): Boolean {
        return string.filter { it in 'A'..'Z' || it in 'a'..'z'  || it in '0'..'9' || it == '_' || it == '.' || it == '.'}.length == string.length
    }

    private fun validEmail(email: String): Boolean {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}