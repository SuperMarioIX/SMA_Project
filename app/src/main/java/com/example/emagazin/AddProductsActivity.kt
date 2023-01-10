package com.example.emagazin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.emagazin.classes.Product
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator

class AddProductsActivity : AppCompatActivity() {

    private lateinit var editButton : Button
    private lateinit var deleteButton : Button
    private lateinit var addButton : Button
    private lateinit var scanButton : Button
    private lateinit var logOutButton : Button

    private lateinit var productName : String
    private lateinit var productPrice : String
    private lateinit var productBarecode : String
    private lateinit var companyName : String

    private lateinit var productNameText : TextInputEditText
    private lateinit var productPriceText : TextInputEditText
    private lateinit var productBarecodeText : TextInputEditText

    private lateinit var productNameLayout : TextInputLayout
    private lateinit var productPriceLayout : TextInputLayout
    private lateinit var productBarecodeLayout : TextInputLayout

    private lateinit var successTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_products_activity)

        logOutButton = findViewById(R.id.button5)

        companyName = intent.getStringExtra("companyName").toString()

        editButton = findViewById(R.id.button2)
        deleteButton = findViewById(R.id.button3)
        addButton = findViewById(R.id.button)
        scanButton = findViewById(R.id.button4)

        productNameText = findViewById(R.id.productNameText)
        productPriceText = findViewById(R.id.productPriceText)
        productBarecodeText = findViewById(R.id.productBarcodeText)

        productNameLayout = findViewById(R.id.productNameLayout)
        productPriceLayout = findViewById(R.id.productPriceLayout)
        productBarecodeLayout = findViewById(R.id.productBarcodeLayout)

        successTextView = findViewById(R.id.textView13)

        productName = ""
        productPrice = ""
        productBarecode = ""

        handleScanButton()

        handleAddButton()

        handleDeleteButton()

        handleEditButton()

        handleLogOutButton()
    }

    private fun handleLogOutButton() {
        logOutButton.setOnClickListener {
            val modalDialog = LayoutInflater.from(this@AddProductsActivity).inflate(R.layout.logout_or_no_layout, null)
            val modalBuilder = AlertDialog.Builder(this@AddProductsActivity).setView(modalDialog)
            val modalAlert = modalBuilder.show()
            val popupYesButton = modalDialog.findViewById<Button>(R.id.popupYesButton7)
            val popupNoButton = modalDialog.findViewById<Button>(R.id.popupNoButton7)

            popupYesButton.setOnClickListener {
                startActivity(Intent(this@AddProductsActivity, LogInActivity::class.java))
            }

            popupNoButton.setOnClickListener {
                modalAlert.dismiss()
            }
        }
    }

    private fun handleScanButton() {
        scanButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.addExtra("SCAN_MODE", "BARCODE_MODE")
            scanner.addExtra("SCAN_CAMERA_ID", 0)
            scanner.initiateScan()
        }
    }


    private fun clearFun() {
        val timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                successTextView.text = ""
            }
        }
        timer.start()
    }

    private fun clearErrors() {
        productBarecodeLayout.error = ""
        productNameLayout.error = ""
        productPriceLayout.error = ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null)
                if (result.contents == null)
                    Log.d("Debug", "CANCELED")
                else {
                    productBarecode = result.contents
                    productBarecodeText.setText(productBarecode)
                }
            else
                super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleAddButton() {
        addButton.setOnClickListener {
            productName = productNameText.text.toString()
            productPrice = productPriceText.text.toString()

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref =
                database.getReference(companyName)

            ref.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var existsProduct = false

                    if (snapshot.exists()) {
                        for (u in snapshot.children) {
                            if (u.child("productName").value.toString() == productName ||
                                u.child("productBarecode").value.toString() == productBarecode
                            ) {
                                existsProduct = true
                                break
                            }
                        }
                    }

                    val a = filterProductName()
                    val b = filterPrice()
                    val c = filterBarecode()
                    val d = a && b && c

                    if (d) {
                        if (!existsProduct) {
                            val product = Product(productName, productPrice, productBarecode)
                            val productId = productName
                            ref.child(productId).setValue(product)

                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")
                            productName = ""
                            productBarecode = ""

                            successTextView.text = "The product was successfully added!"
                            successTextView.setTextColor(Color.parseColor("#03ac13"))
                            clearErrors()
                            clearFun()

                        } else {
                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")

                            successTextView.text = "This product already exists!"
                            successTextView.setTextColor(Color.parseColor("#e3242b"))
                            clearErrors()
                            clearFun()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Debug", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun handleDeleteButton() {
        deleteButton.setOnClickListener {
            productName = productNameText.text.toString()
            productPrice = productPriceText.text.toString()

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref =
                database.getReference(companyName)

            ref.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var deleteProduct = false

                    if (snapshot.exists()) {
                        for (u in snapshot.children) {
                            if (u.child("productName").value.toString() == productName &&
                                u.child("productBarecode").value.toString() == productBarecode ) {
                                deleteProduct = true
                                ref.child(productName).removeValue()
                                break
                            }
                        }
                    }

                    val a = filterProductName()
                    val c = filterBarecode()
                    val d = a && c

                    if (d) {
                        if (deleteProduct) {
                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")

                            successTextView.text = "The product was successfully deleted"
                            successTextView.setTextColor(Color.parseColor("#03ac13"))
                            clearFun()
                        } else {
                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")

                            successTextView.text = "The product does not exist"
                            successTextView.setTextColor(Color.parseColor("#e3242b"))
                            clearFun()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Debug", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun handleEditButton() {
        editButton.setOnClickListener {
            productName = productNameText.text.toString()
            productPrice = productPriceText.text.toString()

            val databaseLocation =
                "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
            val database = Firebase.database(databaseLocation)
            val ref =
                database.getReference(companyName)

            ref.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var existsProduct = false

                    if (snapshot.exists()) {
                        for (u in snapshot.children) {
                            if (u.child("productName").value.toString() == productName &&
                                u.child("productBarecode").value.toString() == productBarecode ) {
                                existsProduct = true
                                break
                            }
                        }
                    }

                    val a = filterProductName()
                    val b = filterPrice()
                    val c = filterBarecode()
                    val d = a && b && c

                    if (d) {
                        if (existsProduct) {
                            val product = Product(productName, productPrice, productBarecode)
                            val productId = productName
                            ref.child(productId).setValue(product)

                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")

                            successTextView.text = "The product was successfully edited!"
                            successTextView.setTextColor(Color.parseColor("#03ac13"))
                            clearFun()
                        } else {
                            productNameText.setText("")
                            productPriceText.setText("")
                            productBarecodeText.setText("")

                            successTextView.text = "This product does not exist!"
                            successTextView.setTextColor(Color.parseColor("#e3242b"))
                            clearFun()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Debug", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun filterProductName(): Boolean {
        if(productName.isEmpty()) {
            productNameLayout.error = "This field must be completed"
            return false
        } else
            productNameLayout.error = ""

        return true
    }

    private fun filterBarecode(): Boolean {
        if(productBarecode.isEmpty()) {
            productBarecodeLayout.error = "This field must be completed"
            return false
        } else
            productBarecodeLayout.error = ""

        return true
    }

    private fun filterPrice(): Boolean {
        if(productPrice.isEmpty()) {
            productPriceLayout.error = "This field must be completed"
            return false
        } else {
            var dots = 0
            for (i in 0 until productPrice.length - 1) {
                if (productPrice[i] == '.') {
                    ++dots
                    if (dots > 1){
                        productPriceLayout.error = "This field does not respect the price format"
                        return false
                    }
                }
                else if (productPrice[i] < '0' || productPrice[i] > '9'){
                    productPriceLayout.error = "This field does not respect the price format"
                    return false
                }
            }

        }

        productPriceLayout.error = ""
        return true
    }
}