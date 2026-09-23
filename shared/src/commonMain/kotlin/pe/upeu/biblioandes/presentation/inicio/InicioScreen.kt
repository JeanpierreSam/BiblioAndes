package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.components.AccesoRapido
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.EstadoVacioCentrado
import pe.upeu.biblioandes.presentation.components.EtiquetaEstado
import pe.upeu.biblioandes.presentation.prestamos.PrestamoUi

/** Conecta el ViewModel con la pantalla; recarga cada vez que se vuelve a Inicio. */
@Composable
fun InicioRoute(
    onAbrirCatalogo: () -> Unit,
    onAbrirPrestamos: () -> Unit,
    onAbrirPerfil: () -> Unit,
    onAbrirLibro: (Int) -> Unit,
    viewModel: InicioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.cargar() }
    InicioScreen(
        uiState = uiState,
        onAbrirCatalogo = onAbrirCatalogo,
        onAbrirPrestamos = onAbrirPrestamos,
        onAbrirPerfil = onAbrirPerfil,
        onAbrirLibro = onAbrirLibro,
        onReintentar = viewModel::cargar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    uiState: InicioUiState,
    onAbrirCatalogo: () -> Unit,
    onAbrirPrestamos: () -> Unit,
    onAbrirPerfil: () -> Unit,
    onAbrirLibro: (Int) -> Unit,
    onReintentar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BiblioAndes") },
                actions = {
                    IconButton(onClick = onAbrirPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil y ajustes")
                    }
                }
            )
        }
    ) { padding ->
        val contenido = Modifier.padding(padding)
        when (uiState) {
            InicioUiState.Cargando -> EstadoCarga("Cargando tu resumen…", contenido)

            is InicioUiState.Error -> EstadoVacioCentrado(
                icono = Icons.Default.ErrorOutline,
                titulo = "Algo salió mal",
                descripcion = uiState.mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = contenido,
                accion = { Button(onClick = onReintentar) { Text("Reintentar") } }
            )

            is InicioUiState.Contenido -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = contenido.fillMaxSize()
            ) {
                item {
                    Column {
                        Text("Hola, ${uiState.primerNombre}", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Consulta el catálogo y controla tus devoluciones",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    val destacado = uiState.destacado
                    if (destacado != null) {
                        TarjetaDestacada(prestamo = destacado, onClick = { onAbrirLibro(destacado.libroId) })
                    } else {
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            EstadoVacio(
                                icono = Icons.Default.EventAvailable,
                                titulo = "No tienes préstamos pendientes",
                                descripcion = "Cuando solicites un libro, aquí verás su fecha de devolución"
                            )
                        }
                    }
                }
                item { Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AccesoRapido(
                            texto = "Catálogo",
                            icono = Icons.AutoMirrored.Filled.MenuBook,
                            onClick = onAbrirCatalogo,
                            modifier = Modifier.weight(1f)
                        )
                        AccesoRapido(
                            texto = "Mis préstamos",
                            icono = Icons.Default.Bookmarks,
                            onClick = onAbrirPrestamos,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/** Tarjeta destacada con el préstamo cuya devolución vence primero. */
@Composable
private fun TarjetaDestacada(prestamo: PrestamoUi, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Event, contentDescription = null)
                Text("Tu próxima devolución", style = MaterialTheme.typography.labelLarge)
            }
            Text(prestamo.titulo, style = MaterialTheme.typography.titleLarge)
            Text(prestamo.autor, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Fecha límite: ${prestamo.fechaLimite}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstado(prestamo.tipo, prestamo.estadoTexto)
            }
            Text(prestamo.detalleEstado, style = MaterialTheme.typography.titleSmall)
        }
    }
}
