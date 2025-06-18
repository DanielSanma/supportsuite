package com.telco.incidents.controller;

import com.telco.incidents.dto.IncidenciaRequestDTO;
import com.telco.incidents.dto.IncidenciaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Incidents Management", description = "API para la gestión y consulta de incidencias")
public interface IIncidenciaController {

    @Operation(summary = "Buscar y filtrar incidencias",
            description = "Obtiene una lista paginada de incidencias. Todos los filtros son opcionales. " +
                    "El acceso está restringido a usuarios con el rol 'ADMIN'.")
    ResponseEntity<Page<IncidenciaResponseDTO>> searchIncidents(
            @Parameter(description = "Filtrar por ID de incidencia") @RequestParam(required = false) Long id,
            @Parameter(description = "Filtrar por ID del técnico asignado") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Filtrar por ID del tipo de incidencia") @RequestParam(required = false) Long tipoId,
            @Parameter(description = "Filtrar por ID del resultado de la incidencia") @RequestParam(required = false) Long resultadoId,
            @Parameter(description = "Paginación y ordenamiento (ej. page=0&size=10&sort=fecha,desc)") Pageable pageable
    );

    /**
     * Crea una nueva incidencia en el sistema.
     * Requiere rol de OPERATOR o ADMIN.
     *
     * @param requestDTO El cuerpo de la solicitud con los datos de la incidencia.
     * @return Una respuesta con la incidencia recién creada y un estado 201 (Created).
     */
    @Operation(
            summary = "Crear una nueva incidencia",
            description = "Registra una nueva incidencia en el sistema. El acceso está restringido.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Incidencia creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    ResponseEntity<IncidenciaResponseDTO> crearIncidencia(@Valid @RequestBody IncidenciaRequestDTO requestDTO);

    /**
     * Obtiene los detalles de una incidencia específica por su ID.
     * @param id El ID de la incidencia a recuperar.
     * @return Una respuesta con el DTO de la incidencia y un estado 200 OK.
     */
    @Operation(summary = "Obtener detalle de una incidencia por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Incidencia encontrada"),
                    @ApiResponse(responseCode = "404", description = "Incidencia no encontrada"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            })
    ResponseEntity<IncidenciaResponseDTO> getIncidenciaById(
            @Parameter(description = "ID de la incidencia") @PathVariable Long id
    );
}