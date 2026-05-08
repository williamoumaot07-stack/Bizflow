package com.william.bizflow.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.william.bizflow.models.Product
import com.william.bizflow.models.Sale
import com.william.bizflow.navigation.Routes

class SaleViewModel(var navController: NavController, var context: Context) {

    fun recordSale(
        product: Product,
        quantity: String,
        customerId: String = "",
        customerName: String = ""
    ) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            navController.navigate(Routes.LOGIN)
            return
        }

        val qtyInt = quantity.toIntOrNull() ?: 0
        val stockInt = product.stockCount.toIntOrNull() ?: 0

        if (qtyInt <= 0) {
            Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        if (qtyInt > stockInt) {
            Toast.makeText(context, "Insufficient stock", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val buyingPrice = product.buyingPrice.toDoubleOrNull() ?: 0.0
        val sellingPrice = product.sellingPrice.toDoubleOrNull() ?: 0.0
        val totalAmount = sellingPrice * qtyInt
        val totalBuying = buyingPrice * qtyInt
        val profit = totalAmount - totalBuying

        val saleData = Sale(
            id = id,
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            buyingPrice = product.buyingPrice,
            sellingPrice = product.sellingPrice,
            totalAmount = totalAmount.toString(),
            profit = profit.toString(),
            customerId = customerId,
            customerName = customerName,
            timestamp = System.currentTimeMillis()
        )

        val database = FirebaseDatabase.getInstance()
        val saleRef = database.getReference("Sales/$userId/$id")
        val productRef = database.getReference("Products/$userId/${product.id}/stockCount")

        saleRef.setValue(saleData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newStock = (stockInt - qtyInt).toString()
                productRef.setValue(newStock)
                Toast.makeText(context, "Sale recorded successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.DASHBOARD)
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun viewSales(sales: SnapshotStateList<Sale>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            navController.navigate(Routes.LOGIN)
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Sales/$userId")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sales.clear()
                for (snap in snapshot.children) {
                    val sale = snap.getValue(Sale::class.java)
                    if (sale != null) {
                        sales.add(sale)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun clearSalesHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            navController.navigate(Routes.LOGIN)
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Sales/$userId")
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Sales history cleared", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
