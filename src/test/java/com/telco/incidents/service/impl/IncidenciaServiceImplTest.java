package com.telco.incidents.service.impl;

import com.telco.incidents.dto.IncidenciaRequestDTO;
import com.telco.incidents.model.*;
import com.telco.incidents.repository.*;
import com.telco.users.model.User;
import com.telco.users.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IncidenciaServiceImplTest {

    @Mock private IIncidenciaRepository incidenciaRepository;
    @Mock private IClienteRepository clienteRepository;
    @Mock private IUserRepository userRepository;
    @Mock private ITipoIncidenciaRepository tipoIncidenciaRepository;
    @Mock private IZonaRepository zonaRepository;
    @Mock private IResultadoIncidenciaRepository resultadoIncidenciaRepository;
    @Mock private IEtiquetaRepository etiquetaRepository;

    @InjectMocks
    private IncidenciaServiceImpl incidenciaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 🔍 1. searchIncidents
    @Test
    void testSearchIncidents_withFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Incidencia> mockPage = new PageImpl<>(Collections.emptyList());

        when(incidenciaRepository.findAll((Specification<Incidencia>) any(), eq(pageable)))
    .thenReturn(mockPage);


        Page<Incidencia> result = incidenciaService.searchIncidents(1L, 2L, 3L, 4L, pageable);

        assertNotNull(result);
        verify(incidenciaRepository, times(1))
    .findAll((Specification<Incidencia>) any(), eq(pageable));

    }

    // ➕ 2. crearIncidencia - success
    @Test
    void testCrearIncidencia_successful() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(
                1L, 2L, 3L, 4L, 5L, "Descripción de prueba", Set.of(6L)
        );

        Cliente cliente = new Cliente();
        User usuario = new User();
        TipoIncidencia tipo = new TipoIncidencia();
        Zona zona = new Zona();
        ResultadoIncidencia resultado = new ResultadoIncidencia();
        Etiqueta etiqueta = new Etiqueta();
        Incidencia saved = new Incidencia();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(userRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(tipoIncidenciaRepository.findById(3L)).thenReturn(Optional.of(tipo));
        when(zonaRepository.findById(4L)).thenReturn(Optional.of(zona));
        when(resultadoIncidenciaRepository.findById(5L)).thenReturn(Optional.of(resultado));
        when(etiquetaRepository.findAllById(Set.of(6L))).thenReturn(List.of(etiqueta));
        when(incidenciaRepository.save(any())).thenReturn(saved);

        Incidencia result = incidenciaService.crearIncidencia(dto);

        assertNotNull(result);
        verify(incidenciaRepository).save(any());
    }

    // ❌ 3. crearIncidencia - cliente no encontrado
    @Test
    void testCrearIncidencia_clienteNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(99L, 2L, 3L, 4L, null, "Sin cliente", Set.of());

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Cliente no encontrado"));
    }

    // 🔎 4. findById - encontrado
    @Test
    void testFindById_found() {
        Incidencia incidencia = new Incidencia();
        when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidencia));

        Incidencia result = incidenciaService.findById(1L);

        assertNotNull(result);
    }

    // 🔍 5. findById - no encontrado
    @Test
    void testFindById_notFound() {
        when(incidenciaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> incidenciaService.findById(1L));
    }

    // ➕ EXTRA TESTS

    @Test
    void testCrearIncidencia_usuarioNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(1L, 99L, 3L, 4L, null, "Sin usuario", Set.of());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void testCrearIncidencia_tipoNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(1L, 2L, 99L, 4L, null, "Sin tipo", Set.of());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(tipoIncidenciaRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Tipo de Incidencia no encontrado"));
    }

    @Test
    void testCrearIncidencia_zonaNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(1L, 2L, 3L, 99L, null, "Sin zona", Set.of());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(tipoIncidenciaRepository.findById(3L)).thenReturn(Optional.of(new TipoIncidencia()));
        when(zonaRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Zona no encontrada"));
    }

    @Test
    void testCrearIncidencia_resultadoNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(1L, 2L, 3L, 4L, 99L, "Resultado inválido", Set.of());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(tipoIncidenciaRepository.findById(3L)).thenReturn(Optional.of(new TipoIncidencia()));
        when(zonaRepository.findById(4L)).thenReturn(Optional.of(new Zona()));
        when(resultadoIncidenciaRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Resultado no encontrado"));
    }

    @Test
    void testCrearIncidencia_sinResultadoNiEtiquetas() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(1L, 2L, 3L, 4L, null, "Sin opcionales", Collections.emptySet());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(tipoIncidenciaRepository.findById(3L)).thenReturn(Optional.of(new TipoIncidencia()));
        when(zonaRepository.findById(4L)).thenReturn(Optional.of(new Zona()));
        when(etiquetaRepository.findAllById(Collections.emptySet())).thenReturn(Collections.emptyList());
        when(incidenciaRepository.save(any())).thenReturn(new Incidencia());

        Incidencia result = incidenciaService.crearIncidencia(dto);

        assertNotNull(result);
        verify(resultadoIncidenciaRepository, never()).findById(anyLong());
        verify(incidenciaRepository).save(any());
    }
}
