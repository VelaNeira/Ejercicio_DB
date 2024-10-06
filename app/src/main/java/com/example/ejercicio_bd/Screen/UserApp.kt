package com.example.ejercicio_bd.Screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ejercicio_bd.Model.User
import com.example.ejercicio_bd.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserApp(userRepository: UserRepository) {
    // Estado mutable para los campos de entrada
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) } // Usuario actualmente seleccionado
    var users by remember { mutableStateOf(listOf<User>()) } // Lista de usuarios
    var showError by remember { mutableStateOf(false) } // Estado para mostrar errores
    val scope = rememberCoroutineScope() // Alcance de corutina para lanzar tareas asíncronas
    val context = LocalContext.current // Contexto local para mostrar Toasts

    // Función para limpiar los campos de entrada
    fun clearFields() {
        nombre = ""
        apellido = ""
        edad = ""
        selectedUser = null
        showError = false
    }

    // LaunchedEffect para cargar usuarios al inicio
    LaunchedEffect(Unit) {
        scope.launch {
            users = withContext(Dispatchers.IO) {
                userRepository.getAllUsers() // Obtener todos los usuarios de forma asíncrona
            }
        }
    }

    // Crear un ScrollState para permitir desplazamiento
    val scrollState = rememberScrollState()

    // Usar un Column que sea scrollable
    Column(
        modifier = Modifier
            .fillMaxSize() // Llenar todo el espacio disponible
            .padding(16.dp) // Agregar padding
            .verticalScroll(scrollState), // Habilitar el desplazamiento vertical
        verticalArrangement = Arrangement.Top, // Organizar verticalmente desde arriba
        horizontalAlignment = Alignment.CenterHorizontally // Alinear horizontalmente al centro
    ) {
        Text(
            text = "Gestión de Usuarios", // Título de la aplicación
            style = MaterialTheme.typography.headlineMedium, // Estilo del texto
            color = Color(0xFF4A90E2), // Color del texto
            modifier = Modifier.padding(bottom = 24.dp) // Espaciado inferior
        )

        // Campo para el nombre
        UserInputField(
            value = nombre,
            label = "Nombre",
            showError = showError,
            onValueChange = { nombre = it } // Actualiza el nombre al cambiar
        )
        Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre campos

        // Campo para el apellido
        UserInputField(
            value = apellido,
            label = "Apellido",
            showError = showError,
            onValueChange = { apellido = it } // Actualiza el apellido al cambiar
        )
        Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre campos

        // Campo para la edad
        UserInputField(
            value = edad,
            label = "Edad",
            showError = showError,
            onValueChange = {
                // Validación para la entrada de edad
                if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() >= 0)) {
                    edad = it // Actualiza la edad si es válida
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number) // Teclado numérico
        )
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre campos

        // Botón para registrar o actualizar el usuario
        Button(
            onClick = {
                showError = true // Mostrar errores si hay problemas con la entrada
                if (nombre.isNotEmpty() && apellido.isNotEmpty() && edad.isNotEmpty() && edad.toIntOrNull() != null && edad.toInt() >= 0) {
                    val edadInt = edad.toIntOrNull() ?: 0 // Convertir edad a entero
                    val user = User(
                        id = selectedUser?.id ?: 0, // ID del usuario (0 si es nuevo)
                        nombre = nombre,
                        apellido = apellido,
                        edad = edadInt
                    )
                    scope.launch {
                        // Insertar o actualizar el usuario en la base de datos
                        if (selectedUser == null) {
                            withContext(Dispatchers.IO) {
                                userRepository.insertar(user) // Insertar nuevo usuario
                            }
                            Toast.makeText(context, "Usuario Registrado", Toast.LENGTH_SHORT).show() // Mensaje de éxito
                        } else {
                            withContext(Dispatchers.IO) {
                                userRepository.updateUser(user) // Actualizar usuario existente
                            }
                            Toast.makeText(context, "Usuario Actualizado", Toast.LENGTH_SHORT).show() // Mensaje de éxito
                        }

                        // Actualizar la lista de usuarios después de la inserción/actualización
                        users = withContext(Dispatchers.IO) {
                            userRepository.getAllUsers() // Obtener la lista actualizada
                        }
                        clearFields() // Limpiar los campos de entrada
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(), // Botón ocupa todo el ancho
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)), // Color del botón
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Elevación del botón
        ) {
            // Texto del botón dependiendo de si se está registrando o actualizando
            Text(text = if (selectedUser == null) "Registrar" else "Actualizar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre el botón y la lista

        // Lista de usuarios
        Column(modifier = Modifier.fillMaxWidth()) {
            users.forEach { user -> // Iterar sobre cada usuario
                UserCard(user,
                    onUpdate = { selectedUser = it;
                        nombre = it.nombre; // Llenar el campo de nombre
                        apellido = it.apellido; // Llenar el campo de apellido
                        edad = it.edad.toString() // Llenar el campo de edad
                    },
                    onDelete = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                userRepository.deleteById(user.id) // Eliminar usuario por ID
                            }
                            // Actualizar la lista de usuarios después de la eliminación
                            users = withContext(Dispatchers.IO) {
                                userRepository.getAllUsers() // Obtener la lista actualizada
                            }
                            Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show() // Mensaje de éxito
                        }
                    }
                ) // Mostrar tarjeta de usuario
                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre tarjetas
            }
        }
    }
}

