package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.model.ValidacionSolicitud
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacioCentrado
import pe.upeu.biblioandes.presentation.components.FilaDato

@Composable
fun DetalleLibroRoute(
    libroId: Int,
    onVolver: () -> Unit,
    viewModel: DetalleLibroViewModel = koinViewModel { parametersOf(libroId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetalleLibroScreen(
        uiState = uiState,
        onVolver = onVolver,
        onSolicitarClick = viewModel::onSolicitarClick,
        onConfirmar = viewModel::onConfirmar,
        onCancelar = viewModel::onCancelar,
        onMensajeMostrado = viewModel::onMensajeMostrado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLibroScreen(
    uiState: DetalleLibroUiState,
    onVolver: () -> Unit,
    onSolicitarClick: () -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    onMensajeMostrado: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val contenido = Modifier.padding(padding)
        when (val fase = uiState.fase) {
            FaseDetalle.Cargando -> EstadoCarga("Cargando libro…", contenido)

            FaseDetalle.NoEncontrado -> EstadoVacioCentrado(
                icono = Icons.Default.SearchOff,
                titulo = "Libro no encontrado",
                descripcion = "Vuelve al catálogo y elige otro libro",
                modifier = contenido
            )

            is FaseDetalle.Contenido -> {
                ContenidoDetalle(
                    libro = fase.libro,
                    solicitando = uiState.solicitando,
                    limiteAlcanzado = uiState.limiteAlcanzado,
                    onSolicitarClick = onSolicitarClick,
                    modifier = contenido
                )
                if (uiState.mostrarConfirmacion) {
                    DialogoConfirmacion(titulo = fase.libro.titulo, onConfirmar = onConfirmar, onCancelar = onCancelar)
                }
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(
    libro: Libro,
    solicitando: Boolean,
    limiteAlcanzado: Boolean,
    onSolicitarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(libro.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(libro.autor, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                FilaDato(Icons.Default.CalendarMonth, "Año", libro.anio.toString())
                FilaDato(Icons.Default.Category, "Categoría", libro.categoria)
                FilaDato(Icons.Default.Place, "Sede", libro.sede)
                FilaDato(Icons.Default.Inventory2, "Ejemplares disponibles", libro.ejemplaresDisponibles.toString())
            }
        }
        Button(
            onClick = onSolicitarClick,
            enabled = !solicitando && !limiteAlcanzado,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (solicitando) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Text("Solicitar préstamo")
            }
        }
        if (limiteAlcanzado) {
            Text(
                ValidacionSolicitud.LimiteAlcanzado.mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DialogoConfirmacion(titulo: String, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Confirmar préstamo") },
        text = {
            Text("¿Deseas solicitar «$titulo»? Deberás devolverlo en ${ReglasPrestamo.DIAS_DE_PRESTAMO} días.")
        },
        confirmButton = { TextButton(onClick = onConfirmar) { Text("Solicitar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
