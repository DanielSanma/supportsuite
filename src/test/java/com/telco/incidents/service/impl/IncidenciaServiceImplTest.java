package com.telco.incidents.service.impl;

import com.telco.incidents.dto.IncidenciaRequestDTO;
import com.telco.incidents.model.*;
import com.telco.incidents.repository.*;
import com.telco.incidents.repository.specification.IncidenciaSpecification;
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

        when(incidenciaRepository.findAll(any(Specification.class), eq(pageable)))
    .thenReturn(mockPage);


        Page<Incidencia> result = incidenciaService.searchIncidents(1L, 2L, 3L, 4L, pageable);

        assertNotNull(result);
        verify(incidenciaRepository, times(1)).findAll(ArgumentMatchers.<Specification<Incidencia>>any(), eq(pageable));

    }

    // ➕ 2. crearIncidencia
    @Test
    void testCrearIncidencia_successful() {
        // Arrange
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(
    1L, // clienteId
    2L, // usuarioId
    3L, // tipoId
    4L, // zonaId
    5L, // resultadoId (puede ser null si lo estás probando)
    "Descripción de prueba", // descripcion
    Set.of(6L) // etiquetaIds
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

        // Act
        Incidencia result = incidenciaService.crearIncidencia(dto);

        // Assert
        assertNotNull(result);
        verify(incidenciaRepository).save(any());
    }

    // ❌ crearIncidencia throws EntityNotFoundException
    @Test
    void testCrearIncidencia_clienteNotFound_throwsException() {
        IncidenciaRequestDTO dto = new IncidenciaRequestDTO(
    99L,                  // clienteId
    2L,                   // usuarioId
    3L,                   // tipoId
    4L,                   // zonaId
    null,                 // resultadoId
    "Sin cliente",        // descripcion (debe ser un String válido)
    Set.of()              // etiquetaIds
);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> incidenciaService.crearIncidencia(dto)
        );

        assertTrue(thrown.getMessage().contains("Cliente no encontrado"));
    }

    // 🔎 3. findById
    @Test
    void testFindById_found() {
        Incidencia incidencia = new Incidencia();
        when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidencia));

        Incidencia result = incidenciaService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void testFindById_notFound() {
        when(incidenciaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> incidenciaService.findById(1L));
    }
}
