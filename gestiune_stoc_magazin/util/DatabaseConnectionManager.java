package gestiune_stoc_magazin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionManager {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "gestiune_stoc_db";
    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String DB_USER = "gestiune_app_user";
    private static final String DB_PASSWORD = "alin1908"; 

    private static DatabaseConnectionManager instance;
    private Connection connection;

    private DatabaseConnectionManager() {
        try {
            // Class.forName("org.postgresql.Driver"); // Opțional pentru JDBC 4.0+
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("INFO: Conexiune la PostgreSQL stabilita cu succes!");
        } catch (SQLException e) {
            System.err.println("EROARE SQL la conectarea initiala la PostgreSQL: " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
        }
    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null || instance.connection == null) {
            instance = new DatabaseConnectionManager();
            if (instance.connection == null) { 
                System.err.println("EROARE CRITICA: Nu s-a putut stabili conexiunea la baza de date in getInstance.");
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("INFO: Conexiunea la BD este nula sau inchisa. Se incearca re-conectarea...");
                this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("INFO: Re-conectare la PostgreSQL reusita!");
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la obtinerea/re-conectarea la PostgreSQL: " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("INFO: Conexiunea la PostgreSQL a fost inchisa.");
            }
        } catch (SQLException e) {
            System.err.println("EROARE la inchiderea conexiunii PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Metoda de initializare schema e optionala, mai ales daca ai creat tabelele manual
    public static void initializeDatabaseSchema(Connection conn) {
        if (conn == null) {
            System.err.println("EROARE: Nu se poate initializa schema, conexiunea la BD este nula.");
            return;
        }
        String createTestTableSQL = "CREATE TABLE IF NOT EXISTS test_initializare_schema (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "mesaj VARCHAR(255) NOT NULL, " +
                                    "data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                    ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTestTableSQL);
            System.out.println("INFO: Schema bazei de date initializata/verificata (test_initializare_schema).");
        } catch (SQLException e) {
            System.err.println("EROARE la initializarea schemei BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}