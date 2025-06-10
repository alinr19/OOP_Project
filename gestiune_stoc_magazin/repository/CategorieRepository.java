package gestiune_stoc_magazin.repository;

import gestiune_stoc_magazin.model.Categorie;
import gestiune_stoc_magazin.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieRepository {
    private static CategorieRepository instance;

    private CategorieRepository() {}

    public static synchronized CategorieRepository getInstance() {
        if (instance == null) {
            instance = new CategorieRepository();
        }
        return instance;
    }

    public void create(Categorie categorie) {
        String sql = "INSERT INTO categorii (id_categorie, nume_categorie, descriere) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate crea categoria, conexiunea la BD este nula.");
                return;
            }

            pstmt.setString(1, categorie.getIdCategorie());
            pstmt.setString(2, categorie.getNumeCategorie());
            pstmt.setString(3, categorie.getDescriere());
            pstmt.executeUpdate();
            System.out.println("INFO DB: Categoria '" + categorie.getNumeCategorie() + "' a fost adaugata in baza de date.");

        } catch (SQLException e) {
            System.err.println("EROARE SQL la adaugarea categoriei '" + categorie.getNumeCategorie() + "': " + e.getMessage());
            if (e.getSQLState().equals("23505")) { 
                 System.err.println("DETALIU: O categorie cu ID-ul '" + categorie.getIdCategorie() + "' sau numele '" + categorie.getNumeCategorie() + "' probabil exista deja.");
            }
            // e.printStackTrace(); // Decomenteaza pentru debugging detaliat
        }
    }

    public Categorie read(String idCategorie) {
        String sql = "SELECT id_categorie, nume_categorie, descriere FROM categorii WHERE id_categorie = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate citi categoria, conexiunea la BD este nula.");
                return null;
            }
            
            pstmt.setString(1, idCategorie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Categorie(
                    rs.getString("id_categorie"),
                    rs.getString("nume_categorie"),
                    rs.getString("descriere")
                );
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la citirea categoriei cu ID '" + idCategorie + "': " + e.getMessage());
            // e.printStackTrace();
        }
        return null;
    }

    public List<Categorie> readAll() {
        List<Categorie> categorii = new ArrayList<>();
        String sql = "SELECT id_categorie, nume_categorie, descriere FROM categorii ORDER BY nume_categorie";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se pot citi categoriile, conexiunea la BD este nula.");
                return categorii; 
            }

            while (rs.next()) {
                categorii.add(new Categorie(
                    rs.getString("id_categorie"),
                    rs.getString("nume_categorie"),
                    rs.getString("descriere")
                ));
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la citirea tuturor categoriilor: " + e.getMessage());
            // e.printStackTrace();
        }
        return categorii;
    }

    public void update(Categorie categorie) {
        String sql = "UPDATE categorii SET nume_categorie = ?, descriere = ? WHERE id_categorie = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate actualiza categoria, conexiunea la BD este nula.");
                return;
            }

            pstmt.setString(1, categorie.getNumeCategorie());
            pstmt.setString(2, categorie.getDescriere());
            pstmt.setString(3, categorie.getIdCategorie());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("INFO DB: Categoria cu ID '" + categorie.getIdCategorie() + "' a fost actualizata.");
            } else {
                System.out.println("INFO DB: Categoria cu ID '" + categorie.getIdCategorie() + "' nu a fost gasita pentru actualizare.");
            }

        } catch (SQLException e) {
            System.err.println("EROARE SQL la actualizarea categoriei '" + categorie.getNumeCategorie() + "': " + e.getMessage());
             if (e.getSQLState().equals("23505")) { 
                 System.err.println("DETALIU: Noul nume '" + categorie.getNumeCategorie() + "' probabil exista deja pentru alta categorie.");
            }
            // e.printStackTrace();
        }
    }

    public void delete(String idCategorie) {
        String sql = "DELETE FROM categorii WHERE id_categorie = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate sterge categoria, conexiunea la BD este nula.");
                return;
            }

            pstmt.setString(1, idCategorie);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("INFO DB: Categoria cu ID '" + idCategorie + "' a fost stearsa.");
            } else {
                System.out.println("INFO DB: Categoria cu ID '" + idCategorie + "' nu a fost gasita pentru stergere.");
            }

        } catch (SQLException e) {
            System.err.println("EROARE SQL la stergerea categoriei cu ID '" + idCategorie + "': " + e.getMessage());
            if (e.getSQLState().equals("23503")) { 
                System.err.println("DETALIU: Categoria nu poate fi stearsa deoarece exista produse asociate cu ea.");
            }
            // e.printStackTrace();
        }
    }
}