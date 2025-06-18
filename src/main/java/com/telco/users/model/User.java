package com.telco.users.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario") // Mapea a la tabla "usuario"
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Mapea a la columna id_usuario
    private Long id;

    // Adaptación: Mapeamos la columna "nombre" de la BD a "firstName"
    @Column(name = "nombre", nullable = false)
    private String firstName;

    // Hacemos que lastName sea "transient" para que JPA lo ignore, ya que no existe en la BD.
    @Transient
    private String lastName;

    @Column(name = "correo", unique = true, nullable = false) // Mapea a la columna correo
    private String email;

    // El username no existe en la tabla. Haremos que sea el mismo que el correo para que Spring Security funcione.
    @Transient
    private String username;

    @Column(name = "contrasena", nullable = false) // Mapea a la columna contraseña
    private String password;

    // Adaptación: Reemplazamos el enum por una relación Many-to-One
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para que el rol se cargue siempre con el usuario
    @JoinColumn(name = "id_rol", nullable = false) // Mapea a la FK id_rol
    private transient Rol rol;

    // --- Métodos de la interfaz UserDetails (¡IMPORTANTE ACTUALIZARLOS!) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ahora obtenemos el nombre del rol desde la entidad Rol
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase()));
    }

    @Override
    public String getUsername() {
        // Devolvemos el correo como nombre de usuario para Spring Security
        return this.username;
    }

    // ... los otros métodos de UserDetails (isAccountNonExpired, etc.) se quedan igual ...

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}