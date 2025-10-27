package com.example.app_armario.data


object RegionesChile {

    val REGIONES: Map<String, List<String>> = mapOf(
        // I - Región de Arica y Parinacota
        "Región de Arica y Parinacota" to listOf(
            "Arica", "Camarones", "General Lagos", "Putre"
        ),

        // II - Región de Tarapacá
        "Región de Tarapacá" to listOf(
            "Iquique", "Alto Hospicio",
            "Pozo Almonte", "Camiña", "Colchane", "Huara", "Pica"
        ),

        // III - Región de Antofagasta
        "Región de Antofagasta" to listOf(
            "Antofagasta", "Mejillones", "Sierra Gorda", "Taltal",
            "Calama", "Ollagüe", "San Pedro de Atacama",
            "Tocopilla", "María Elena"
        ),

        // IV - Región de Atacama
        "Región de Atacama" to listOf(
            "Copiapó", "Caldera", "Tierra Amarilla",
            "Chañaral", "Diego de Almagro",
            "Vallenar", "Alto del Carmen", "Freirina", "Huasco"
        ),

        // V - Región de Coquimbo
        "Región de Coquimbo" to listOf(
            "La Serena", "Coquimbo", "Andacollo", "La Higuera", "Paihuano", "Vicuña",
            "Ovalle", "Combarbalá", "Monte Patria", "Punitaqui", "Río Hurtado",
            "Illapel", "Canela", "Los Vilos", "Salamanca"
        ),

        // VI - Región de Valparaíso
        "Región de Valparaíso" to listOf(
            // Valparaíso
            "Valparaíso", "Casablanca", "Concón", "Juan Fernández", "Puchuncaví", "Quintero", "Viña del Mar",
            // Quillota
            "Quillota", "Calera", "Hijuelas", "La Cruz", "Nogales",
            // San Antonio
            "San Antonio", "Algarrobo", "Cartagena", "El Quisco", "El Tabo", "Santo Domingo",
            // San Felipe de Aconcagua
            "San Felipe", "Catemu", "Llaillay", "Panquehue", "Putaendo", "Santa María",
            // Los Andes
            "Los Andes", "Calle Larga", "Rinconada", "San Esteban",
            // Petorca
            "La Ligua", "Cabildo", "Papudo", "Petorca", "Zapallar",
            // Marga Marga
            "Quilpué", "Limache", "Olmué", "Villa Alemana",
            // Insular
            "Isla de Pascua"
        ),

        // VII - Región Metropolitana de Santiago (COMPLETA)
        "Región Metropolitana de Santiago" to listOf(
            // Provincia de Santiago
            "Santiago", "Cerrillos", "Cerro Navia", "Conchalí", "El Bosque", "Estación Central",
            "Huechuraba", "Independencia", "La Cisterna", "La Florida", "La Granja",
            "La Pintana", "La Reina", "Las Condes", "Lo Barnechea", "Lo Espejo",
            "Lo Prado", "Macul", "Maipú", "Ñuñoa", "Pedro Aguirre Cerda", "Peñalolén",
            "Providencia", "Pudahuel", "Quilicura", "Quinta Normal", "Recoleta", "Renca",
            "San Joaquín", "San Miguel", "San Ramón", "Vitacura",
            // Cordillera
            "Puente Alto", "Pirque", "San José de Maipo",
            // Maipo
            "San Bernardo", "Buin", "Calera de Tango", "Paine",
            // Melipilla
            "Melipilla", "Alhué", "Curacaví", "María Pinto", "San Pedro",
            // Talagante
            "Talagante", "El Monte", "Isla de Maipo", "Padre Hurtado", "Peñaflor"
        ),

        // VIII - Región del Libertador General Bernardo O’Higgins
        "Región del Libertador General Bernardo O’Higgins" to listOf(
            // Cachapoal
            "Rancagua", "Codegua", "Coinco", "Coltauco", "Doñihue", "Graneros",
            "Las Cabras", "Machalí", "Malloa", "Mostazal", "Olivar", "Peumo",
            "Pichidegua", "Quinta de Tilcoco", "Rengo", "Requínoa", "San Vicente",
            // Cardenal Caro
            "Pichilemu", "La Estrella", "Litueche", "Marchigüe", "Navidad", "Paredones",
            // Colchagua
            "San Fernando", "Chépica", "Chimbarongo", "Lolol", "Nancagua", "Palmilla",
            "Peralillo", "Placilla", "Pumanque", "Santa Cruz"
        ),

        // IX - Región del Maule
        "Región del Maule" to listOf(
            // Talca
            "Talca", "Constitución", "Curepto", "Empedrado", "Maule", "Pelarco", "Pencahue",
            "Río Claro", "San Clemente", "San Rafael",
            // Cauquenes
            "Cauquenes", "Chanco", "Pelluhue",
            // Curicó
            "Curicó", "Hualañé", "Licantén", "Molina", "Rauco", "Romeral", "Sagrada Familia", "Teno", "Vichuquén",
            // Linares
            "Linares", "Colbún", "Longaví", "Parral", "Retiro", "San Javier", "Villa Alegre", "Yerbas Buenas"
        ),

        // X - Región de Ñuble
        "Región de Ñuble" to listOf(
            "Chillán", "Chillán Viejo",
            "Quillón", "Quirihue", "Cobquecura", "Coelemu", "Ninhue", "Portezuelo", "Ránquil", "Treguaco",
            "Bulnes", "San Ignacio", "El Carmen", "Pemuco", "Yungay",
            "San Carlos", "Coihueco", "Ñiquén", "San Fabián", "San Nicolás"
        ),

        // XI - Región del Biobío
        "Región del Biobío" to listOf(
            // Concepción
            "Concepción", "Coronel", "Chiguayante", "Florida", "Hualpén", "Hualqui",
            "Lota", "Penco", "San Pedro de la Paz", "Santa Juana", "Talcahuano", "Tomé",
            // Biobío
            "Los Ángeles", "Antuco", "Cabrero", "Laja", "Mulchén", "Nacimiento", "Negrete",
            "Quilaco", "Quilleco", "San Rosendo", "Santa Bárbara", "Tucapel", "Yumbel", "Alto Biobío",
            // Arauco
            "Arauco", "Cañete", "Contulmo", "Curanilahue", "Lebu", "Los Álamos", "Tirúa"
        ),

        // XII - Región de La Araucanía
        "Región de La Araucanía" to listOf(
            // Cautín
            "Temuco", "Carahue", "Cholchol", "Cunco", "Curarrehue", "Freire", "Galvarino", "Gorbea",
            "Lautaro", "Loncoche", "Melipeuco", "Nueva Imperial", "Padre Las Casas",
            "Perquenco", "Pitrufquén", "Pucón", "Saavedra", "Teodoro Schmidt", "Toltén",
            "Vilcún", "Villarrica",
            // Malleco
            "Angol", "Collipulli", "Curacautín", "Ercilla", "Lonquimay", "Los Sauces",
            "Lumaco", "Purén", "Renaico", "Traiguén", "Victoria"
        ),

        // XIII - Región de Los Ríos
        "Región de Los Ríos" to listOf(
            // Valdivia
            "Valdivia", "Corral", "Lanco", "Los Lagos", "Máfil", "Mariquina", "Paillaco", "Panguipulli",
            // Ranco
            "La Unión", "Futrono", "Lago Ranco", "Río Bueno"
        ),

        // XIV - Región de Los Lagos
        "Región de Los Lagos" to listOf(
            // Llanquihue
            "Puerto Montt", "Calbuco", "Cochamó", "Fresia", "Frutillar", "Llanquihue",
            "Los Muermos", "Maullín", "Puerto Varas",
            // Chiloé
            "Castro", "Ancud", "Chonchi", "Curaco de Vélez", "Dalcahue", "Puqueldón",
            "Queilén", "Quellón", "Quemchi", "Quinchao",
            // Osorno
            "Osorno", "Puerto Octay", "Purranque", "Puyehue", "Río Negro", "San Juan de la Costa", "San Pablo",
            // Palena
            "Chaitén", "Futaleufú", "Hualaihué", "Palena"
        ),

        // XV - Región de Aysén del Gral. Carlos Ibáñez del Campo
        "Región de Aysén del Gral. Carlos Ibáñez del Campo" to listOf(
            // Coyhaique
            "Coyhaique", "Lago Verde",
            // Aysén
            "Aysén", "Cisnes", "Guaitecas",
            // Capitán Prat
            "Cochrane", "O’Higgins", "Tortel",
            // General Carrera
            "Chile Chico", "Río Ibáñez"
        ),

        // XVI - Región de Magallanes y de la Antártica Chilena
        "Región de Magallanes y de la Antártica Chilena" to listOf(
            // Magallanes
            "Punta Arenas", "Laguna Blanca", "Río Verde", "San Gregorio",
            // Antártica Chilena
            "Cabo de Hornos", "Antártica",
            // Tierra del Fuego
            "Porvenir", "Primavera", "Timaukel",
            // Última Esperanza
            "Natales", "Torres del Paine"
        )
    )

    /** Lista de nombres de regiones en el mismo orden visual que arriba */
    val NOMBRES_REGIONES: List<String> = listOf(
        "Región de Arica y Parinacota",
        "Región de Tarapacá",
        "Región de Antofagasta",
        "Región de Atacama",
        "Región de Coquimbo",
        "Región de Valparaíso",
        "Región Metropolitana de Santiago",
        "Región del Libertador General Bernardo O’Higgins",
        "Región del Maule",
        "Región de Ñuble",
        "Región del Biobío",
        "Región de La Araucanía",
        "Región de Los Ríos",
        "Región de Los Lagos",
        "Región de Aysén del Gral. Carlos Ibáñez del Campo",
        "Región de Magallanes y de la Antártica Chilena"
    )

    fun regiones(): List<String> = NOMBRES_REGIONES

    fun comunasDe(region: String): List<String> = REGIONES[region] ?: emptyList()
}
