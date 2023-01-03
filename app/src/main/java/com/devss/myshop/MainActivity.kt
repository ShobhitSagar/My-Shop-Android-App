package com.devss.myshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.devss.myshop.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference

    private lateinit var mView: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mView.root)

        db = FirebaseDatabase.getInstance().reference

        mView.addBtn.setOnClickListener { checkData() }
        mView.allProdBtn.setOnClickListener { startActivity(Intent(this, AllProductActivity::class.java)) }
    }

    private fun checkData() {
        val name: String = mView.nameEt.text.toString().trim()
        val model: String = mView.modelEt.text.toString().trim()
        val price: String = mView.priceEt.text.toString().trim()

        if (name.isNotBlank()) {
            if (model.isNotBlank()) {
                if (model.isDigitsOnly()) {
                    if (price.isNotBlank()) {
                        if (price.isDigitsOnly()) {
                            addNewProduct(name, model.toInt(), price.toInt(), false)
                        } else toastS("Please enter proper price(Only Digits)")
                    } else toastS("Price cannot be empty")
                } else toastS("Please enter proper model number(Only Digits)")
            } else toastS("Model cannot be empty")
        } else toastS("Name cannot be empty")
    }

    private fun addNewProduct(name: String, model: Int, price: Int, sold: Boolean) {
        val product = Product(name, model, price, sold)
        db.child("users")
            .child("hsmotorsgzb")
            .child("products")
            .push().setValue(product)
            .addOnSuccessListener {
                resetUI()
                toastL("Product Added Successfully")
            }
            .addOnFailureListener { toastS("Something went wrong") }
    }

    private fun resetUI() {
        mView.nameEt.setText("")
        mView.modelEt.setText("")
        mView.priceEt.setText("")
    }

    private fun toastS(txt: String) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
    }

    private fun toastL(txt: String) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show()
    }
}