package server;

import common.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://pg/studs";
    private final Connection connection;

    public DatabaseManager(String user, String password) throws SQLException {
        String dbUrl = getDatabaseUrl();
        connection = DriverManager.getConnection(dbUrl, user, password);
        connection.setAutoCommit(true);
        initializeTables();
        logger.info("Подключение к базе данных ({}) установлено", dbUrl);
    }

    static String getDatabaseUrl() {
        return getDatabaseUrl(System.getenv("DB_URL"));
    }

    static String getDatabaseUrl(String envUrl) {
        if (envUrl == null || envUrl.isBlank()) {
            return DEFAULT_DB_URL;
        }
        return envUrl.trim();
    }

    private void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username VARCHAR(255) PRIMARY KEY,
                        password_hash VARCHAR(255) NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        coordinate_x DOUBLE PRECISION NOT NULL,
                        coordinate_y REAL NOT NULL,
                        creation_date BIGINT NOT NULL,
                        price BIGINT NOT NULL,
                        unit_of_measure VARCHAR(15) NOT NULL,
                        person_name VARCHAR(255),
                        person_height INTEGER,
                        person_weight REAL,
                        creator VARCHAR(255) REFERENCES users(username) ON DELETE SET NULL
                    )
                    """);
        }
        logger.info("Таблицы базы данных инициализированы");
    }

    public synchronized boolean addUser(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            logger.info("Зарегистрирован новый пользователь: {}", username);
            return true;
        } catch (SQLException e) {// уникальный ключ нарушен => пользователь уже существует
            logger.warn("Попытка регистрации существующего пользователя: {}", username);
            return false;
        }
    }

    public synchronized String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении пароля пользователя {}: {}", username, e.getMessage());
        }
        return null;
    }

    public synchronized long countProducts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    public synchronized LinkedList<Product> loadAllProducts() throws SQLException {
        LinkedList<Product> list = new LinkedList<>();
        String sql = "SELECT * FROM products ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(createProductFromResultSet(rs));
            }
            logger.info("Загружено {} продуктов из базы данных", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка загрузки продуктов из базы данных: {}", e.getMessage());
            throw e;
        }
        return list;
    }

    public synchronized long insertProduct(Product product, String creator) throws SQLException {
        String sql = """
                INSERT INTO products
                    (name, coordinate_x, coordinate_y, creation_date, price,
                     unit_of_measure, person_name, person_height, person_weight, creator)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                RETURNING id
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getCoordinates().getX());
            ps.setFloat(3, product.getCoordinates().getY());
            ps.setLong(4, product.getCreationDate().getTime());
            ps.setLong(5, product.getPrice());
            ps.setString(6, product.getUnitOfMeasure().name());
            setPersonFields(ps, 7, product.getOwner());
            ps.setString(10, creator);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    logger.debug("Продукт добавлен в базу данных с id={}", id);
                    return id;
                } else {
                    throw new SQLException("Не удалось получить id нового продукта из sequence");
                }
            }
        }
    }

    public synchronized boolean updateProduct(long id, Product product, String creator) throws SQLException {
        String sql = """
                UPDATE products
                SET name=?, coordinate_x=?, coordinate_y=?, price=?,
                    unit_of_measure=?, person_name=?, person_height=?, person_weight=?
                WHERE id=? AND creator=?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getCoordinates().getX());
            ps.setFloat(3, product.getCoordinates().getY());
            ps.setLong(4, product.getPrice());
            ps.setString(5, product.getUnitOfMeasure().name());
            setPersonFields(ps, 6, product.getOwner());
            ps.setLong(9, id);
            ps.setString(10, creator);
            return ps.executeUpdate() > 0;
        }
    }

    public synchronized boolean deleteProduct(long id, String creator) throws SQLException {
        String sql = "DELETE FROM products WHERE id=? AND creator=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, creator);
            return ps.executeUpdate() > 0;
        }
    }

    public synchronized int deleteUserProducts(String creator) throws SQLException {
        String sql = "DELETE FROM products WHERE creator=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, creator);
            return ps.executeUpdate();
        }
    }

    public synchronized boolean isOwner(long id, String creator) throws SQLException {
        String sql = "SELECT creator FROM products WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return creator.equals(rs.getString("creator"));
            }
        }
        return false;
    }

    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        double x = rs.getDouble("coordinate_x");
        float y = rs.getFloat("coordinate_y");
        long price = rs.getLong("price");
        UnitOfMeasure unit = UnitOfMeasure.valueOf(rs.getString("unit_of_measure"));
        String creator = rs.getString("creator");

        Person owner = null;
        String personName = rs.getString("person_name");
        if (personName != null) {
            owner = new Person(personName,
                    rs.getInt("person_height"),
                    rs.getFloat("person_weight"));
        }

        Product p = new Product(name, new Coordinates(x, y), price, unit, owner);
        p.setId(rs.getLong("id"));
        p.setCreationDate(new Date(rs.getLong("creation_date")));
        p.setCreatorUsername(creator);
        return p;
    }

    private void setPersonFields(PreparedStatement ps, int startIndex, Person person) throws SQLException {
        if (person != null) {
            ps.setString(startIndex, person.getName());
            ps.setInt(startIndex + 1, person.getHeight());
            ps.setFloat(startIndex + 2, person.getWeight());
        } else {
            ps.setNull(startIndex, Types.VARCHAR);
            ps.setNull(startIndex + 1, Types.INTEGER);
            ps.setNull(startIndex + 2, Types.REAL);
        }
    }
}
