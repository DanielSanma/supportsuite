package com.telco.incidents.mapper;

import com.telco.incidents.dto.IncidenciaResponseDTO;
import com.telco.incidents.dto.SimpleClienteDTO;
import com.telco.incidents.dto.SimpleUserDTO;
import com.telco.incidents.model.Cliente;
import com.telco.incidents.model.Etiqueta;
import com.telco.incidents.model.Incidencia;
import com.telco.users.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IncidenciaMapper {

    // --- Mapeo Principal: de Entidad Incidencia a DTO ---

    @Mapping(source = "usuario", target = "usuarioAsignado")
    @Mapping(source = "tipoIncidencia.nombre", target = "tipoIncidencia")
    @Mapping(source = "resultadoIncidencia.descripcion", target = "resultadoIncidencia")
    @Mapping(source = "zona.nombre", target = "zona")
    @Mapping(source = "etiquetas", target = "etiquetas", qualifiedByName = "etiquetasToString")
    IncidenciaResponseDTO toDto(Incidencia incidencia);


    // --- Mapeos Auxiliares para objetos anidados ---

    @Mapping(source = "firstName", target = "nombre")
    SimpleUserDTO userToSimpleUserDto(User user);

    SimpleClienteDTO clienteToSimpleClienteDto(Cliente cliente);


    // --- Método Personalizado para convertir Set<Etiqueta> a Set<String> ---

    @Named("etiquetasToString")
    default Set<String> etiquetasToString(Set<Etiqueta> etiquetas) {
        if (etiquetas == null) {
            return Collections.emptySet();
        }
        return etiquetas.stream()
                .map(Etiqueta::getNombre)
                .collect(Collectors.toSet());
    }
}