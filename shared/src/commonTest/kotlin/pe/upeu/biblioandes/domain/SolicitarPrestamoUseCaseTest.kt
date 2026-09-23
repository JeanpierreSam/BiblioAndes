package pe.upeu.biblioandes.domain

import kotlinx.coroutines.test.runTest
import pe.upeu.biblioandes.data.local.ProveedorFechaSistema
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.ValidacionSolicitud
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.PrestamoRechazadoException
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Recorre los casos de uso reales contra el repositorio simulado (runTest salta los 800 ms). */
class SolicitarPrestamoUseCaseTest {

    private val fecha = ProveedorFechaSistema()
    private val repository = BibliotecaRepositoryFake()
    private val obtenerPrestamos = ObtenerPrestamosUseCase(repository, fecha)
    private val solicitar = SolicitarPrestamoUseCase(repository, obtenerPrestamos, fecha)
    private val devolver = DevolverPrestamoUseCase(repository, fecha)

    @Test
    fun losDatosSemillaTienenDosActivosDosDevueltosYUnVencido() = runTest {
        val estados = obtenerPrestamos().getOrThrow().map { it.estado::class.simpleName }
        assertEquals(2, estados.count { it == "Activo" })
        assertEquals(2, estados.count { it == "Devuelto" })
        assertEquals(1, estados.count { it == "Vencido" })
    }

    @Test
    fun rn04_conElVencidoDeLaSemillaSeRechazaLaSolicitud() = runTest {
        val error = solicitar(repository.obtenerLibro(7)!!).exceptionOrNull()
        assertIs<PrestamoRechazadoException>(error)
        assertEquals(ValidacionSolicitud.TienePrestamoVencido, error.motivo)
    }

    @Test
    fun trasRegularizarSeRegistraElPrestamoYSeDescuentaUnEjemplar() = runTest {
        devolver(5).getOrThrow()
        val libro = repository.obtenerLibro(7)!!
        val prestamo = solicitar(libro).getOrThrow()
        assertEquals(EstadoPrestamo.Activo(7), prestamo.estado)
        assertEquals(libro.ejemplaresDisponibles - 1, repository.obtenerLibro(7)!!.ejemplaresDisponibles)
    }

    @Test
    fun rn01_elCuartoPrestamoActivoSeRechaza() = runTest {
        devolver(5).getOrThrow()
        solicitar(repository.obtenerLibro(7)!!).getOrThrow() // tercer activo
        val error = solicitar(repository.obtenerLibro(8)!!).exceptionOrNull()
        assertIs<PrestamoRechazadoException>(error)
        assertEquals(ValidacionSolicitud.LimiteAlcanzado, error.motivo)
    }
}
