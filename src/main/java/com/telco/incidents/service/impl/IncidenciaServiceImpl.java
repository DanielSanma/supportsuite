package com.telco.incidents.service.impl;

import com.telco.incidents.dto.IncidenciaRequestDTO;
import com.telco.incidents.model.*;
import com.telco.incidents.repository.*;
import com.telco.incidents.repository.specification.IncidenciaSpecification;
import com.telco.incidents.service.IIncidenciaService;
import com.telco.users.model.User;
import com.telco.users.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Todas las búsquedas son de solo lectura por defecto
public class IncidenciaServiceImpl implements IIncidenciaService {

    private final IIncidenciaRepository incidenciaRepository;
    private final IClienteRepository clienteRepository;
    private final IUserRepository userRepository;
    private final ITipoIncidenciaRepository tipoIncidenciaRepository;
    private final IZonaRepository zonaRepository;
    private final IResultadoIncidenciaRepository resultadoIncidenciaRepository;
    private final IEtiquetaRepository etiquetaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Incidencia> searchIncidents(
            Long id,
            Long usuarioId,
            Long tipoId,
            Long resultadoId,
            Pageable pageable) {

        // 1. Empezamos con una especificación que no hace nada (devuelve todo).
        Specification<Incidencia> spec = Specification.where(null);

        // 2. Añadimos condiciones dinámicamente solo si el filtro no es nulo.
        if (id != null) {
            spec = spec.and(IncidenciaSpecification.hasId(id));
        }
        if (usuarioId != null) {
            spec = spec.and(IncidenciaSpecification.hasUsuarioId(usuarioId));
        }
        if (tipoId != null) {
            spec = spec.and(IncidenciaSpecification.hasTipoId(tipoId));
        }
        if (resultadoId != null) {
            spec = spec.and(IncidenciaSpecification.hasResultadoId(resultadoId));
        }

        // 3. Ejecutamos la consulta final con todos los filtros combinados y la paginación.
        return incidenciaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Incidencia crearIncidencia(IncidenciaRequestDTO dto) {
        // 1. "Hidratar" las entidades a partir de los IDs
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.clienteId()));

        User usuario = userRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + dto.usuarioId()));

        TipoIncidencia tipo = tipoIncidenciaRepository.findById(dto.tipoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Incidencia no encontrado con ID: " + dto.tipoId()));

        Zona zona = zonaRepository.findById(dto.zonaId())
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con ID: " + dto.zonaId()));

        // El resultado es opcional, lo buscamos solo si se proporciona un ID
        ResultadoIncidencia resultado = null;
        if (dto.resultadoId() != null) {
            resultado = resultadoIncidenciaRepository.findById(dto.resultadoId())
                    .orElseThrow(() -> new EntityNotFoundException("Resultado no encontrado con ID: " + dto.resultadoId()));
        }

        // Las etiquetas son opcionales
        Set<Etiqueta> etiquetas = new HashSet<>();
        if (dto.etiquetaIds() != null && !dto.etiquetaIds().isEmpty()) {
            etiquetas = new HashSet<>(etiquetaRepository.findAllById(dto.etiquetaIds()));
        }

        // 2. Construir la nueva entidad Incidencia
        Incidencia nuevaIncidencia = new Incidencia();
        nuevaIncidencia.setCliente(cliente);
        nuevaIncidencia.setUsuario(usuario);
        nuevaIncidencia.setTipoIncidencia(tipo);
        nuevaIncidencia.setZona(zona);
        nuevaIncidencia.setResultadoIncidencia(resultado); // Será null si no se proporcionó
        nuevaIncidencia.setDescripcion(dto.descripcion());
        nuevaIncidencia.setEtiquetas(etiquetas);

        // La fecha se asigna automáticamente con @CreationTimestamp

        // 3. Guardar y devolver la nueva entidad
        return incidenciaRepository.save(nuevaIncidencia);
    }

    @Override
    @Transactional(readOnly = true)
    public Incidencia findById(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incidencia no encontrada con ID: " + id));
    }
}