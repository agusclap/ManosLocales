package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.R

@Composable
fun SettingScreen() {
    var notificationEnabled by remember { mutableStateOf(true) }
    var loginEnabled by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        },
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->   // <-- Este paddingValues viene del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
                .padding(paddingValues)  // <-- Se aplica aquí
                .padding(16.dp),          // <-- Este es el padding adicional
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .width(180.dp)
            )
            Text(
                text = "Settings", style = MaterialTheme.typography.headlineMedium,
                color = Color(0xffFEFAE0),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingItem(title = "Language", value = "English", actionText = "Change")
            SettingItem(title = "Password", value = "*********", actionText = "Change")
            SettingItem(title = "Location", value = "Cordoba, Argentina", actionText = "Change")

            Spacer(modifier = Modifier.height(16.dp))

            SwitchItem("Receive notifications", notificationEnabled) {
                notificationEnabled = it
            }
            SwitchItem("Log in automatically", loginEnabled) {
                loginEnabled = it
            }
        }
    }
}


@Composable
fun SettingItem(title: String, value: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = Color.Gray, fontSize = 14.sp)
            Text(text = value, color = Color.White, fontSize = 16.sp)
        }
        Text(text = actionText, color = Color(0xffFEFAE0), fontSize = 14.sp)
    }
}


@Composable
fun SwitchItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 16.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xffFEFAE0),
                uncheckedThumbColor = Color.Gray
            )
        )

    }
}


@Preview(showBackground = true)
@Composable
fun SettingScreenPrewiew() {
    SettingScreen()
}