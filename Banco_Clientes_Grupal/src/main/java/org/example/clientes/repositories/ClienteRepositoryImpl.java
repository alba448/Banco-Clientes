package org.example.clientes.repositories;

import org.example.clientes.model.Cliente;
import org.example.clientes.model.Tarjeta;
import org.example.clientes.model.Usuario;
import org.example.database.LocalDataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

    /**
     * Implementación del repositorio de clientes.
     * Esta clase gestiona las operaciones CRUD para los objetos {@link Cliente}
     * en la base de datos. Utiliza {@link LocalDataBaseManager} para manejar
     * la conexión y las operaciones de la base de datos.
     *
     * <p>Esta implementación permite obtener todos los clientes, buscar un cliente
     * por ID, crear, actualizar y eliminar clientes, así como eliminar todos los clientes.</p>
     *
     * @see Cliente
     * @see Usuario
     * @see Tarjeta
     *
     * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo
     * @version 1.0-SNAPSHOT
     */
public class ClienteRepositoryImpl implements ClienteRepository {
    private final Logger logger = LoggerFactory.getLogger(ClienteRepositoryImpl.class);
    private final LocalDataBaseManager dataBaseManager;

    /**
     * Constructor de la clase.
     *
     * @param dataBaseManager el gestor de la base de datos utilizado para las operaciones de conexión y consultas.
     */
    public ClienteRepositoryImpl(LocalDataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    /**
     * Obtiene todos los clientes de la base de datos.
     *
     * @return una lista de {@link Cliente} que contiene todos los clientes.
     * Si no hay clientes, se devuelve una lista vacía.
     */
    public List<Cliente> getAll() {
        logger.info("Obteniendo clientes...");
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT u.id AS usuarioId, u.nombre, u.userName, u.email, u.createdAt AS usuarioCreatedAt, u.updatedAt AS usuarioUpdatedAt," +
                        "t.id AS tarjetaId, t.numeroTarjeta, t.nombreTitular, t.fechaCaducidad, t.createdAt AS tarjetaCreatedAt, t.updatedAt AS tarjetaUpdatedAt " +
                        "FROM Usuario u LEFT JOIN Tarjeta t ON u.nombre = t.nombreTitular";

        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Usuario usuario = Usuario.builder()
                        .id(resultSet.getLong("usuarioId"))
                        .nombre(resultSet.getString("nombre"))
                        .userName(resultSet.getString("userName"))
                        .email(resultSet.getString("email"))
                        .createdAt(resultSet.getObject("usuarioCreatedAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("usuarioUpdatedAt", LocalDateTime.class))
                        .build();

                Tarjeta tarjeta = null;
                if (resultSet.getString("tarjetaId") != null) {
                    tarjeta =Tarjeta.builder()
                            .id(resultSet.getLong("tarjetaId"))
                            .numeroTarjeta(resultSet.getString("numeroTarjeta"))
                            .nombreTitular(resultSet.getString("nombreTitular"))
                            .fechaCaducidad(resultSet.getObject("fechaCaducidad", LocalDate.class))
                            .createdAt(resultSet.getObject("tarjetaCreatedAt", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("tarjetaUpdatedAt", LocalDateTime.class))
                            .build();
                }

                Cliente cliente = Cliente.builder()
                        .id(resultSet.getLong("usuarioId"))
                        .usuario(usuario)
                        .tarjeta(Collections.singletonList(tarjeta))
                        .createdAt(resultSet.getObject("usuarioCreatedAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("usuarioUpdatedAt", LocalDateTime.class))
                        .build();

                clientes.add(cliente);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener clientes", e);
        }

        return clientes;
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id el ID del cliente a buscar.
     * @return un {@link Optional} que contiene el cliente si se encuentra, o un {@link Optional#empty()} si no se encuentra.
     */
    @Override
    public Optional<Cliente> getById(long id) {
        logger.info("Obteniendo cliente por id...");
        String query = "SELECT u.id AS usuarioId, u.nombre, u.userName, u.email, u.createdAt AS usuarioCreatedAt, u.updatedAt AS usuarioUpdatedAt,"
                + " t.id AS tarjetaId, t.numeroTarjeta, t.nombreTitular, t.fechaCaducidad, t.createdAt AS tarjetaCreatedAt, t.updatedAt AS tarjetaUpdatedAt"
                + " FROM Usuario u LEFT JOIN Tarjeta t ON u.nombre = t.nombreTitular WHERE u.id = ?";
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Usuario usuario = Usuario.builder()
                            .id(resultSet.getLong("usuarioId"))
                            .nombre(resultSet.getString("nombre"))
                            .userName(resultSet.getString("userName"))
                            .email(resultSet.getString("email"))
                            .createdAt(resultSet.getObject("usuarioCreatedAt", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("usuarioUpdatedAt", LocalDateTime.class))
                            .build();
                    Tarjeta tarjeta = null;
                    if (resultSet.getString("tarjetaId") != null) {
                        tarjeta = Tarjeta.builder()
                                .id(Long.valueOf(resultSet.getString("tarjetaId")))
                                .numeroTarjeta(resultSet.getString("numeroTarjeta"))
                                .nombreTitular(resultSet.getString("nombreTitular"))
                                .fechaCaducidad(resultSet.getObject("fechaCaducidad", LocalDate.class))
                                .createdAt(resultSet.getObject("tarjetaCreatedAt", LocalDateTime.class))
                                .updatedAt(resultSet.getObject("tarjetaUpdatedAt", LocalDateTime.class))
                                .build();
                    }
                    return Optional.of(Cliente.builder()
                            .id(resultSet.getLong("usuarioId"))
                            .usuario(usuario)
                            .tarjeta(Collections.singletonList(tarjeta))
                            .createdAt(resultSet.getObject("usuarioCreatedAt", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("usuarioUpdatedAt", LocalDateTime.class))
                            .build());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por id", e);
        }
        return Optional.empty();
    }

    /**
     * Crea un nuevo cliente en la base de datos.
     *
     * @param cliente el cliente a crear.
     * @return el cliente creado con sus ID asignados.
     */
    @Override
    public Cliente create(Cliente cliente) {
        logger.info("Creando cliente...");

        String userQuery = "INSERT INTO Usuario (nombre, userName, email, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)";
        String tarjetaQuery = "INSERT INTO Tarjeta (numeroTarjeta, nombreTitular, fechaCaducidad, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)";

        LocalDateTime timeStamp = LocalDateTime.now();

        try (Connection connection = dataBaseManager.connect()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statementUsuario = connection.prepareStatement(userQuery)) {
                statementUsuario.setString(1, cliente.getUsuario().getNombre());
                statementUsuario.setString(2, cliente.getUsuario().getUserName());
                statementUsuario.setString(3, cliente.getUsuario().getEmail());
                statementUsuario.setObject(4, timeStamp);
                statementUsuario.setObject(5, timeStamp);

                statementUsuario.executeUpdate();

                try (ResultSet generatedKeys = statementUsuario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long clienteId = generatedKeys.getLong(1);
                        cliente.getUsuario().setId(clienteId);
                        cliente.setId(clienteId);
                    } else {
                        throw new SQLException("No se pudo obtener la clave generada para Usuario.");
                    }
                }
            }

            if (cliente.getTarjeta() != null && !cliente.getTarjeta().isEmpty()) {
                for (Tarjeta tarjeta : cliente.getTarjeta()) {
                    try (PreparedStatement statementTarjeta = connection.prepareStatement(tarjetaQuery, Statement.RETURN_GENERATED_KEYS)) {
                        statementTarjeta.setString(1, tarjeta.getNumeroTarjeta());
                        statementTarjeta.setString(2, tarjeta.getNombreTitular());
                        statementTarjeta.setObject(3, tarjeta.getFechaCaducidad());
                        statementTarjeta.setObject(4, timeStamp);
                        statementTarjeta.setObject(5, timeStamp);
                        statementTarjeta.executeUpdate();

                        try (ResultSet generatedKeys = statementTarjeta.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                tarjeta.setId(generatedKeys.getLong(1));
                            } else {
                                throw new SQLException("No se pudo obtener la clave generada para Tarjeta.");
                            }
                        }
                    }
                }
            }

            connection.commit();

            cliente.setCreatedAt(timeStamp);
            cliente.setUpdatedAt(timeStamp);

            return cliente;

        } catch (SQLException e) {
            logger.error("Error al crear cliente", e);
        }

        return cliente;
    }

    @Override
    public Cliente update(long id, Cliente cliente) {
        logger.info("Actualizando cliente...");

        String userQuery = "UPDATE Usuario SET nombre = ?, userName = ?, email = ?, updatedAt = ? WHERE id = ?";
        String tarjetaQuery = "UPDATE Tarjeta SET numeroTarjeta = ?, nombreTitular = ?, fechaCaducidad = ?, updatedAt = ? WHERE nombreTitular = ?";

        LocalDateTime timeStamp = LocalDateTime.now();

        try (Connection connection = dataBaseManager.connect()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statementUsuario = connection.prepareStatement(userQuery)) {
                statementUsuario.setString(1, cliente.getUsuario().getNombre());
                statementUsuario.setString(2, cliente.getUsuario().getUserName());
                statementUsuario.setString(3, cliente.getUsuario().getEmail());
                statementUsuario.setObject(4, timeStamp);
                statementUsuario.setLong(5, id);
                statementUsuario.executeUpdate();
            }

            if (cliente.getTarjeta() != null && !cliente.getTarjeta().isEmpty()) {
                for (Tarjeta tarjeta : cliente.getTarjeta()){
                    try (PreparedStatement statementTarjeta = connection.prepareStatement(tarjetaQuery)) {
                        statementTarjeta.setString(1, tarjeta.getNumeroTarjeta());
                        statementTarjeta.setString(2, tarjeta.getNombreTitular());
                        statementTarjeta.setObject(3, tarjeta.getFechaCaducidad());
                        statementTarjeta.setObject(4, timeStamp);
                        statementTarjeta.setString(5, cliente.getUsuario().getNombre());
                        statementTarjeta.executeUpdate();
                    }
                }
            }

            connection.commit();

            cliente.setUpdatedAt(timeStamp);
            return cliente;

        } catch (SQLException e) {
            logger.error("Error al actualizar cliente", e);
        }

        return null;
    }

    @Override
    public boolean delete(long id) {
        logger.info("Borrando cliente...");

        String deleteUsuarioQuery = "DELETE FROM Usuario WHERE id = ?";
        String deleteTarjetaQuery = "DELETE FROM Tarjeta WHERE nombreTitular = ?";

        try (Connection connection = dataBaseManager.connect()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statementTarjeta = connection.prepareStatement(deleteTarjetaQuery)) {
                statementTarjeta.setLong(1, id);
                statementTarjeta.executeUpdate();
            }

            try (PreparedStatement statementUsuario = connection.prepareStatement(deleteUsuarioQuery)) {
                statementUsuario.setLong(1, id);
                int rows = statementUsuario.executeUpdate();

                if (rows > 0) {
                    connection.commit();
                    return true;
                } else {
                    logger.warn("No se ha borrado ningún cliente");
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al borrar cliente", e);
        }

        return false;
    }

    @Override
    public boolean deleteAll() {
        logger.info("Borrando todos los usuarios...");

        String usuarioQuery = "DELETE FROM Usuario";
        String tarjetaQuery = "DELETE FROM Tarjeta";

        try (Connection connection = dataBaseManager.connect()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statementTarjeta = connection.prepareStatement(tarjetaQuery)) {
                statementTarjeta.executeUpdate();
            }

            try (PreparedStatement statementUsuario = connection.prepareStatement(usuarioQuery)) {
                int rows = statementUsuario.executeUpdate();

                if (rows > 0) {
                    connection.commit();
                    return true;
                } else {
                    logger.warn("No se ha borrado ningún usuario");
                    connection.commit();
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al borrar todos los usuarios", e);
        }

        return false;
    }
}
