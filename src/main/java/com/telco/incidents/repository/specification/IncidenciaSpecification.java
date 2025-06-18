package com.telco.incidents.repository.specification;

import com.telco.incidents.model.*;
import com.telco.users.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaSpecification {

    private IncidenciaSpecification(){
        
    }

    /**
     * Devuelve una Specification que filtra por el ID de la incidencia.
     */
    public static Specification<Incidencia> hasId(Long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

    /**
     * Devuelve una Specification que filtra por el ID del técnico (Usuario) asignado.
     * Realiza un JOIN con la tabla de Usuario.
     */
    public static Specification<Incidencia> hasUsuarioId(Long usuarioId) {
        return (root, query, criteriaBuilder) -> {
            Join<Incidencia, User> usuarioJoin = root.join("usuario");
            return criteriaBuilder.equal(usuarioJoin.get("id"), usuarioId);
        };
    }

    /**
     * Devuelve una Specification que filtra por el ID del tipo de incidencia.
     * Realiza un JOIN con la tabla de TipoIncidencia.
     */
    public static Specification<Incidencia> hasTipoId(Long tipoId) {
        return (root, query, criteriaBuilder) -> {
            Join<Incidencia, TipoIncidencia> tipoJoin = root.join("tipoIncidencia");
            return criteriaBuilder.equal(tipoJoin.get("id"), tipoId);
        };
    }

    /**
     * Devuelve una Specification que filtra por el ID del resultado de la incidencia.
     * Realiza un JOIN con la tabla de ResultadoIncidencia.
     */
    public static Specification<Incidencia> hasResultadoId(Long resultadoId) {
        return (root, query, criteriaBuilder) -> {
            Join<Incidencia, ResultadoIncidencia> resultadoJoin = root.join("resultadoIncidencia");
            return criteriaBuilder.equal(resultadoJoin.get("id"), resultadoId);
        };
    }
}