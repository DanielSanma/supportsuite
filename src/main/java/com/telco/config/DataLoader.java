package com.telco.config;

import com.telco.incidents.model.*;
import com.telco.incidents.repository.*;
import com.telco.users.model.Rol;
import com.telco.users.model.User;
import com.telco.users.repository.IUserRepository;
import com.telco.users.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j // Anotación de Lombok para logging, más profesional que System.out.println
public class DataLoader implements CommandLineRunner {

    // --- Inyección de todos los repositorios ---
    private final IUserRepository userRepository;
    private final RolRepository rolRepository;
    private final IClienteRepository clienteRepository;
    private final ITipoIncidenciaRepository tipoIncidenciaRepository;
    private final IResultadoIncidenciaRepository resultadoIncidenciaRepository;
    private final IZonaRepository zonaRepository;
    private final IEtiquetaRepository etiquetaRepository;
    private final IIncidenciaRepository incidenciaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Envuelve toda la operación en una única transacción
    public void run(String... args) throws Exception {

        log.info("Iniciando la carga de datos de prueba...");

        // --- 1. Crear Roles y Usuarios ---
    
        Rol tecnicoRol = createRolIfNotFound("tecnico");
        Rol supervisorRol = createRolIfNotFound("supervisor"); // Nuevo rol

        User userTecnico1 = createUserIfNotFound("tecnico1@telconova.com", "Ana", "Gomez", "tecnico123", tecnicoRol);
        User userSupervisor1 = createUserIfNotFound("supervisor1@telconova.com", "Carlos", "Perez", "supervisor123", supervisorRol);

        // USuarios a borrar
        User userParaBorrar1 = createUserIfNotFound("borrar1@telconova.com", "Usuario", "A Borrar 1", "borrar123", tecnicoRol);
        User userParaBorrar2 = createUserIfNotFound("borrar2@telconova.com", "Usuario", "A Borrar 2", "borrar123", tecnicoRol);

        // --- Crear Incidencias (solo si no existen) ---
        if (incidenciaRepository.count() == 0) {
            log.info("No se encontraron incidencias, creando datos de prueba...");

            log.info("Creando catálogos...");
            // --- 2. Crear Datos de Catálogo ---
            log.info("Creando catálogos...");
            Cliente cliente1 = createCliente("Empresa ABC", "contacto@abc.com", "555-0101");
            Cliente cliente2 = createCliente("Compañía XYZ", "soporte@xyz.net", "555-0102");

            TipoIncidencia tipoRed = createTipoIncidencia("Falla de Red");
            TipoIncidencia tipoHardware = createTipoIncidencia("Equipo Defectuoso");
            TipoIncidencia tipoSoftware = createTipoIncidencia("Falla de Software");

            ResultadoIncidencia resuelto = createResultadoIncidencia("Solucionado Exitosamente");
            ResultadoIncidencia pendiente = createResultadoIncidencia("Requiere Segunda Visita");

            Zona zonaNorte = createZona("Norte");
            Zona zonaSur = createZona("Sur");

            Etiqueta etiquetaVIP = createEtiqueta("VIP");
            Etiqueta etiquetaUrgente = createEtiqueta("Urgente");
            Etiqueta etiquetaSoftware = createEtiqueta("Software");

            // --- 3. Crear Incidencias de Prueba ---
            log.info("Creando incidencias de prueba...");

            // Incidencia 1: Resuelta, VIP y Urgente
            Incidencia inc1 = new Incidencia();
            inc1.setCliente(cliente1);
            inc1.setUsuario(userTecnico1);
            inc1.setTipoIncidencia(tipoHardware);
            inc1.setResultadoIncidencia(resuelto);
            inc1.setZona(zonaNorte);
            inc1.setDescripcion("El router principal no enciende.");
            inc1.setFecha(LocalDateTime.now().minusDays(5));
            inc1.setEtiquetas(Set.of(etiquetaVIP, etiquetaUrgente));
            incidenciaRepository.save(inc1);

            // Incidencia 2: Pendiente, en otra zona
            Incidencia inc2 = new Incidencia();
            inc2.setCliente(cliente2);
            inc2.setUsuario(userTecnico1);
            inc2.setTipoIncidencia(tipoRed);
            inc2.setResultadoIncidencia(pendiente);
            inc2.setZona(zonaSur);
            inc2.setDescripcion("Conexión intermitente en la oficina del segundo piso.");
            inc2.setFecha(LocalDateTime.now().minusDays(2));
            incidenciaRepository.save(inc2);

            // Incidencia 3: Sin resultado aún, asignada al supervisor
            Incidencia inc3 = new Incidencia();
            inc3.setCliente(cliente1);
            inc3.setUsuario(userSupervisor1);
            inc3.setTipoIncidencia(tipoSoftware);
            inc3.setZona(zonaNorte);
            inc3.setDescripcion("El sistema de monitoreo no reporta datos.");
            inc3.setFecha(LocalDateTime.now());
            inc3.setEtiquetas(Set.of(etiquetaSoftware));
            incidenciaRepository.save(inc3);
        } else {
            log.info("Las incidencias de prueba ya existen.");
        }

        log.info("Carga de datos y verificación finalizada.");

        log.info("Carga de datos de prueba finalizada.");
    }

    // --- Métodos de ayuda para mantener el código limpio ---

    private Rol createRolIfNotFound(String name) {
        return rolRepository.findByNombreRol(name).orElseGet(() -> {
            Rol newRol = new Rol();
            newRol.setNombreRol(name);
            return rolRepository.save(newRol);
        });
    }

    private User createUserIfNotFound(String email, String firstName, String lastName, String password, Rol rol) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRol(rol);
            return userRepository.save(newUser);
        });
    }

    private Cliente createCliente(String nombre, String correo, String telefono) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setCorreo(correo);
        c.setTelefono(telefono);
        return clienteRepository.save(c);
    }

    private TipoIncidencia createTipoIncidencia(String nombre) {
        TipoIncidencia ti = new TipoIncidencia();
        ti.setNombre(nombre);
        return tipoIncidenciaRepository.save(ti);
    }

    private ResultadoIncidencia createResultadoIncidencia(String descripcion) {
        ResultadoIncidencia ri = new ResultadoIncidencia();
        ri.setDescripcion(descripcion);
        return resultadoIncidenciaRepository.save(ri);
    }

    private Zona createZona(String nombre) {
        Zona z = new Zona();
        z.setNombre(nombre);
        return zonaRepository.save(z);
    }

    private Etiqueta createEtiqueta(String nombre) {
        Etiqueta e = new Etiqueta();
        e.setNombre(nombre);
        return etiquetaRepository.save(e);
    }
}