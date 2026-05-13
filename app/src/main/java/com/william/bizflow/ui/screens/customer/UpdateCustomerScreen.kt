package com.william.bizflow.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.william.bizflow.data.CustomerViewModel
import com.william.bizflow.models.Customer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCustomerScreen(navController: NavController, customerId: String) {
    var customerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val context = LocalContext.current
    val customerViewModel = CustomerViewModel(navController, context)

    val nameState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val locationState = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        customerViewModel.fetchCustomer(customerId, nameState, phoneState, locationState)
    }

    // Sync local state only once when data is fetched
    LaunchedEffect(nameState.value) { if(nameState.value.isNotEmpty()) customerName = nameState.value }
    LaunchedEffect(phoneState.value) { if(phoneState.value.isNotEmpty()) phoneNumber = phoneState.value }
    LaunchedEffect(locationState.value) { if(locationState.value.isNotEmpty()) location = locationState.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Customer", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
        Text("Update Customer", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name", fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1A237E),
                unfocusedLabelColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF1A237E)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number", fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1A237E),
                unfocusedLabelColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF1A237E)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location", fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1A237E),
                unfocusedLabelColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF1A237E)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                customerViewModel.updateCustomer(customerName, phoneNumber, location, customerId)
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Update Customer", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}
}
