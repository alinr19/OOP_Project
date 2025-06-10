package gestiune_stoc_magazin.repository;

import gestiune_stoc_magazin.model.Adresa;
import gestiune_stoc_magazin.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresaRepository {
    private static AdresaRepository instance;

    private AdresaRepository() {}

    public static synchronized AdresaRepository getInstance() {
        if (instance == null) {
            instance = new AdresaRepository();
        }
        return instance;
    }

    public Adresa create(Adresa adresa) {
        String sql = "INSERT INTO adrese (strada, numar, oras, judet, cod_postal) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate crea adresa, conexiunea la BD este nula.");
                return null;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, adresa.getStrada());
                pstmt.setString(2, adresa.getNumar());
                pstmt.setString(3, adresa.getOras());
                pstmt.setString(4, adresa.getJudet());
                pstmt.setString(5, adresa.getCodPostal());
                
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            adresa.setIdAdresa(generatedKeys.getInt(1)); // Seteaza ID-ul generat pe obiect
                            System.out.println("INFO DB: Adresa pentru '" + adresa.getStrada() + "' a fost adaugata cu ID: " + adresa.getIdAdresa());
                            return adresa;
                        } else {
                            System.err.println("EROARE DB: Crearea adresei a reusit, dar nu s-a putut obtine ID-ul generat.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la adaugarea adresei pentru strada '" + adresa.getStrada() + "': " + e.getMessage());
            // e.printStackTrace();
        }
        return null; 
    }

    public Adresa read(int idAdresa) {
        String sql = "SELECT id_adresa, strada, numar, oras, judet, cod_postal FROM adrese WHERE id_adresa = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) { /* ... mesaj eroare ... */ return null; }
            
            pstmt.setInt(1, idAdresa);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Adresa(
                    rs.getInt("id_adresa"),
                    rs.getString("strada"),
                    rs.getString("numar"),
                    rs.getString("oras"),
                    rs.getString("judet"),
                    rs.getString("cod_postal")
                );
            }
        } catch (SQLException e) { /* ... mesaj eroare ... */ }
        return null;
    }

    public List<Adresa> readAll() {
        List<Adresa> adrese = new ArrayList<>();
        String sql = "SELECT id_adresa, strada, numar, oras, judet, cod_postal FROM adrese ORDER BY oras, strada";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (connection == null) { /* ... mesaj eroare ... */ return adrese; }
            
            while (rs.next()) {
                 adrese.add(new Adresa(
                    rs.getInt("id_adresa"),
                    rs.getString("strada"),
                    rs.getString("numar"),
                    rs.getString("oras"),
                    rs.getString("judet"),
                    rs.getString("cod_postal")
                ));
            }
        } catch (SQLException e) { /* ... mesaj eroare ... */ }
        return adrese;
    }

    public boolean update(Adresa adresa) { 
        if (adresa.getIdAdresa() == 0) { // Presupunem ca ID 0 e invalid/nesetat
            System.err.println("EROARE DB: ID Adresa invalid pentru operatia de update.");
            return false;
        }
        String sql = "UPDATE adrese SET strada = ?, numar = ?, oras = ?, judet = ?, cod_postal = ? WHERE id_adresa = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) { /* ... mesaj eroare ... */ return false; }

            pstmt.setString(1, adresa.getStrada());
            pstmt.setString(2, adresa.getNumar());
            pstmt.setString(3, adresa.getOras());
            pstmt.setString(4, adresa.getJudet());
            pstmt.setString(5, adresa.getCodPostal());
            pstmt.setInt(6, adresa.getIdAdresa());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) { /* ... mesaj eroare ... */ }
        return false;
    }

    public boolean delete(int idAdresa) {
        String sql = "DELETE FROM adrese WHERE id_adresa = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) { /* ... mesaj eroare ... */ return false; }

            pstmt.setInt(1, idAdresa);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("EROARE SQL la stergerea adresei cu ID '" + idAdresa + "': " + e.getMessage());
            if (e.getSQLState().equals("23503")) { 
                System.err.println("DETALIU: Adresa nu poate fi stearsa deoarece este folosita de unul sau mai multi distribuitori.");
            }
        }
        return false;
    }
}