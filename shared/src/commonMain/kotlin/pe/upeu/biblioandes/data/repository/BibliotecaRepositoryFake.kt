package pe.upeu.biblioandes.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Implementación en memoria de [BibliotecaRepository]. Simula la latencia de
 * red con [delay] (no bloquea el hilo principal) y protege las listas con un
 * [Mutex] porque varias corrutinas pueden leer y escribir a la vez.
 *
 * @param simularErrorCatalogo true hace fallar obtenerLibros() para mostrar
 * el estado de error del catálogo (§3.2 del examen).
 */
class BibliotecaRepositoryFake(
    private val simularErrorCatalogo: Boolean = false
) : BibliotecaRepository {

    private val mutex = Mutex()
    private val libros = DatosSimulados.libros.toMutableList()
    private val prestamos = DatosSimulados.prestamos.toMutableList()

    override suspend fun obtenerEstudiante(): Estudiante = conRetardo { DatosSimulados.estudiante }

    override suspend fun obtenerCategorias(): List<String> = conRetardo { DatosSimulados.categorias }

    override suspend fun obtenerLibros(): List<Libro> = conRetardo {
        if (simularErrorCatalogo) error("No se pudo conectar con el catálogo de la biblioteca")
        libros.toList()
    }

    override suspend fun obtenerLibro(id: Int): Libro? = conRetardo { libros.find { it.id == id } }

    override suspend fun obtenerPrestamos(): List<Prestamo> = conRetardo { prestamos.toList() }

    override suspend fun registrarPrestamo(
        libroId: Int,
        fechaPrestamo: String,
        fechaLimite: String
    ): Prestamo = conRetardo {
        val indice = libros.indexOfFirst { it.id == libroId }
        require(indice >= 0) { "El libro no existe" }
        val libro = libros[indice].let { it.copy(ejemplaresDisponibles = it.ejemplaresDisponibles - 1) }
        libros[indice] = libro

        val prestamo = Prestamo(
            id = (prestamos.maxOfOrNull { it.id } ?: 0) + 1,
            libro = libro,
            fechaPrestamo = fechaPrestamo,
            fechaLimite = fechaLimite,
            estado = EstadoPrestamo.Activo(ReglasPrestamo.DIAS_DE_PRESTAMO)
        )
        prestamos += prestamo
        prestamo
    }

    override suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String) = conRetardo {
        val indice = prestamos.indexOfFirst { it.id == prestamoId }
        require(indice >= 0) { "El préstamo no existe" }
        val prestamo = prestamos[indice]
        if (prestamo.estado is EstadoPrestamo.Devuelto) return@conRetardo

        prestamos[indice] = prestamo.copy(estado = EstadoPrestamo.Devuelto(fechaDevolucion))
        val indiceLibro = libros.indexOfFirst { it.id == prestamo.libro.id }
        if (indiceLibro >= 0) {
            libros[indiceLibro] = libros[indiceLibro].let {
                it.copy(ejemplaresDisponibles = it.ejemplaresDisponibles + 1)
            }
        }
    }

    /** Espera el retardo simulado y luego ejecuta [bloque] con acceso exclusivo. */
    private suspend fun <T> conRetardo(bloque: () -> T): T {
        delay(RETARDO_MS)
        return mutex.withLock { bloque() }
    }

    private companion object {
        const val RETARDO_MS = 800L
    }
}
