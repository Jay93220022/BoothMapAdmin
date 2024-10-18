package com.jay.boothmap.Screens


import android.transition.Scene
import android.widget.Toast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.R
import com.jay.boothmap.Viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    authViewModel: AuthViewModel,
    navController: NavController,


    ) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val eciOrange = Color(0xFFF26522)
    val eciGreen = Color(0xFF017A3E)

        Column(Modifier.fillMaxSize().background(Color.White)) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(start = 25.dp,top=30.dp)
            )



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Enter Your Email",
                            fontWeight = FontWeight.Bold,

                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor =Color(0xFFF26522),
                        focusedLabelColor = Color(0xFFF26522),
                        cursorColor = eciGreen
                    ), singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Enter Password",
                            fontWeight = FontWeight.Bold,

                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor =Color(0xFFF26522),
                        focusedLabelColor = Color(0xFFF26522),
                        cursorColor = eciGreen
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val iconResId = if (passwordVisible) {
                            R.drawable.img_visible
                        } else {
                            R.drawable.img_invisible
                        }

                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { passwordVisible = !passwordVisible },

                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Only one error message display block
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

//                if (preferenceHelper.isLoggedIn()) {
//
//                    navController.navigate(Screen.Dashboard.route) {
//                        popUpTo(Screen.Login.route) { inclusive = true }
//                    }
//                }
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            authViewModel.login(email, password, {
                                //  preferenceHelper.setLoggedIn(true)

                                navController.navigate(Screen.ListScreen.route) {
                                    // popUpTo(Screen.Login.route) { inclusive = true }

                                }
                            }, {
                                errorMessage = it
                            })
                        } else {
                            if (email.isEmpty() && password.isEmpty()) {
                                Toast.makeText(context, "Please Enter Deatails", Toast.LENGTH_SHORT).show()
                            } else if (email.isEmpty()) {
                                Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = eciGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    navController.navigate(Screen.Register.route)
                }) {
                    Text("Don't have Account ? Register",color= EciOrange)
                }

                TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                    Text("Forgot Password?", color = EciOrange)
                }
            }
        }
    }
