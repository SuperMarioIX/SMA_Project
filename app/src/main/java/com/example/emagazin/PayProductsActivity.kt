package com.example.emagazin

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PayProductsActivity : AppCompatActivity() {
    private lateinit var paymentTotalPrice : TextView

    private lateinit var cardNameTextLayout: TextInputLayout
    private lateinit var cardNumberTextLayout: TextInputLayout
    private lateinit var expiryDateTextLayout: TextInputLayout
    private lateinit var securityCodeTextLayout: TextInputLayout

    private lateinit var cardNameTextInput: TextInputEditText
    private lateinit var cardNumberTextInput: TextInputEditText
    private lateinit var expiryDateTextInput: TextInputEditText
    private lateinit var securityCodeTextInput: TextInputEditText

    private lateinit var cardName : String
    private lateinit var cardNumber : String
    private lateinit var expiryDate : String
    private lateinit var securityCode : String
    private lateinit var totalPrice : String
    private lateinit var username : String

    private lateinit var payButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_products_activity)

        totalPrice = intent.getStringExtra("totalPrice").toString().split(" ")[0] + "RON"
        username = intent.getStringExtra("totalPrice").toString().split(" ")[1]

        paymentTotalPrice = findViewById(R.id.paymentTotalPrice)
        paymentTotalPrice.text = totalPrice

        cardNameTextLayout = findViewById(R.id.cardNameTextLayout)
        cardNumberTextLayout = findViewById(R.id.cardNumberTextLayout)
        expiryDateTextLayout = findViewById(R.id.expiryDateTextLayout)
        securityCodeTextLayout = findViewById(R.id.securityCodeTextLayout)

        cardNameTextInput = findViewById(R.id.cardNameTextField)
        cardNumberTextInput = findViewById(R.id.cardNumberTextField)
        expiryDateTextInput = findViewById(R.id.expiryDateTextField)
        securityCodeTextInput = findViewById(R.id.securityCodeTextField)

        payButton = findViewById(R.id.payButton2)

        handlePayButton()
    }

    private fun handlePayButton() {
        payButton.setOnClickListener {
            cardName = cardNameTextInput.text.toString()
            cardNumber = cardNumberTextInput.text.toString()
            expiryDate = expiryDateTextInput.text.toString()
            securityCode = securityCodeTextInput.text.toString()

            val a = filterCardName()
            val b = filterCardNumber()
            val c = filterExpiryDate()
            val d = filterSecurityCode()
            val result = a && b && c && d

            if(result) {
                val modalDialog = LayoutInflater.from(this@PayProductsActivity).inflate(R.layout.successful_pay_popup, null)
                val modalBuilder = AlertDialog.Builder(this@PayProductsActivity).setView(modalDialog)
                val modalAlert = modalBuilder.show()
                val closeButton = modalDialog.findViewById<Button>(R.id.closeButton2)

                closeButton.setOnClickListener {
                    val intent2 = Intent(
                        this@PayProductsActivity,
                        CompanyListActivity::class.java
                    )
                    intent2.putExtra("username", username)
                    startActivity(intent2)
                    modalAlert.dismiss()
                }
            }

        }
    }

    private fun filterCardName() : Boolean {
        if(cardName.isEmpty()) {
            cardNameTextLayout.error = "This field is empty"
            return false
        } else if(!containsLetters(cardName)) {
            cardNameTextInput.error = "This field contains wrong characters"
            return false
        }

        cardNameTextLayout.error = ""
        return true
    }

    private fun filterCardNumber() : Boolean {
        if(cardNumber.isEmpty()) {
            cardNumberTextLayout.error = "This field is empty"
            return false
        } else if(!containsNumbers(cardNumber)) {
            cardNumberTextLayout.error = "This field contains wrong characters"
            return false
        } else if(cardNumber.length != 16) {
            cardNumberTextLayout.error = "This field does not respect the length format"
            return false
        }

        cardNumberTextLayout.error = ""
        return true
    }

    private fun filterExpiryDate() : Boolean {
        if(expiryDate.isEmpty()) {
            expiryDateTextLayout.error = "This field is empty"
            return false
        } else if(expiryDate.length != 5) {
            expiryDateTextLayout.error = "This field does not respect the length format"
            return false
        } else if(expiryDate[0] < '0' && expiryDate[0] > '1' ||
                  expiryDate[1] < '0' && expiryDate[1]> '9' ||
                  expiryDate[2] != '/' ||
                  expiryDate[3] < '0' && expiryDate[3] > '9' ||
                  expiryDate[4] < '0' && expiryDate[4] > '9'
                ) {
            expiryDateTextLayout.error = "This field contains wrong characters"
            return false
        }

        expiryDateTextLayout.error = ""
        return true
    }

    private fun filterSecurityCode() : Boolean {
        if(securityCode.isEmpty()) {
            securityCodeTextLayout.error = "This field is empty"
            return false
        } else if(securityCode.length != 3) {
            securityCodeTextLayout.error = "This field does not respect the length format"
            return false
        } else if(securityCode[0] < '0' && securityCode[0] < '9' ||
                  securityCode[1] < '0' && securityCode[1] < '9' ||
                  securityCode[2] < '0' && securityCode[2] < '9'
                ) {
            securityCodeTextLayout.error = "This field contains wrong characters"
            return false
        }

        securityCodeTextLayout.error = ""
        return true
    }

    private fun containsNumbers(string: String): Boolean {
        return string.filter {it in '0'..'9'}.length == string.length
    }

    private fun containsLetters(string: String): Boolean {
        return string.filter {it in 'A'..'Z' || it in 'a'..'z' || it == ' '}.length == string.length
    }
}