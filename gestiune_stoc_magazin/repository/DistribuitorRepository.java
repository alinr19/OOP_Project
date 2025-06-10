package gestiune_stoc_magazin.repository;

import gestiune_stoc_magazin.model.Adresa;
import gestiune_stoc_magazin.model.Distribuitor;
import gestiune_stoc_magazin.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistribuitorRepository {
    private static DistribuitorRepository instance;
    private AdresaRepository adresaRepository = AdresaRepository.getInstance();

    private DistribuitorRepository() {}

    public static synchronized DistribuitorRepository getInstance() {
        if (instance == null) {
            instance = new DistribuitorRepository();
        }
        return instance;
    }

    public void create(Distribuitor distribuitor) {
        Integer idAdresaFk = null;
        if (distribuitor.getAdresa() != null) {
            Adresa adresaDeSalvat = distribuitor.getAdresa();
            Adresa adresaSalvata = adresaRepository.create(adresaDeSalvat);
            if (adresaSalvata != null && adresaSalvata.getIdAdresa() != 0) { 
                idAdresaFk = adresaSalvata.getIdAdresa();
                distribuitor.setAdresa(adresaSalvata); 
            } else {
                System.err.println("EROARE DB: Nu s-a putut salva/obtine ID-ul pentru adresa distribuitorului '" + distribuitor.getNumeDistribuitor() + "'. Distribuitorul va fi salvat fara adresa.");
            }
        }

        String sql = "INSERT INTO distribuitori (id_distribuitor, nume_distribuitor, persoana_contact, telefon, id_adresa_fk) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate crea distribuitorul, conexiunea la BD este nula.");
                return;
            }

            pstmt.setString(1, distribuitor.getIdDistribuitor());
            pstmt.setString(2, distribuitor.getNumeDistribuitor());
            pstmt.setString(3, distribuitor.getPersoanaContact());
            pstmt.setString(4, distribuitor.getTelefon());
            if (idAdresaFk != null) {
                pstmt.setInt(5, idAdresaFk);
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            System.out.println("INFO DB: Distribuitorul '" + distribuitor.getNumeDistribuitor() + "' a fost adaugat in baza de date.");

        } catch (SQLException e) {
            System.err.println("EROARE SQL la adaugarea distribuitorului '" + distribuitor.getNumeDistribuitor() + "': " + e.getMessage());
            if (e.getSQLState().equals("23505")) { 
                 System.err.println("DETALIU: Un distribuitor cu ID-ul '" + distribuitor.getIdDistribuitor() + "' probabil exista deja.");
            }
            // e.printStackTrace();
        }
    }

    public Distribuitor read(String idDistribuitor) {
        String sql = "SELECT id_distribuitor, nume_distribuitor, persoana_contact, telefon, id_adresa_fk FROM distribuitori WHERE id_distribuitor = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) { /* ... */ return null; }

            pstmt.setString(1, idDistribuitor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Adresa adresa = null;
                int idAdresaFk = rs.getInt("id_adresa_fk");
                if (!rs.wasNull()) { 
                    adresa = adresaRepository.read(idAdresaFk);
                }
                return new Distribuitor(
                    rs.getString("id_distribuitor"),
                    rs.getString("nume_distribuitor"),
                    rs.getString("persoana_contact"),
                    rs.getString("telefon"),
                    adresa
                );
            }
        } catch (SQLException e) { /* ... */ }
        return null;
    }

    public List<Distribuitor> readAll() {
        List<Distribuitor> distribuitori = new ArrayList<>();
        String sql = "SELECT id_distribuitor, nume_distribuitor, persoana_contact, telefon, id_adresa_fk FROM distribuitori ORDER BY nume_distribuitor";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (connection == null) { /* ... */ return distribuitori; }
            
            while (rs.next()) {
                Adresa adresa = null;
                int idAdresaFk = rs.getInt("id_adresa_fk");
                if (!rs.wasNull()) {
                    adresa = adresaRepository.read(idAdresaFk);
                }
                distribuitori.add(new Distribuitor(
                    rs.getString("id_distribuitor"),
                    rs.getString("nume_distribuitor"),
                    rs.getString("persoana_contact"),
                    rs.getString("telefon"),
                    adresa
                ));
            }
        } catch (SQLException e) { /* ... */ }
        return distribuitori;
    }

    public void update(Distribuitor distribuitor) {
        // Pas 1: Gestioneaza Adresa (creare noua daca nu are ID, update daca are ID si s-a schimbat)
        Integer idAdresaFk = null;
        if (distribuitor.getAdresa() != null) {
            Adresa adr = distribuitor.getAdresa();
            if (adr.getIdAdresa() != 0) { // Adresa exista si are ID, facem update
                adresaRepository.update(adr);
                idAdresaFk = adr.getIdAdresa();
            } else { // Adresa este noua, o cream
                Adresa adresaSalvata = adresaRepository.create(adr);
                if (adresaSalvata != null && adresaSalvata.getIdAdresa() != 0) {
                    idAdresaFk = adresaSalvata.getIdAdresa();
                    distribuitor.setAdresa(adresaSalvata); // Seteaza adresa cu ID pe obiectul distribuitor
                }
            }
        }
        // Daca adresa a fost stearsa (setata la null pe obiectul distribuitor), idAdresaFk ramane null.

        String sql = "UPDATE distribuitori SET nume_distribuitor = ?, persoana_contact = ?, telefon = ?, id_adresa_fk = ? WHERE id_distribuitor = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) { /* ... */ return; }

            pstmt.setString(1, distribuitor.getNumeDistribuitor());
            pstmt.setString(2, distribuitor.getPersoanaContact());
            pstmt.setString(3, distribuitor.getTelefon());
            if (idAdresaFk != null) {
                pstmt.setInt(4, idAdresaFk);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, distribuitor.getIdDistribuitor());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("INFO DB: Distribuitorul '" + distribuitor.getNumeDistribuitor() + "' a fost actualizat.");
            } else {
                 System.out.println("INFO DB: Distribuitorul cu ID '" + distribuitor.getIdDistribuitor() + "' nu a fost gasit pentru actualizare.");
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la actualizarea distribuitorului '" + distribuitor.getNumeDistribuitor() + "': " + e.getMessage());
            // e.printStackTrace();
        }
    }

    public void delete(String idDistribuitor) {

        String sql = "DELETE FROM distribuitori WHERE id_distribuitor = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) { /* ... */ return; }

            pstmt.setString(1, idDistribuitor);
            int affectedRows = pstmt.executeUpdate();
             if (affectedRows > 0) {
                System.out.println("INFO DB: Distribuitorul cu ID '" + idDistribuitor + "' a fost sters.");
            } else {
                System.out.println("INFO DB: Distribuitorul cu ID '" + idDistribuitor + "' nu a fost gasit pentru stergere.");
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la stergerea distribuitorului cu ID '" + idDistribuitor + "': " + e.getMessage());
            // e.printStackTrace();
        }
    }
}