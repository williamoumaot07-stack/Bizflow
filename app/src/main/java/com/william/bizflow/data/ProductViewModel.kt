package com.william.bizflow.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.william.bizflow.models.Product
import com.william.bizflow.navigation.Routes

class ProductViewModel(var navController: NavController, var context: Context) {

    fun saveProduct(name: String, buyingPrice: String, sellingPrice: String, stockCount: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.LOGIN)
            return
        }

        if (name.isBlank() || buyingPrice.isBlank() || sellingPrice.isBlank() || stockCount.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val bp = buyingPrice.toDoubleOrNull()
        val sp = sellingPrice.toDoubleOrNull()
        val sc = stockCount.toIntOrNull()

        if (bp == null || sp == null || sc == null) {
            Toast.makeText(context, "Price and Stock must be valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (bp < 0 || sp < 0 || sc < 0) {
            Toast.makeText(context, "Values cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val productData = Product(name, buyingPrice, sellingPrice, stockCount, id)
        val productRef = FirebaseDatabase.getInstance().getReference().child("Products/$userId/$id")

        productRef.setValue(productData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.DASHBOARD)
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun viewProducts(product: MutableState<Product>, products: SnapshotStateList<Product>): SnapshotStateList<Product> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            navController.navigate(Routes.LOGIN)
            return products
        }

        val userId = currentUser.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Products/$userId")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (snap in snapshot.children) {
                    val value = snap.getValue(Product::class.java)
                    if (value != null) {
                        product.value = value
                        products.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        return products
    }

    fun deleteProduct(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Products/$userId/$id")
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateProduct(name: String, buyingPrice: String, sellingPrice: String, stockCount: String, id: String) {
        if (name.isBlank() || buyingPrice.isBlank() || sellingPrice.isBlank() || stockCount.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val bp = buyingPrice.toDoubleOrNull()
        val sp = sellingPrice.toDoubleOrNull()
        val sc = stockCount.toIntOrNull()

        if (bp == null || sp == null || sc == null) {
            Toast.makeText(context, "Price and Stock must be valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (bp < 0 || sp < 0 || sc < 0) {
            Toast.makeText(context, "Values cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val productData = Product(name, buyingPrice, sellingPrice, stockCount, id)
        val productRef = FirebaseDatabase.getInstance().getReference().child("Products/$userId/$id")

        productRef.setValue(productData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.VIEW_PRODUCTS)
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchProduct(id: String, name: MutableState<String>, buyingPrice: MutableState<String>, sellingPrice: MutableState<String>, stockCount: MutableState<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Products/$userId/$id")
        ref.get().addOnSuccessListener {
            val product = it.getValue(Product::class.java)
            if (product != null) {
                name.value = product.name
                buyingPrice.value = product.buyingPrice
                sellingPrice.value = product.sellingPrice
                stockCount.value = product.stockCount
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}
