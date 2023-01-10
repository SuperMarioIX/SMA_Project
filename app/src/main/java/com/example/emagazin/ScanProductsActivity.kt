package com.example.emagazin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.properties.Delegates
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ScanProductsActivity : AppCompatActivity() {
    private lateinit var scanButton : Button
    private lateinit var payButton : Button

    private lateinit var linearLayout : LinearLayout

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var companyName : String
    private lateinit var username : String

    private lateinit var total : TextView
    private lateinit var errorTextView : TextView

    private var counter by Delegates.notNull<Int>()
    private var totalPrice by Delegates.notNull<Double>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_products_activity)

        companyName = intent.getStringExtra("companyName").toString().split(" ")[0]
        username = intent.getStringExtra("companyName").toString().split(" ")[1]

        scanButton = findViewById(R.id.scanButton)

        payButton = findViewById(R.id.payButton)

        linearLayout = findViewById(R.id.scanProductsLinearLayout)

        constraintLayout = findViewById(R.id.scanProductsConstraintLayout)

        errorTextView = findViewById(R.id.textView21)

        counter = 0
        totalPrice = 0.0

        initTotalPriceTextView()

        handleScanButton()

        handlePayButton()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTotalPriceTextView() {
        total = TextView(this@ScanProductsActivity)
        total.textSize = 20f
        total.text = "Total: " + totalPrice.toString() + "RON"
        total.setTextColor(Color.parseColor("#253A4B"))
        total.typeface = resources.getFont(R.font.segoe_ui_light)

        constraintLayout.addView(total)

        val params = total.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = constraintLayout.id
        params.topToTop = constraintLayout.id
        params.bottomToBottom = constraintLayout.id
        params.endToEnd = constraintLayout.id
        params.horizontalBias = 0.9F
        params.verticalBias = 0.05F
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
                errorTextView.text = ""
            }
        }
        timer.start()
    }

    private fun handlePayButton() {
        payButton.setOnClickListener {

            val modalDialog = if(totalPrice == 0.0)
                LayoutInflater.from(this@ScanProductsActivity).inflate(R.layout.payment_info_popup_layout, null)
            else
                LayoutInflater.from(this@ScanProductsActivity).inflate(R.layout.continue_payment_popup_layout, null)

            val modalBuilder = AlertDialog.Builder(this@ScanProductsActivity).setView(modalDialog)
            val modalAlert = modalBuilder.show()

            if(totalPrice != 0.0) {
                val popupYesButton = modalDialog.findViewById<Button>(R.id.popupYesButton2)
                val popupNoButton = modalDialog.findViewById<Button>(R.id.popupNoButton2)

                popupYesButton.setOnClickListener {
                    val intent2 = Intent(
                        this@ScanProductsActivity,
                        PayProductsActivity::class.java
                    )
                    val message = "$totalPrice $username"
                    intent2.putExtra("totalPrice", message)
                    startActivity(intent2)
                    modalAlert.dismiss()
                }

                popupNoButton.setOnClickListener {
                    modalAlert.dismiss()
                }
            } else {
                val closeButton = modalDialog.findViewById<Button>(R.id.closeButton)

                closeButton.setOnClickListener {
                    modalAlert.dismiss()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null)
                if(result.contents == null)
                    Log.d("Debug", "CANCELED")
                else {
                    val productBarecode = result.contents

                    val databaseLocation =
                        "https://emagazin-f1b52-default-rtdb.europe-west1.firebasedatabase.app/"
                    val database = Firebase.database(databaseLocation)
                    val ref = database.getReference(companyName)

                    ref.addValueEventListener(object : ValueEventListener {
                        @SuppressLint("CommitPrefEdits")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var exists = false

                            if (snapshot.exists())
                                for (u in snapshot.children)
                                    if(u.child("productBarecode").value.toString() == productBarecode) {
                                        val _productPrice = u.child("productPrice").value.toString().toDouble()
                                        val _productName = u.child("productName").value.toString()
                                        exists = true

                                        val price = TextView(this@ScanProductsActivity)
                                        price.textSize = 20f
                                        price.text = _productPrice.toString() + "RON"
                                        price.setTextColor(Color.parseColor("#253A4B"))
                                        price.typeface = resources.getFont(R.font.segoe_ui_light)
                                        price.bringToFront()

                                        val delete = TextView(this@ScanProductsActivity)
                                        delete.textSize = 15f
                                        delete.text = "x"
                                        delete.setTextColor(Color.parseColor("#e3242b"))
                                        delete.typeface = resources.getFont(R.font.segoe_ui_light)
                                        delete.bringToFront()

                                        val productName = TextView(this@ScanProductsActivity)
                                        productName.textSize = 20f
                                        productName.text = _productName
                                        productName.setTextColor(Color.parseColor("#253A4B"))
                                        productName.typeface = resources.getFont(R.font.segoe_ui_light)
                                        productName.bringToFront()

                                        val photoPath = "@drawable/" + "company_box"
                                        val image = ImageView(this@ScanProductsActivity)
                                        image.setImageDrawable(
                                            ContextCompat.getDrawable(this@ScanProductsActivity, resources.getIdentifier(photoPath, null,
                                                packageName
                                            )))

                                        val container = ConstraintLayout(this@ScanProductsActivity)
                                        container.addView(image)
                                        container.addView(delete)
                                        container.addView(price)
                                        container.addView(productName)
                                        container.id = counter++

                                        val deleteParams = delete.layoutParams as ConstraintLayout.LayoutParams
                                        deleteParams.startToStart = container.id
                                        deleteParams.topToTop = container.id
                                        deleteParams.bottomToBottom = container.id
                                        deleteParams.endToEnd = container.id
                                        deleteParams.verticalBias = 0.485F
                                        deleteParams.horizontalBias = 0.03F

                                        val productNameParams = productName.layoutParams as ConstraintLayout.LayoutParams
                                        productNameParams.startToStart = container.id
                                        productNameParams.topToTop = container.id
                                        productNameParams.bottomToBottom = container.id
                                        productNameParams.endToEnd = container.id
                                        productNameParams.verticalBias = 0.55F
                                        productNameParams.horizontalBias = 0.08F

                                        val priceParams = price.layoutParams as ConstraintLayout.LayoutParams
                                        priceParams.startToStart = container.id
                                        priceParams.topToTop = container.id
                                        priceParams.bottomToBottom = container.id
                                        priceParams.endToEnd = container.id
                                        priceParams.verticalBias = 0.55F
                                        priceParams.horizontalBias = 0.95F

                                        val imageParams = image.layoutParams as ConstraintLayout.LayoutParams
                                        imageParams.startToStart = container.id
                                        imageParams.topToTop = container.id
                                        imageParams.bottomToBottom = container.id
                                        imageParams.endToEnd = container.id

                                        delete.setOnClickListener {
                                            val modalDialog = LayoutInflater.from(this@ScanProductsActivity).inflate(R.layout.delete_or_no_layout, null)
                                            val modalBuilder = AlertDialog.Builder(this@ScanProductsActivity).setView(modalDialog)
                                            val modalAlert = modalBuilder.show()
                                            val popupYesButton = modalDialog.findViewById<Button>(R.id.popupYesButton)
                                            val popupNoButton = modalDialog.findViewById<Button>(R.id.popupNoButton)

                                            popupYesButton.setOnClickListener {
                                                linearLayout.removeView(container)
                                                totalPrice -= _productPrice
                                                total.text = "Total: " + totalPrice.toString() + "RON"
                                                modalAlert.dismiss()
                                            }

                                            popupNoButton.setOnClickListener {
                                                modalAlert.dismiss()
                                            }
                                        }

                                        linearLayout.addView(container)

                                        val params = container.layoutParams as LinearLayout.LayoutParams
                                        params.topMargin = 25
                                        params.bottomMargin = 25
                                        params.leftMargin = 5
                                        params.rightMargin = 5

                                        totalPrice += _productPrice
                                        total.text = "Total: " + totalPrice.toString() + "RON"
                                        errorTextView.text = ""
                                    }

                            if(!exists) {
                                errorTextView.text = "This product does not exist"
                                clearFun()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Debug", "Failed to read value.", error.toException())
                        }
                    })
                }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

}