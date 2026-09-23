package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/** RF-02: catálogo completo y sus categorías. */
class ObtenerCatalogoUseCase(private val repository: BibliotecaRepository) {

    // Las dos consultas corren en paralelo: la pantalla espera un solo retardo.
    suspend operator fun invoke(): Result<Catalogo> = runCatching {
        coroutineScope {
            val categorias = async { repository.obtenerCategorias() }
            val libros = async { repository.obtenerLibros() }
            Catalogo(categorias = categorias.await(), libros = libros.await())
        }
    }
}

data class Catalogo(val categorias: List<String>, val libros: List<Libro>)
