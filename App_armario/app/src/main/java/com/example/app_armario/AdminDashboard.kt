package com.example.app_armario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_armario.Models.*
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.Repositories.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val productoRepo = remember { ProductoRepository(context) }
    val usuarioRepo = remember { UsuarioRepository() }

    // Datos en memoria
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var usuarios by remember { mutableStateOf(listOf<Usuario>()) }

    // Cargar al entrar
    LaunchedEffect(Unit) {
        productos = productoRepo.getProductos()
        usuarios  = usuarioRepo.getUsuarios()
    }

    // Helpers de refresco (ahora lanzan corrutinas)
    fun refreshProductos() { 
        scope.launch { 
            productos = productoRepo.getProductos() 
        } 
    }
    fun refreshUsuarios() { 
        scope.launch { 
            usuarios = usuarioRepo.getUsuarios() 
        } 
    }

    // Estado de UI
    var tabIndex by remember { mutableStateOf(0) } // 0 = Productos, 1 = Usuarios

    // Dialogs Producto
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProducto by remember { mutableStateOf<Producto?>(null) }

    // Dialogs Usuario
    var showUserDialog by remember { mutableStateOf(false) }
    var editingUsuario by remember { mutableStateOf<Usuario?>(null) }

    // Confirmaciones
    var confirmDeleteProduct by remember { mutableStateOf<Producto?>(null) }
    var confirmDeleteUser by remember { mutableStateOf<Usuario?>(null) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (tabIndex == 0) { editingProducto = null; showProductDialog = true }
                    else { editingUsuario = null; showUserDialog = true }
                },
                containerColor = Color(0xFFB32DD4),
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Agregar") }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Black,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex]),
                        color = Color(0xFFB32DD4)
                    )
                }
            ) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Productos") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Usuarios") })
            }

            Divider(color = Color(0xFFB32DD4))

            if (tabIndex == 0) {
                // ============ LISTA DE PRODUCTOS ============
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(productos, key = { it.id }) { p ->
                        AdminItemCard(
                            title = p.nombre,
                            subtitle = "Precio: $${p.precio} • Stock: ${p.stock} • ${p.categoria.nombre}",
                            onEdit = { editingProducto = p; showProductDialog = true },
                            onDelete = { confirmDeleteProduct = p }
                        )
                    }
                }
            } else {
                // ============ LISTA DE USUARIOS ============
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(usuarios, key = { it.id }) { u ->
                        AdminItemCard(
                            title = u.nombre,
                            subtitle = "${u.email} • Rol: ${u.rol.nombre.uppercase()}",
                            onEdit = { editingUsuario = u; showUserDialog = true },
                            onDelete = { confirmDeleteUser = u }
                        )
                    }
                }
            }
        }
    }

    // ----------------- DIALOGS -----------------

    // Crear / Editar Producto
    if (showProductDialog) {
        ProductFormDialog(
            initial = editingProducto,
            onDismiss = { showProductDialog = false },
            onSave = { form ->
                scope.launch {
                    if (editingProducto == null) {
                        // Crear
                        productoRepo.agregarProducto(form)
                    } else {
                        // Editar (mantener id)
                        productoRepo.editarProducto(form.copy(id = editingProducto!!.id))
                    }
                    showProductDialog = false
                    refreshProductos()
                }
            }
        )
    }

    // Crear / Editar Usuario
    if (showUserDialog) {
        UserFormDialog(
            initial = editingUsuario,
            onDismiss = { showUserDialog = false },
            onSave = { form ->
                scope.launch {
                    if (editingUsuario == null) {
                        usuarioRepo.agregarUsuario(form)
                    } else {
                        usuarioRepo.editarUsuario(form.copy(id = editingUsuario!!.id))
                    }
                    showUserDialog = false
                    refreshUsuarios()
                }
            }
        )
    }

    // Confirmación eliminar producto
    confirmDeleteProduct?.let { p ->
        ConfirmDialog(
            title = "Eliminar producto",
            message = "¿Seguro que quieres eliminar \"${p.nombre}\"?",
            onDismiss = { confirmDeleteProduct = null },
            onConfirm = {
                scope.launch {
                    productoRepo.eliminarProducto(p.id)
                    confirmDeleteProduct = null
                    refreshProductos()
                }
            }
        )
    }

    // Confirmación eliminar usuario
    confirmDeleteUser?.let { u ->
        ConfirmDialog(
            title = "Eliminar usuario",
            message = "¿Seguro que quieres eliminar a \"${u.nombre}\"?",
            onDismiss = { confirmDeleteUser = null },
            onConfirm = {
                scope.launch {
                    usuarioRepo.eliminarUsuario(u.id)
                    confirmDeleteUser = null
                    refreshUsuarios()
                }
            }
        )
    }
}

