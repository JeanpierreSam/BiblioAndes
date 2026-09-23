package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.biblioandes.data.local.ProveedorFechaSistema
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerResumenInicioUseCase
import pe.upeu.biblioandes.domain.usecase.OrdenarCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ProveedorFecha
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroViewModel
import pe.upeu.biblioandes.presentation.inicio.InicioViewModel
import pe.upeu.biblioandes.presentation.perfil.PerfilViewModel
import pe.upeu.biblioandes.presentation.prestamos.PrestamosViewModel

/**
 * Único lugar que conoce las implementaciones concretas. Cuando exista la API,
 * solo cambia la línea del repositorio.
 * `single`: toda la app comparte el mismo catálogo y los mismos préstamos.
 */
val dataModule = module {
    // Cambia a true para demostrar el estado de error del catálogo.
    single<BibliotecaRepository> { BibliotecaRepositoryFake(simularErrorCatalogo = false) }
    single<ProveedorFecha> { ProveedorFechaSistema() }
}

/** `factory`: los casos de uso no guardan estado; se crea uno por inyección. */
val domainModule = module {
    factoryOf(::ObtenerCatalogoUseCase)
    factoryOf(::FiltrarCatalogoUseCase)
    factoryOf(::ObtenerLibroUseCase)
    factoryOf(::ObtenerPrestamosUseCase)
    factoryOf(::SolicitarPrestamoUseCase)
    factoryOf(::DevolverPrestamoUseCase)
    factoryOf(::ObtenerEstudianteUseCase)
    factoryOf(::ObtenerResumenInicioUseCase)
    factoryOf(::OrdenarCatalogoUseCase)
}

val presentationModule = module {
    viewModelOf(::InicioViewModel)
    viewModelOf(::CatalogoViewModel)
    viewModelOf(::PrestamosViewModel)
    viewModelOf(::PerfilViewModel)
    // El id del libro llega como parámetro desde la ruta de navegación.
    viewModel { parametros ->
        DetalleLibroViewModel(
            libroId = parametros.get(),
            obtenerLibro = get(),
            solicitarPrestamo = get()
        )
    }
}

val modulosBiblioAndes: List<Module> = listOf(dataModule, domainModule, presentationModule)

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(modulosBiblioAndes)
}
