package com.devss.myshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.devss.myshop.EditProductActivity.Companion.P_ID
import com.devss.myshop.databinding.ActivityAllProductBinding
import com.google.firebase.database.*

private const val TAG = "AllProductActivity"
class AllProductActivity : AppCompatActivity() {

    private lateinit var mView: ActivityAllProductBinding
    private lateinit var db: DatabaseReference
    private lateinit var productAdapter: ArrayAdapter<String>
    private val productArray = arrayListOf<String>()
    private val productIdArray = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivityAllProductBinding.inflate(layoutInflater)
        setContentView(mView.root)

        db = FirebaseDatabase.getInstance().reference.child("users")
            .child("hsmotorsgzb")
            .child("products")

        productAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productArray)

//        db.get().addOnSuccessListener { products ->
//            products.children.forEach { product ->
//                val prod = product.getValue(Product::class.java)
//
//                productArray.add("${prod!!.name}(${prod.model}) \n- ₹${prod.price}")
//                productIdArray.add("${product.key}")
//            }
//            productAdapter.notifyDataSetChanged()
//        }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productArray.clear()
                productIdArray.clear()

                snapshot.children.forEach { product ->
                    val prod = product.getValue(Product::class.java)

                    productArray.add("${prod!!.name} (${prod.model}) \n- ₹${prod.price}")
                    productIdArray.add("${product.key}")
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

        mView.productLv.adapter = productAdapter
        
        mView.productLv.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra(P_ID, productIdArray[i])
            startActivity(intent)
        }
    }
}