package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/** RF-06: datos del estudiante. */
class ObtenerEstudianteUseCase(private val repository: BibliotecaRepository) {

    suspend operator fun invoke(): Result<Estudiante> = runCatching { repository.obtenerEstudiante() }
}
