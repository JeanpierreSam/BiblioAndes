package pe.upeu.biblioandes.domain.usecase

import kotlinx.datetime.LocalDate

/** Fecha de "hoy". La implementación real está en data; las pruebas usan una fija. */
fun interface ProveedorFecha {
    fun hoy(): LocalDate
}
