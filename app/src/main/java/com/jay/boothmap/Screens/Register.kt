package com.jay.boothmap.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.R
import com.jay.boothmap.Viewmodels.AuthViewModel

val eciOrange = Color(0xFFF26522)
val eciGreen = Color(0xFF017A3E)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    viewModel: AuthViewModel,
    navController: NavController,

) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisible1 by remember { mutableStateOf(false) }

    val context = LocalContext.current




        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopCenter,
                modifier = Modifier.padding(start = 25.dp,top=30.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter Your Email", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor =Color(0xFFF26522),
                        focusedLabelColor = Color(0xFFF26522),
                        cursorColor = eciGreen
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter Password", fontWeight = FontWeight.Bold) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor =Color(0xFFF26522),
                        focusedLabelColor = Color(0xFFF26522),
                        cursorColor = eciGreen
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", fontWeight = FontWeight.Bold) },
                    visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    trailingIcon = {
                        val iconResId = if (passwordVisible1) {
                            R.drawable.img_visible
                        } else {
                            R.drawable.img_invisible
                        }

                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = if (passwordVisible1) "Hide password" else "Show password",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { passwordVisible1 = !passwordVisible1 },

                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor =Color(0xFFF26522),
                        focusedLabelColor = Color(0xFFF26522),
                        cursorColor = eciGreen
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        when {
                            email.isBlank() -> {
                                Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show()
                            }

                            password.isBlank() -> {
                                Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show()
                            }

                            confirmPassword.isBlank() -> {
                                Toast.makeText(context, "Please Enter Details", Toast.LENGTH_SHORT).show()
                            }

                            password != confirmPassword -> {
                                Toast.makeText(context, "Please Confirm the Password", Toast.LENGTH_SHORT).show()
                            }

                            else -> {
                                viewModel.register(email, password, {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                }, {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                })
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006838),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }

