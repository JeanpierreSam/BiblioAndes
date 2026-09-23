package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacioCentrado
import pe.upeu.biblioandes.presentation.components.FilaDeChips
import pe.upeu.biblioandes.presentation.components.PrestamoItem

@Composable
fun PrestamosRoute(
    onAbrirLibro: (Int) -> Unit,
    viewModel: PrestamosViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.cargar() }
    PrestamosScreen(
        uiState = uiState,
        onFiltroSeleccionado = viewModel::onFiltroSeleccionado,
        onDevolver = viewModel::onDevolver,
        onReintentar = viewModel::cargar,
        onAbrirLibro = onAbrirLibro
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamosScreen(
    uiState: PrestamosUiState,
    onFiltroSeleccionado: (FiltroEstado) -> Unit,
    onDevolver: (Int) -> Unit,
    onReintentar: () -> Unit,
    onAbrirLibro: (Int) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Mis préstamos") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            FilaDeChips(
                opciones = FiltroEstado.entries,
                seleccionada = uiState.filtro,
                etiqueta = { it.etiqueta },
                onSeleccionar = onFiltroSeleccionado,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            when (val fase = uiState.fase) {
                FasePrestamos.Cargando -> EstadoCarga("Cargando tus préstamos…")

                is FasePrestamos.Vacio -> EstadoVacioCentrado(
                    icono = Icons.Default.BookmarkBorder,
                    titulo = "Sin préstamos",
                    descripcion = fase.mensaje
                )

                is FasePrestamos.Error -> EstadoVacioCentrado(
                    icono = Icons.Default.ErrorOutline,
                    titulo = "Algo salió mal",
                    descripcion = fase.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    accion = { Button(onClick = onReintentar) { Text("Reintentar") } }
                )

                is FasePrestamos.Contenido -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(fase.prestamos, key = { it.id }) { prestamo ->
                        PrestamoItem(
                            prestamo = prestamo,
                            onClick = { onAbrirLibro(prestamo.libroId) },
                            onDevolver = if (prestamo.puedeDevolver) {
                                { onDevolver(prestamo.id) }
                            } else {
                                null
                            }
                        )
                    }
                }
            }
        }
    }
}
