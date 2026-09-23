package pe.upeu.biblioandes.data.local

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import pe.upeu.biblioandes.domain.usecase.ProveedorFecha
import kotlin.time.Clock

/** "Hoy" según el reloj y la zona horaria del dispositivo. */
class ProveedorFechaSistema : ProveedorFecha {
    override fun hoy(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
}