/* ============================================================
 * Reusable item card (fila con Editar / Eliminar)
 * ============================================================ */
@Composable
private fun AdminItemCard(
    title: String,
    subtitle: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFFB32DD4))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

/* ============================================================
 * Confirmación genérica
 * ============================================================ */
@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            }
        },
        title = { Text(title, color = Color.White) },
        text = { Text(message, color = Color.White) },
        containerColor = Color(0xFF121212)
    )
}

/* ============================================================
 * Formulario de Producto (crear/editar)
 * ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormDialog(
    initial: Producto?,
    onDismiss: () -> Unit,
    onSave: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(initial?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(initial?.descripcion ?: "") }
    var precioStr by remember { mutableStateOf(if (initial != null) initial.precio.toString() else "") }
    var stockStr by remember { mutableStateOf(if (initial != null) initial.stock.toString() else "") }
    var imagenUrl by remember { mutableStateOf(initial?.imagenUrl ?: "") }
    var categoria by remember { mutableStateOf(initial?.categoria ?: Categoria.POLERAS) }

    var expandCat by remember { mutableStateOf(false) }
    val purple = Color(0xFFB32DD4)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val precio = precioStr.toLongOrNull() ?: 0L
                        val stock  = stockStr.toIntOrNull() ?: 0
                        if (nombre.isNotBlank() && precio > 0 && stock >= 0) {
                            onSave(
                                Producto(
                                    id = initial?.id ?: "", // En creación se generará un ID nuevo, en edición se usa el existente
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.trim(),
                                    precio = precio,
                                    stock = stock,
                                    imagenUrl = if (imagenUrl.isBlank()) null else imagenUrl.trim(),
                                    activo = true,
                                    categoria = categoria
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purple)
                ) { Text(if (initial == null) "Crear" else "Guardar", color = Color.White) }
            }
        },
        title = { Text(if (initial == null) "Nuevo producto" else "Editar producto", color = Color.White) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    label = { Text("Descripción", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = precioStr, onValueChange = { precioStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Precio (entero)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = stockStr, onValueChange = { stockStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Stock", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = imagenUrl, onValueChange = { imagenUrl = it },
                    label = { Text("Imagen URL (opcional)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))

                // Selector de categoría
                ExposedDropdownMenuBox(
                    expanded = expandCat,
                    onExpandedChange = { expandCat = it }
                ) {
                    OutlinedTextField(
                        value = categoria.nombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría", color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandCat) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(expanded = expandCat, onDismissRequest = { expandCat = false }) {
                        Categoria.TODAS.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.nombre) },
                                onClick = { categoria = c; expandCat = false }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF121212)
    )
}

/* ============================================================
 * Formulario de Usuario (crear/editar)
 * ============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserFormDialog(
    initial: Usuario?,
    onDismiss: () -> Unit,
    onSave: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf(initial?.nombre ?: "") }
    var email by remember { mutableStateOf(initial?.email ?: "") }
    var password by remember { mutableStateOf(initial?.password ?: "") }
    var telefono by remember { mutableStateOf(initial?.telefono ?: "") }
    var rol by remember { mutableStateOf(initial?.rol ?: RolesPredefinidos.CLIENTE) }

    var expandRol by remember { mutableStateOf(false) }
    val purple = Color(0xFFB32DD4)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            onSave(
                                Usuario(
                                    id = initial?.id ?: "", // En creación se generará ID, en edición se mantiene
                                    nombre = nombre.trim(),
                                    email = email.trim(),
                                    password = password,
                                    telefono = telefono.ifBlank { null },
                                    rol = rol
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purple)
                ) { Text(if (initial == null) "Crear" else "Guardar", color = Color.White) }
            }
        },
        title = { Text(if (initial == null) "Nuevo usuario" else "Editar usuario", color = Color.White) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it },
                    label = { Text("Teléfono (opcional)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(8.dp))

                // Selector de Rol
                ExposedDropdownMenuBox(
                    expanded = expandRol,
                    onExpandedChange = { expandRol = it }
                ) {
                    OutlinedTextField(
                        value = rol.nombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol", color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandRol) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(expanded = expandRol, onDismissRequest = { expandRol = false }) {
                        listOf(RolesPredefinidos.ADMIN, RolesPredefinidos.CLIENTE).forEach { r ->
                            DropdownMenuItem(text = { Text(r.nombre.uppercase()) }, onClick = {
                                rol = r; expandRol = false
                            })
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF121212)
    )
}

/* ============================================================
 * Helpers de colores para inputs en modo oscuro
 * ============================================================ */
@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFFB32DD4),
    unfocusedBorderColor = Color(0xFF9C27B0),
    cursorColor = Color(0xFFB32DD4)
)
