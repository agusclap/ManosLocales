package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var selectedItem by remember { mutableStateOf(0) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo { fetchedUser ->
            user = fetchedUser
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                navController = navController
            )
        },
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.profileImageUrl),
                    contentDescription = stringResource(id = R.string.profile_pic_desc),
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.profile_name, it.nombre, it.apellido),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.profile_email, it.email),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.profile_phone, it.phone),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(40.dp))
            } ?: Text(stringResource(id = R.string.loading_user), color = Color.White)

            Button(
                onClick = { navController.navigate("editProfile") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_edit_profile), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("changePassword") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_change_password), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    userViewModel.logoutUser()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_logout), color = Color.Black)
            }
        }
    }
}
