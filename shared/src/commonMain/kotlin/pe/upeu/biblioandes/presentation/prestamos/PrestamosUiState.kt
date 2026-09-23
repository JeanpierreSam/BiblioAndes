package pe.upeu.biblioandes.presentation.prestamos

data class PrestamosUiState(
    val filtro: FiltroEstado = FiltroEstado.TODOS,
    val fase: FasePrestamos = FasePrestamos.Cargando
)

sealed interface FasePrestamos {
    data object Cargando : FasePrestamos
    data class Contenido(val prestamos: List<PrestamoUi>) : FasePrestamos
    data class Vacio(val mensaje: String) : FasePrestamos
    data class Error(val mensaje: String) : FasePrestamos
}
