package pe.upeu.biblioandes.presentation.prestamos

import pe.upeu.biblioandes.domain.model.EstadoPrestamo

/** Filtro de la pantalla Mis préstamos (RF-04). */
enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido");

    fun admite(estado: EstadoPrestamo): Boolean = when (this) {
        TODOS -> true
        ACTIVO -> estado is EstadoPrestamo.Activo
        DEVUELTO -> estado is EstadoPrestamo.Devuelto
        VENCIDO -> estado is EstadoPrestamo.Vencido
    }
}
