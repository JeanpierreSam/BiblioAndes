package pe.upeu.biblioandes.presentation.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.presentation.components.CampoBusqueda
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacioCentrado
import pe.upeu.biblioandes.presentation.components.FilaDeChips
import pe.upeu.biblioandes.presentation.components.LibroItem

@Composable
fun CatalogoRoute(
    onAbrirLibro: (Int) -> Unit,
    viewModel: CatalogoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Recarga al volver del detalle: los ejemplares pueden haber cambiado.
    LaunchedEffect(Unit) { viewModel.cargar() }
    CatalogoScreen(
        uiState = uiState,
        onConsultaChange = viewModel::onConsultaChange,
        onCategoriaSeleccionada = viewModel::onCategoriaSeleccionada,
        onSoloDisponiblesChange = viewModel::onSoloDisponiblesChange,
        onReintentar = viewModel::cargar,
        onAbrirLibro = onAbrirLibro
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    uiState: CatalogoUiState,
    onConsultaChange: (String) -> Unit,
    onCategoriaSeleccionada: (String?) -> Unit,
    onSoloDisponiblesChange: (Boolean) -> Unit,
    onReintentar: () -> Unit,
    onAbrirLibro: (Int) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Catálogo") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            CampoBusqueda(
                valor = uiState.consulta,
                onValorChange = onConsultaChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            FilterChip(
                selected = uiState.soloDisponibles,
                onClick = { onSoloDisponiblesChange(!uiState.soloDisponibles) },
                label = { Text("Solo disponibles") },
                leadingIcon = if (uiState.soloDisponibles) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else {
                    null
                },
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            FilaDeChips(
                opciones = listOf<String?>(null) + uiState.categorias,
                seleccionada = uiState.categoriaSeleccionada,
                etiqueta = { it ?: "Todas" },
                onSeleccionar = onCategoriaSeleccionada,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            when (val fase = uiState.fase) {
                FaseCatalogo.Cargando -> EstadoCarga("Cargando catálogo…")

                FaseCatalogo.Vacio -> EstadoVacioCentrado(
                    icono = Icons.Default.SearchOff,
                    titulo = "Sin resultados",
                    descripcion = "Ningún libro coincide con la búsqueda o la categoría elegida"
                )

                is FaseCatalogo.Error -> EstadoVacioCentrado(
                    icono = Icons.Default.CloudOff,
                    titulo = "No se pudo cargar el catálogo",
                    descripcion = fase.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    accion = { Button(onClick = onReintentar) { Text("Reintentar") } }
                )

                is FaseCatalogo.Contenido -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(fase.libros, key = { it.id }) { libro ->
                        LibroItem(
                            titulo = libro.titulo,
                            autor = libro.autor,
                            ejemplares = libro.ejemplaresDisponibles,
                            disponible = ReglasPrestamo.tieneEjemplares(libro),
                            onClick = { onAbrirLibro(libro.id) }
                        )
                    }
                }
            }
        }
    }
}
