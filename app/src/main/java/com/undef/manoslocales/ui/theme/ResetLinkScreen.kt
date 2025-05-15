package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.undef.manoslocales.R

@Composable
fun ResetLinkScreen(
    email: String,
    onBackToLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff3E2C1C))
            .padding(16.dp),
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

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Reset Link Sent",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xffFEFAE0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "We have sent a password reset link to:",
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Please check your email and follow the instructions to reset your password.",
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = { onBackToLoginClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xffFEFAE0),
                contentColor = Color(0xff3E2C1C)
            )
        ) {
            Text(text = "Back to Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetLinkScreenPreview() {
    ResetLinkScreen(
        email = "example@mail.com",
        onBackToLoginClick = {}
    )
}
