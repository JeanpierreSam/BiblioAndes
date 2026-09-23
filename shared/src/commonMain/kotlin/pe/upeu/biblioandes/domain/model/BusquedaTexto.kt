package pe.upeu.biblioandes.domain.model

/**
 * RF-05: texto en minúsculas y sin tildes para comparar búsquedas.
 * Kotlin común no tiene java.text.Normalizer, por eso se reemplaza a mano.
 */
fun String.normalizado(): String = lowercase()
    .map { c ->
        when (c) {
            'á', 'à', 'ä', 'â' -> 'a'
            'é', 'è', 'ë', 'ê' -> 'e'
            'í', 'ì', 'ï', 'î' -> 'i'
            'ó', 'ò', 'ö', 'ô' -> 'o'
            'ú', 'ù', 'ü', 'û' -> 'u'
            'ñ' -> 'n'
            else -> c
        }
    }
    .joinToString("")
    .trim()
