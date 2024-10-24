package org.example.rest.repository;

import org.example.clientes.mappers.UsuarioMapper;
import org.example.clientes.model.Usuario;
import org.example.rest.UserApiRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Clase UserRemoteRepository
 *
 * Esta clase gestiona la interacción con la API remota de usuarios a través de llamadas sincrónicas.
 * Utiliza la clase UserApiRest para realizar las peticiones HTTP a la API y convierte los resultados
 * en objetos Usuario mediante el UsuarioMapper.
 *
 * Cada operación realiza una llamada sincrónica a la API y maneja los errores correspondientes,
 * registrando las operaciones a través de SLF4J.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo
 * @version 1.0-SNAPSHOT
 */
public class UserRemoteRepository {

    private final UserApiRest userApiRest;
    private final Logger logger = LoggerFactory.getLogger(UserRemoteRepository.class);

    /**
     * Constructor que recibe una instancia de UserApiRest.
     *
     * @param userApiRest La interfaz que expone los métodos para interactuar con la API REST.
     */
    public UserRemoteRepository(UserApiRest userApiRest) {
        this.userApiRest = userApiRest;
    }

    /**
     * Recupera todos los usuarios de la API de forma sincrónica.
     *
     * @return Una lista opcional de usuarios si la operación es exitosa, o Optional.empty() si falla.
     */
    public Optional<List<Usuario>> getAllSync(){
        logger.debug("UserRemoteRepository: Devolviendo todos los usuarios de la API");
        var call = userApiRest.getAllSync();
        try {
            var response = call.execute();
            if (!response.isSuccessful()) {
                logger.error("Error recuperando todos los usuarios: " + response.code());
                return Optional.empty();
            }
            var body = response.body();
            if (body != null) {
                return Optional.of(body.stream()
                        .map(UsuarioMapper::toUserFromCreate)
                        .toList());
            } else {
                logger.error("Response body es null");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error recuperando todos los usuarios de la API", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Recupera un usuario por su ID de forma sincrónica.
     *
     * @param id El ID del usuario a recuperar.
     * @return El usuario si existe, o Optional.empty() si no se encuentra o ocurre un error.
     */
    public Optional<Usuario> getByIdSync(long id) {
        logger.debug("UserRemoteRepository: Recuperando el usuario con id " + id);
        var call = userApiRest.getByIdSync(id);
        try {
            var response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    logger.debug("UserRemoteRepository: Error, usuario no encontrado con id " + id);
                    return Optional.empty();
                } else {
                    throw new Exception("Error: " + response.code());
                }
            }
            return Optional.of(UsuarioMapper.toUserFromCreate(response.body()));
        } catch (Exception e) {
            logger.error("Error recuperando el usuario con id: " + id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Crea un nuevo usuario a través de la API de forma sincrónica.
     *
     * @param user El usuario a crear.
     * @return El usuario creado con marcas de tiempo si la operación es exitosa, o Optional.empty() si falla.
     */

    public Optional<Usuario> createUserSync(Usuario user) {
        logger.debug("UserRemoteRepository: Creando un nuevo usuario con username: " + user.getUserName());
        var call = userApiRest.createUserSync(UsuarioMapper.toRequest(user));
        try {
            var response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == 500) {
                    logger.debug("UserRemoteRepository: Error interno servidor" + response.code());
                    return Optional.empty();
                } else {
                    logger.debug("UserRemoteRepository: Error creando usuario " + response.code());
                    return Optional.empty();
                }
            }
            var timeStamp = LocalDateTime.now();
            return Optional.of(UsuarioMapper.toUserFromCreate(response.body(),timeStamp,timeStamp));
        } catch (Exception e) {
            logger.debug("UserRemoteRepository: Error creando usuario" + user + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Actualiza un usuario existente en la API de forma sincrónica.
     *
     * @param id   El ID del usuario a actualizar.
     * @param user Los nuevos datos del usuario.
     * @return El usuario actualizado si la operación es exitosa, o Optional.empty() si falla.
     */
    public Optional<Usuario> updateUserSync(long id, Usuario user) {
        logger.debug("UserRemoteRepository: Actualizando al usuario con id " + id);
        var call = userApiRest.updateUserSync(id, UsuarioMapper.toRequest(user));
        try {
            var response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    logger.debug("UserRemoteRepository: Error, usuario no encontrado al actualizar con id " + id);
                    return Optional.empty();
                } else {
                    logger.debug("UserRemoteRepository: Error actualizando usuario " + response.code());
                    return Optional.empty();
                }
            }
            var timeStamp = LocalDateTime.now();
            return Optional.of(UsuarioMapper.toUserFromCreate(response.body(), timeStamp));
        } catch (Exception e) {
            logger.debug("UserRemoteRepository: Error actualizando usuario con id: " + id + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Elimina un usuario por su ID de forma sincrónica.
     *
     * @param id El ID del usuario a eliminar.
     * @return El usuario eliminado si la operación es exitosa, o Optional.empty() si falla.
     */
    public Optional<Usuario> deleteUserSync(long id){
        logger.debug("UserRemoteRepository: Eliminando al usuario con id " + id);
        var call = userApiRest.deleteUserSync(id);
        try {
            var response = call.execute();
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    logger.debug("UserRemoteRepository: Error, usuario no encontrado al eliminar con id " + id);
                    return Optional.empty();
                } else {
                    logger.debug("UserRemoteRepository: Error eliminando usuario " + response.code());
                    return Optional.empty();
                }
            }
            return Optional.of(UsuarioMapper.toUserFromDelete(response.body()));
        } catch (Exception e) {
            logger.debug("UserRemoteRepository: Error eliminando usuario con id: " + id + e.getMessage());
            return Optional.empty();
        }
    }
}
