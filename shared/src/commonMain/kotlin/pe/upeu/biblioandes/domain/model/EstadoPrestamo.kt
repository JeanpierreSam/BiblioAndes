package pe.upeu.biblioandes.domain.model

/**
 * Cada estado lleva solo el dato que le corresponde. Con un enum o un String
 * el préstamo tendría que cargar diasRestantes, fechaDevolucion y diasDeAtraso
 * a la vez, dejando dos de ellos vacíos (o nulos) en cada caso.
 */
sealed class EstadoPrestamo {
    data class Activo(val diasRestantes: Int) : EstadoPrestamo()
    data class Devuelto(val fechaDevolucion: String) : EstadoPrestamo()
    data class Vencido(val diasDeAtraso: Int) : EstadoPrestamo()
}
