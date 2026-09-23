package pe.upeu.biblioandes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme

/**
 * Raíz común de la UI (Android e iOS). Koin ya se inició en MainApplication
 * (Android) o en initKoinIos() (iOS). El modo oscuro es estado de la UI y se
 * eleva hasta aquí para que el tema envuelva a toda la aplicación.
 */
@Composable
fun App() {
    val oscuroDelSistema = isSystemInDarkTheme()
    var modoOscuro by rememberSaveable { mutableStateOf(oscuroDelSistema) }

    BiblioAndesTheme(modoOscuro = modoOscuro) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost(modoOscuro = modoOscuro, onModoOscuroChange = { modoOscuro = it })
        }
    }
}
