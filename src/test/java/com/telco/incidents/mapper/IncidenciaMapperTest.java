package com.telco.incidents.mapper;

import com.telco.incidents.dto.IncidenciaResponseDTO;
import com.telco.incidents.dto.SimpleClienteDTO;
import com.telco.incidents.dto.SimpleUserDTO;
import com.telco.incidents.model.*;
import com.telco.users.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IncidenciaMapperTest {

    private final IncidenciaMapper mapper = Mappers.getMapper(IncidenciaMapper.class);

    @Test
    void shouldMapUserToSimpleUserDto() {
        User user = new User();
        user.setFirstName("Carlos");

        SimpleUserDTO dto = mapper.userToSimpleUserDto(user);

        assertNotNull(dto);
        assertEquals("Carlos", dto.nombre());
    }

    @Test
    void shouldMapClienteToSimpleClienteDto() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Empresa S.A.");
        
        SimpleClienteDTO dto = mapper.clienteToSimpleClienteDto(cliente);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Empresa S.A.", dto.nombre());
        
    }

    @Test
    void shouldConvertEtiquetasToStringSet() {
        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre("URGENTE");

        Set<String> etiquetas = mapper.etiquetasToString(Set.of(etiqueta));

        assertNotNull(etiquetas);
        assertTrue(etiquetas.contains("URGENTE"));
    }

    @Test
    void shouldMapIncidenciaToDto() {
        // Preparar datos
        User usuario = new User();
        usuario.setFirstName("Juan");

        TipoIncidencia tipo = new TipoIncidencia();
        tipo.setNombre("Técnica");

        ResultadoIncidencia resultado = new ResultadoIncidencia();
        resultado.setDescripcion("Resuelto");

        Zona zona = new Zona();
        zona.setNombre("Zona Norte");

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre("Crítico");

        Incidencia incidencia = new Incidencia();
        incidencia.setUsuario(usuario);
        incidencia.setTipoIncidencia(tipo);
        incidencia.setResultadoIncidencia(resultado);
        incidencia.setZona(zona);
        incidencia.setEtiquetas(Set.of(etiqueta));

        // Ejecutar mapper
        IncidenciaResponseDTO dto = mapper.toDto(incidencia);

        // Verificar resultados
        assertNotNull(dto);
        assertNotNull(dto.getUsuarioAsignado());
        assertEquals("Juan", dto.getUsuarioAsignado().nombre());
        assertEquals("Técnica", dto.getTipoIncidencia());
        assertEquals("Resuelto", dto.getResultadoIncidencia());
        assertEquals("Zona Norte", dto.getZona());
        assertTrue(dto.getEtiquetas().contains("Crítico"));
    }

    @Test
    void shouldHandleNullEtiquetaSetGracefully() {
        Set<String> result = mapper.etiquetasToString(null);
        assertNull(result);
    }

    @Test
    void shouldHandleEmptyEtiquetaSet() {
        Set<String> result = mapper.etiquetasToString(Collections.emptySet());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

