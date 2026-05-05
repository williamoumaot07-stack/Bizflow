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
import com.william.bizflow.models.Customer
import com.william.bizflow.navigation.Routes

class CustomerViewModel(var navController: NavController, var context: Context) {

    fun saveCustomer(name: String, phone: String, location: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.LOGIN)
            return
        }

        if (name.isBlank() || phone.isBlank() || location.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val customerData = Customer(name, phone, location, id)
        val customerRef = FirebaseDatabase.getInstance().getReference().child("Customers/$userId/$id")

        customerRef.setValue(customerData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Customer saved successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.CUSTOMER)
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun viewCustomers(customer: MutableState<Customer>, customers: SnapshotStateList<Customer>): SnapshotStateList<Customer> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            navController.navigate(Routes.LOGIN)
            return customers
        }

        val userId = currentUser.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Customers/$userId")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                customers.clear()
                for (snap in snapshot.children) {
                    val value = snap.getValue(Customer::class.java)
                    if (value != null) {
                        customer.value = value
                        customers.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        return customers
    }

    fun deleteCustomer(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Customers/$userId/$id")
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Customer deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateCustomer(name: String, phone: String, location: String, id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val customerData = Customer(name, phone, location, id)
        val customerRef = FirebaseDatabase.getInstance().getReference().child("Customers/$userId/$id")

        customerRef.setValue(customerData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Customer updated", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.CUSTOMER)
            } else {
                Toast.makeText(context, "ERROR: " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchCustomer(id: String, name: MutableState<String>, phone: MutableState<String>, location: MutableState<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference().child("Customers/$userId/$id")
        ref.get().addOnSuccessListener {
            val customer = it.getValue(Customer::class.java)
            if (customer != null) {
                name.value = customer.name
                phone.value = customer.phone
                location.value = customer.location
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}