// Componente de entrada de usuario
@Composable
fun UserInputField(value: String, label: String, showError: Boolean, onValueChange: (String) -> Unit, keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {
    TextField(
        value = value, // Valor actual del campo
        onValueChange = onValueChange, // Lógica para cambiar el valor
        label = { Text(text = label) }, // Etiqueta del campo
        modifier = Modifier.fillMaxWidth(), // Campo ocupa todo el ancho
        keyboardOptions = keyboardOptions, // Opciones del teclado
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE8F6FA), // Color de fondo cuando está enfocado
            unfocusedContainerColor = Color(0xFFE8F6FA), // Color de fondo cuando no está enfocado
            focusedIndicatorColor = if (showError && value.isEmpty()) Color.Red else Color(0xFF4A90E2), // Color del indicador enfocado
            unfocusedIndicatorColor = Color(0xFFBDBDBD) // Color del indicador no enfocado
        ),
        isError = showError && value.isEmpty() // Indicar error si el campo está vacío
    )
}

// Componente de tarjeta para mostrar cada usuario
@Composable
fun UserCard(user: User, onUpdate: (User) -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), // Tarjeta ocupa todo el ancho
        elevation = CardDefaults.cardElevation(8.dp), // Elevación de la tarjeta
        colors = CardDefaults.cardColors(containerColor = Color.White), // Color de la tarjeta
        shape = MaterialTheme.shapes.medium, // Forma de la tarjeta
        border = BorderStroke(1.dp, Color(0xFFBDBDBD)) // Borde de la tarjeta
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Columna dentro de la tarjeta
            Text(
                text = "${user.nombre} ${user.apellido}, ${user.edad} años", // Texto del usuario
                style = MaterialTheme.typography.bodyLarge, // Estilo del texto
                color = Color(0xFF333333) // Color del texto
            )
            Spacer(modifier = Modifier.height(4.dp)) // Espacio entre texto y botones

            Row(
                horizontalArrangement = Arrangement.SpaceBetween, // Espaciado horizontal entre botones
                modifier = Modifier.fillMaxWidth() // Fila ocupa todo el ancho
            ) {
                // Botón de Actualizar
                Button(
                    onClick = { onUpdate(user) }, // Lógica para actualizar el usuario
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Color del botón
                    modifier = Modifier.weight(1f).padding(end = 8.dp) // Peso y padding
                ) {
                    Text("Actualizar", color = Color.White) // Texto del botón
                }

                // Botón de Eliminar
                Button(
                    onClick = onDelete, // Lógica para eliminar el usuario
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Color del botón
                    modifier = Modifier.weight(1f) // Peso del botón
                ) {
                    Text("Eliminar", color = Color.White) // Texto del botón
                }
            }
        }
    }
}
