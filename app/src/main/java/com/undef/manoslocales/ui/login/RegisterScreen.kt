package com.undef.manoslocales.ui.login

import android.Manifest
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.*

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
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val countryCodes = listOf("+54", "+55", "+56", "+57", "+58", "+51", "+598")
    val categorias = listOf("Tecnología", "Herramientas", "Alimentos")
    val provincias = listOf(
        "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba", 
        "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", 
        "Mendoza", "Misiones", "Neuquén", "Río Negro", "Salta", "San Juan", 
        "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", 
        "Tierra del Fuego", "Tucumán"
    )

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
            .background(CafeOscuro)
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
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Crear Cuenta",
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
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
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
                        label = { Text("Cód.") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryCodeExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                        colors = registerTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = countryCodeExpanded,
                        onDismissRequest = { countryCodeExpanded = false },
                        modifier = Modifier.background(Crema)
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code, color = Cafe) },
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
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = registerTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Provincia (ExposedDropdownMenuBox)
            ExposedDropdownMenuBox(
                expanded = provinciaExpanded && !isLoading,
                onExpandedChange = { if (!isLoading) provinciaExpanded = !provinciaExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProvincia,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provincia") },
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
                    modifier = Modifier.background(Crema)
                ) {
                    provincias.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov, color = Cafe) },
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
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = registerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = registerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Rol
            Text("Quiero unirme como:", color = Crema, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = role == "user",
                    onClick = { if (!isLoading) role = "user" },
                    colors = RadioButtonDefaults.colors(selectedColor = Crema, unselectedColor = Crema.copy(alpha = 0.6f)),
                    enabled = !isLoading
                )
                Text("Usuario", color = Crema)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = role == "provider",
                    onClick = { if (!isLoading) role = "provider" },
                    colors = RadioButtonDefaults.colors(selectedColor = Crema, unselectedColor = Crema.copy(alpha = 0.6f)),
                    enabled = !isLoading
                )
                Text("Proveedor", color = Crema)
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
                        label = { Text("Categoría de Emprendimiento") },
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
                        modifier = Modifier.background(Crema)
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Cafe) },
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
                    // Validaciones de Contraseña
                    val hasUpperCase = password.any { it.isUpperCase() }
                    val hasDigit = password.any { it.isDigit() }
                    
                    if (!email.contains("@")) {
                        Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    } else if (!hasUpperCase) {
                        Toast.makeText(context, "La contraseña debe incluir al menos una mayúscula", Toast.LENGTH_SHORT).show()
                    } else if (!hasDigit) {
                        Toast.makeText(context, "La contraseña debe incluir al menos un número", Toast.LENGTH_SHORT).show()
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
                                ciudad = selectedProvincia, // Se usa la provincia seleccionada
                                lat = if (role == "provider") la else null,
                                lng = if (role == "provider") lo else null
                            ) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    onRegisterSuccess(email, uid)
                                } else {
                                    // Manejo de colisión de email
                                    val exception = FirebaseAuth.getInstance().currentUser // Esto es un hack, mejor capturar en el VM o pasar el error
                                    // Pero basándonos en el mensaje de Firebase o el tipo:
                                    if (message?.contains("already in use", ignoreCase = true) == true) {
                                        Toast.makeText(context, "Este correo ya está registrado. Intenta iniciar sesión.", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, message ?: "Error al registrar", Toast.LENGTH_LONG).show()
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
                    Text("REGISTRARSE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Ya tenés cuenta? Iniciá sesión",
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
