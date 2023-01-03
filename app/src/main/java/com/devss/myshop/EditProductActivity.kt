package com.devss.myshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.devss.myshop.databinding.ActivityEditProductBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "EditProductActivity"
class EditProductActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var mView: ActivityEditProductBinding
    private var productId: String? = null
    private var cProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(mView.root)

        productId = intent.getStringExtra(P_ID)
        db = FirebaseDatabase.getInstance().reference.child("users")
            .child("hsmotorsgzb")
            .child("products")

        if (productId != null) {
            productId?.let { pId ->
                db.child(pId).get().addOnSuccessListener {
                    cProduct = it.getValue(Product::class.java)
                    Log.d(TAG, "onCreate: ${it.value}")
                    updateUI(cProduct)
                }
            }
        } else finish()

        mView.updateBtn.setOnClickListener { checkData() }
        mView.deleteBtn.setOnClickListener { showDeleteConfirmDialog() }
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
                            showUpdateConfirmDialog(name, model.toInt(), price.toInt())
                        } else toastS("Please enter proper price(Only Digits)")
                    } else toastS("Price cannot be empty")
                } else toastS("Please enter proper model number(Only Digits)")
            } else toastS("Model cannot be empty")
        } else toastS("Name cannot be empty")
    }

    private fun showUpdateConfirmDialog(name: String, model: Int, price: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Update Product")
            .setMessage("Are you sure you want to update product details?")
            .setPositiveButton("Yes") { _, _ ->
                addNewProduct(name, model, price, false)
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun addNewProduct(name: String, model: Int, price: Int, sold: Boolean) {
        val product = Product(name, model, price, sold)
        productId?.let {
            db.child(it).setValue(product)
                .addOnSuccessListener {
                    toastS("Product Updated")
                    finish()
                }
                .addOnFailureListener { toastS("Something went wrong") }
        }
    }

    private fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { _, _ ->
                deleteProduct()
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun deleteProduct() {
        if (productId != null) {
            db.child(productId!!).setValue(null)
            finish()
            toastS("Product deleted successfully")
        } else toastS("Something went wrong")
    }

    private fun toastS(txt: String) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(cProduct: Product?) {
        if (cProduct != null) {
            mView.nameEt.setText(cProduct.name)
            mView.modelEt.setText(cProduct.model.toString())
            mView.priceEt.setText(cProduct.price.toString())
        }
    }

    companion object {
        const val P_ID = "p_id"
    }
}