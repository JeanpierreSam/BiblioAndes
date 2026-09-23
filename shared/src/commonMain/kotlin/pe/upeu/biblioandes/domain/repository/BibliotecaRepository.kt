package pe.upeu.biblioandes.domain.repository

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Contrato de datos de la biblioteca. Hoy lo cumple BibliotecaRepositoryFake
 * (memoria); en la Unidad 2 lo cumplirá una clase que consuma la API, sin
 * tocar los casos de uso ni las pantallas.
 */
interface BibliotecaRepository {

    suspend fun obtenerEstudiante(): Estudiante

    suspend fun obtenerCategorias(): List<String>

    suspend fun obtenerLibros(): List<Libro>

    /** Devuelve null solo si no existe un libro con ese id. */
    suspend fun obtenerLibro(id: Int): Libro?

    suspend fun obtenerPrestamos(): List<Prestamo>

    /** Registra el préstamo y descuenta un ejemplar del libro. */
    suspend fun registrarPrestamo(libroId: Int, fechaPrestamo: String, fechaLimite: String): Prestamo

    /** Marca el préstamo como Devuelto y repone el ejemplar. */
    suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String)
}
