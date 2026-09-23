package pe.upeu.biblioandes.data.local

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Fuente simulada en memoria (anexo del examen). Las fechas de los préstamos
 * Activos y del Vencido se calculan desde hoy para que conserven su estado
 * el día de la evaluación.
 */
object DatosSimulados {

    private val hoy = ProveedorFechaSistema().hoy()

    /** Fecha ISO desplazada [dias] respecto de hoy (negativo = pasado). */
    private fun dias(dias: Int): String = hoy.plus(dias, DateTimeUnit.DAY).toString()

    val estudiante = Estudiante(
        "E-2291", "Diego Huamán Ccama",
        "Ingeniería de Sistemas", "diego.huaman@correo.pe"
    )

    val categorias = listOf("Programación", "Matemática", "Redes", "Gestión", "Literatura")

    val libros = listOf(
        Libro(1, "Kotlin en profundidad", "M. Salazar", 2023, "Programación", "Central", 3),
        Libro(2, "Estructuras de datos", "R. Peña", 2021, "Programación", "Central", 0),
        Libro(3, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(4, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4),
        Libro(5, "Seguridad en redes", "P. Ríos", 2024, "Redes", "Central", 0),
        Libro(6, "Gestión de proyectos", "S. Delgado", 2021, "Gestión", "Sede Norte", 2),
        Libro(7, "Patrones de diseño en Kotlin", "E. Rojas", 2024, "Programación", "Sede Norte", 1),
        Libro(8, "Álgebra lineal", "C. Vargas", 2020, "Matemática", "Central", 3),
        Libro(9, "Enrutamiento y conmutación", "J. Torres", 2023, "Redes", "Sede Norte", 1),
        Libro(10, "Liderazgo y gestión ágil", "V. Paredes", 2023, "Gestión", "Central", 2),
        Libro(11, "Los ríos profundos", "José María Arguedas", 1958, "Literatura", "Central", 2),
        Libro(12, "Cien años de soledad", "Gabriel García Márquez", 1967, "Literatura", "Sede Sur", 0)
    )

    val prestamos = listOf(
        Prestamo(1, libros[0], dias(-2), dias(5), EstadoPrestamo.Activo(5)),
        Prestamo(2, libros[3], dias(-1), dias(6), EstadoPrestamo.Activo(6)),
        Prestamo(3, libros[2], "2026-08-20", "2026-08-27", EstadoPrestamo.Devuelto("2026-08-26")),
        Prestamo(4, libros[1], "2026-08-05", "2026-08-12", EstadoPrestamo.Devuelto("2026-08-11")),
        Prestamo(5, libros[5], dias(-25), dias(-18), EstadoPrestamo.Vencido(18))
    )
}
