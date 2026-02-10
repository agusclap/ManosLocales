package com.undef.manoslocales.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    onRegisterSuccess: (String, String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numerotel by remember { mutableStateOf("") }
    
    var selectedCountryCode by remember { mutableStateOf("+54") }
    var countryCodeExpanded by remember { mutableStateOf(false) }
    
    var selectedProvincia by remember { mutableStateOf("") }
    var provinciaExpanded by remember { mutableStateOf(false) }
    
    var role by remember { mutableStateOf("user") }
    var categoria by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Misión: Reparación de Selectores - Carga de arrays desde strings.xml
    val countryCodes = stringArrayResource(R.array.country_codes).toList()
    val categorias = stringArrayResource(R.array.categorias).toList()
    val provincias = stringArrayResource(R.array.provincias).toList()

    // Strings para Toasts y Errores
    val errorInvalidEmail = stringResource(id = R.string.error_invalid_email)
    val errorPasswordLength = stringResource(id = R.string.error_password_length)
    val errorPasswordUpperCase = stringResource(id = R.string.error_password_uppercase)
    val errorPasswordDigit = stringResource(id = R.string.error_password_digit)
    val errorEmailInUse = stringResource(id = R.string.error_email_in_use)
    val errorRegisterGeneric = stringResource(id = R.string.error_register_generic)

    val isFormValid = password.isNotBlank() && email.isNotBlank() && numerotel.isNotBlank() && nombre.isNotBlank() && selectedProvincia.isNotBlank()

    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (locationPermission.status.isGranted && role == "provider") {
                    val fused = LocationServices.getFusedLocationProviderClient(context)
                    fused.lastLocation.addOnSuccessListener { loc ->
                        lat = loc?.latitude
                        lng = loc?.longitude
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3E2C1C)) // Fondo corregido
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = stringResource(id = R.string.app_logo_desc),
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Crema,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre y Apellido Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(id = R.string.name_label)) },
                    modifier = Modifier.weight(1f),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text(stringResource(id = R.string.lastname_label)) },
                    modifier = Modifier.weight(1f),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teléfono con Selector de Código
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = countryCodeExpanded && !isLoading,
                    onExpandedChange = { if (!isLoading) countryCodeExpanded = !countryCodeExpanded },
                    modifier = Modifier.width(100.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.country_code_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryCodeExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                        colors = registerTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = countryCodeExpanded,
                        onDismissRequest = { countryCodeExpanded = false },
                        modifier = Modifier.background(Crema) // Fondo Crema
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code, color = Cafe) }, // Texto Café
                                onClick = {
                                    selectedCountryCode = code
                                    countryCodeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = numerotel,
                    onValueChange = { if (it.all { char -> char.isDigit() }) numerotel = it },
                    label = { Text(stringResource(id = R.string.phone_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Provincia
            ExposedDropdownMenuBox(
                expanded = provinciaExpanded && !isLoading,
                onExpandedChange = { if (!isLoading) provinciaExpanded = !provinciaExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProvincia,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.province_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinciaExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = provinciaExpanded,
                    onDismissRequest = { provinciaExpanded = false },
                    modifier = Modifier.background(Crema) // Fondo Crema
                ) {
                    provincias.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov, color = Cafe) }, // Texto Café
                            onClick = {
                                selectedProvincia = prov
                                provinciaExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                colors = registerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = registerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Rol
            Text(stringResource(id = R.string.role_selection_title), color = Crema, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = role == "user",
                    onClick = { if (!isLoading) role = "user" },
                    colors = RadioButtonDefaults.colors(selectedColor = Crema, unselectedColor = Crema.copy(alpha = 0.6f)),
                    enabled = !isLoading
                )
                Text(stringResource(id = R.string.role_user), color = Crema)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = role == "provider",
                    onClick = { if (!isLoading) role = "provider" },
                    colors = RadioButtonDefaults.colors(selectedColor = Crema, unselectedColor = Crema.copy(alpha = 0.6f)),
                    enabled = !isLoading
                )
                Text(stringResource(id = R.string.role_provider), color = Crema)
            }

            if (role == "provider") {
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedCategory && !isLoading,
                    onExpandedChange = { if (!isLoading) expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.provider_category_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                        colors = registerTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.background(Crema) // Fondo Crema
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Cafe) }, // Texto Café
                                onClick = {
                                    categoria = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val hasUpperCase = password.any { it.isUpperCase() }
                    val hasDigit = password.any { it.isDigit() }
                    
                    if (!email.contains("@")) {
                        Toast.makeText(context, errorInvalidEmail, Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(context, errorPasswordLength, Toast.LENGTH_SHORT).show()
                    } else if (!hasUpperCase) {
                        Toast.makeText(context, errorPasswordUpperCase, Toast.LENGTH_SHORT).show()
                    } else if (!hasDigit) {
                        Toast.makeText(context, errorPasswordDigit, Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        val fullPhone = "$selectedCountryCode$numerotel"
                        val registerAction: (Double?, Double?) -> Unit = { la, lo ->
                            viewModel.registerUserWithVerification(
                                email = email,
                                password = password,
                                nombre = nombre,
                                apellido = apellido,
                                role = role,
                                phone = fullPhone,
                                categoria = if (role == "provider") categoria else null,
                                ciudad = selectedProvincia,
                                lat = if (role == "provider") la else null,
                                lng = if (role == "provider") lo else null
                            ) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    onRegisterSuccess(email, uid)
                                } else {
                                    if (message?.contains("already in use", ignoreCase = true) == true) {
                                        Toast.makeText(context, errorEmailInUse, Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, message ?: errorRegisterGeneric, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }

                        if (role == "provider" && locationPermission.status.isGranted) {
                            val fused = LocationServices.getFusedLocationProviderClient(context)
                            fused.lastLocation.addOnSuccessListener { loc ->
                                if (loc != null) {
                                    registerAction(loc.latitude, loc.longitude)
                                } else {
                                    val token = CancellationTokenSource()
                                    fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
                                        .addOnSuccessListener { cLoc ->
                                            registerAction(cLoc?.latitude, cLoc?.longitude)
                                        }
                                        .addOnFailureListener { 
                                            isLoading = false
                                            registerAction(null, null) 
                                        }
                                }
                            }.addOnFailureListener { 
                                isLoading = false
                                registerAction(null, null) 
                            }
                        } else if (role == "provider" && !locationPermission.status.isGranted) {
                            isLoading = false
                            locationPermission.launchPermissionRequest()
                        } else {
                            registerAction(null, null)
                        }
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crema,
                    contentColor = Cafe
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Cafe, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = stringResource(id = R.string.btn_register), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        color = Cafe
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.already_have_account),
                color = Crema,
                modifier = Modifier.clickable(enabled = !isLoading) { onLoginClick() },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Crema,
    unfocusedTextColor = Crema,
    focusedBorderColor = Crema,
    unfocusedBorderColor = Crema.copy(alpha = 0.5f),
    focusedLabelColor = Crema,
    unfocusedLabelColor = Crema.copy(alpha = 0.7f),
    cursorColor = Crema,
    disabledTextColor = Crema.copy(alpha = 0.5f),
    disabledBorderColor = Crema.copy(alpha = 0.3f),
    disabledLabelColor = Crema.copy(alpha = 0.5f)
)
