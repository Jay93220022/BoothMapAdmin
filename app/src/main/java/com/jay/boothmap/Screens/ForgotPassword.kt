package com.jay.boothmap.Screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.boothmap.R
import com.jay.boothmap.Viewmodels.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(viewModel: AuthViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current




    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopCenter,
            modifier = Modifier.padding(start = 25.dp,top=50.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp).offset(y=(-30).dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter Your Email", fontWeight = FontWeight.Bold) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor =Color(0xFFF26522),
                    focusedLabelColor = Color(0xFFF26522),
                    cursorColor = eciGreen
                ),
                modifier =   Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(text = message, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if(email.isNotEmpty()){
                        viewModel.resetPassword(email, {
                            message = "Reset link sent to $email"
                        }, {
                            message = it
                        })
                    }else{
                        Toast.makeText(context,"Please Enter Email", Toast.LENGTH_SHORT).show()
                    }
                },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006838),
                    contentColor = Color.White
                )
            ) {
                Text("Reset Password")
            }
        }
    }
}