Armario de Sombras

Una aplicación móvil Android para gestionar un “mini e-commerce” de estilo dark/rock.


Descripción

“Armario de Sombras” es una app que permite al usuario:

-Visualizar una galería de productos (moda alternativa: poleras, faldas, calzas, accesorios)
-Filtrar productos por categoría
-Agregar productos a un carrito de compras
-Simular un flujo de compra
-Iniciar sesión y registrar usuario (con roles “cliente” y “admin”)
-Para el rol administrador: acceder a funciones especiales (por ejemplo panel de administración)
-Guardar localmente el estado del carrito y los usuarios mediante DataStore

Integrantes
Javiera Curin
Catalina Pino

Tecnologías utilizadas

Kotlin
Jetpack Compose para la interfaz de usuario
DataStore de Android para persistencia local
Navegación con NavHostController
Android Studio como IDE de desarrollo

Funcionalidades implementadas

-Registro e inicio de sesión de usuario con rol (cliente/admin)
-Visualización de catálogo completo y filtrado por categoría
-Carrito de compras: añadir productos, ver contador dinámico, persistencia local
-Flujo de compra simulado
-Panel de administración: creación automática del usuario admin en primer inicio
-UI con tema oscuro, estilo coherente.
-Visualizacion a mi perfil (para usuario)
-Historial de compras totales(administrador)

Próximas mejoras

-Integrar pasarela de pago real o simulada con más detalle (por ejemplo, validación de tarjeta)
-Historial de compras para cada usuario
-Mejorar componente de filtrado y búsqueda
-Añadir test unitarios
