package pe.upeu.biblioandes.domain

import kotlinx.datetime.LocalDate
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.model.ValidacionSolicitud
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class ReglasPrestamoTest {

    private val hoy = LocalDate(2026, 9, 23)
    private val libro = Libro(1, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2, "McGraw-Hill")
    private val agotado = libro.copy(id = 2, ejemplaresDisponibles = 0)

    private fun prestamo(limite: String, estado: EstadoPrestamo = EstadoPrestamo.Activo(0)) =
        Prestamo(1, libro, "2026-09-01", limite, estado)

    @Test
    fun rn03_prestamoConFechaLimitePasadaEsVencido() {
        assertEquals(EstadoPrestamo.Vencido(13), ReglasPrestamo.estadoSegunFecha(prestamo("2026-09-10"), hoy))
    }

    @Test
    fun rn03_prestamoConFechaLimiteFuturaEsActivo() {
        assertEquals(EstadoPrestamo.Activo(2), ReglasPrestamo.estadoSegunFecha(prestamo("2026-09-25"), hoy))
    }

    @Test
    fun rn03_prestamoDevueltoConservaSuEstado() {
        val devuelto = prestamo("2026-09-10", EstadoPrestamo.Devuelto("2026-09-09"))
        assertEquals(EstadoPrestamo.Devuelto("2026-09-09"), ReglasPrestamo.estadoSegunFecha(devuelto, hoy))
    }

    @Test
    fun rn03_laFechaLimiteEsSieteDiasDespues() {
        assertEquals(LocalDate(2026, 9, 30), ReglasPrestamo.calcularFechaLimite(hoy))
    }

    @Test
    fun rn02_noSePuedeSolicitarUnLibroSinEjemplares() {
        assertEquals(ValidacionSolicitud.SinEjemplares, ReglasPrestamo.validarSolicitud(agotado, emptyList()))
    }

    @Test
    fun rn04_unPrestamoVencidoBloqueaNuevasSolicitudes() {
        val prestamos = listOf(prestamo("2026-09-10", EstadoPrestamo.Vencido(13)))
        assertEquals(ValidacionSolicitud.TienePrestamoVencido, ReglasPrestamo.validarSolicitud(libro, prestamos))
    }

    @Test
    fun rn01_conTresActivosNoSePuedeSolicitarOtro() {
        val activos = List(3) { prestamo("2026-09-28", EstadoPrestamo.Activo(5)) }
        assertEquals(ValidacionSolicitud.LimiteAlcanzado, ReglasPrestamo.validarSolicitud(libro, activos))
    }

    @Test
    fun conDosActivosSinVencidosSePuedeSolicitar() {
        val activos = List(2) { prestamo("2026-09-28", EstadoPrestamo.Activo(5)) }
        assertEquals(ValidacionSolicitud.Permitida, ReglasPrestamo.validarSolicitud(libro, activos))
    }

    @Test
    fun rf05_laBusquedaIgnoraMayusculasYTildes() {
        val libros = listOf(libro, agotado.copy(titulo = "Redes", autor = "Gabriel García Márquez"))
        val filtrar = FiltrarCatalogoUseCase()
        assertEquals(listOf(libro), filtrar(libros, categoria = null, consulta = "CALCULO"))
        assertEquals(1, filtrar(libros, categoria = null, consulta = "garcia").size)
        assertEquals(2, filtrar(libros, categoria = "Matemática", consulta = "").size)
    }
}
