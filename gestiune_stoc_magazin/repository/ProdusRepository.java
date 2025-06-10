package gestiune_stoc_magazin.repository;

import gestiune_stoc_magazin.model.*;
import gestiune_stoc_magazin.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdusRepository {
    private static ProdusRepository instance;

    private CategorieRepository categorieRepository = CategorieRepository.getInstance();
    // Vei avea nevoie si de DistribuitorRepository cand il implementezi
    // private DistribuitorRepository distribuitorRepository = DistribuitorRepository.getInstance();


    private ProdusRepository() {}

    public static synchronized ProdusRepository getInstance() {
        if (instance == null) {
            instance = new ProdusRepository();
        }
        return instance;
    }

    public void create(Produs produs) {
        String sql = "INSERT INTO produse (id_produs, nume_produs, pret, id_categorie_fk, id_distribuitor_fk, cantitate_stoc, tip_produs, data_expirare, garantie_luni) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate crea produsul, conexiunea la BD este nula.");
                return;
            }

            pstmt.setString(1, produs.getIdProdus());
            pstmt.setString(2, produs.getNumeProdus());
            pstmt.setDouble(3, produs.getPret());
            pstmt.setString(4, (produs.getCategorie() != null ? produs.getCategorie().getIdCategorie() : null));
            pstmt.setString(5, (produs.getDistribuitor() != null ? produs.getDistribuitor().getIdDistribuitor() : null));
            pstmt.setInt(6, produs.getCantitateStoc());

            if (produs instanceof ProdusAlimentar) {
                pstmt.setString(7, "ALIMENTAR");
                ProdusAlimentar pa = (ProdusAlimentar) produs;
                if (pa.getDataExpirare() != null) {
                    pstmt.setDate(8, Date.valueOf(pa.getDataExpirare()));
                } else {
                    pstmt.setNull(8, Types.DATE);
                }
                pstmt.setNull(9, Types.INTEGER); // garantie_luni
            } else if (produs instanceof ProdusElectronic) {
                pstmt.setString(7, "ELECTRONIC");
                ProdusElectronic pe = (ProdusElectronic) produs;
                pstmt.setNull(8, Types.DATE); // data_expirare
                pstmt.setInt(9, pe.getGarantieLuni());
            } else {
                // Presupunem un tip 'GENERAL' daca nu e nici alimentar, nici electronic
                // Sau arunca o eroare daca vrei sa fortezi un tip specific
                pstmt.setString(7, "GENERAL"); 
                pstmt.setNull(8, Types.DATE);
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            System.out.println("INFO DB: Produsul '" + produs.getNumeProdus() + "' a fost adaugat in baza de date.");

        } catch (SQLException e) {
            System.err.println("EROARE SQL la adaugarea produsului '" + produs.getNumeProdus() + "': " + e.getMessage());
            if (e.getSQLState().equals("23505")) { 
                 System.err.println("DETALIU: Un produs cu ID-ul '" + produs.getIdProdus() + "' probabil exista deja.");
            }
            // e.printStackTrace();
        }
    }

    public Produs read(String idProdus) {
        String sql = "SELECT * FROM produse WHERE id_produs = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se poate citi produsul, conexiunea la BD este nula.");
                return null;
            }
            
            pstmt.setString(1, idProdus);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToProdus(rs);
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la citirea produsului cu ID '" + idProdus + "': " + e.getMessage());
            // e.printStackTrace();
        }
        return null;
    }

    public List<Produs> readAll() {
        List<Produs> produse = new ArrayList<>();
        String sql = "SELECT * FROM produse ORDER BY nume_produs";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (connection == null) {
                System.err.println("EROARE DB: Nu se pot citi produsele, conexiunea la BD este nula.");
                return produse; 
            }

            while (rs.next()) {
                produse.add(mapRowToProdus(rs));
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la citirea tuturor produselor: " + e.getMessage());
            // e.printStackTrace();
        }
        return produse;
    }
    
    // Metoda ajutatoare pentru a mapa un rand din ResultSet la un obiect Produs
    private Produs mapRowToProdus(ResultSet rs) throws SQLException {
        String id = rs.getString("id_produs");
        String nume = rs.getString("nume_produs");
        double pret = rs.getDouble("pret");
        String idCategorie = rs.getString("id_categorie_fk");
        String idDistribuitor = rs.getString("id_distribuitor_fk");
        int cantitateStoc = rs.getInt("cantitate_stoc");
        String tipProdus = rs.getString("tip_produs");

        Categorie categorie = null;
        if (idCategorie != null) {
            categorie = categorieRepository.read(idCategorie); // Foloseste CategorieRepository
        }

        Distribuitor distribuitor = null;
        // Cand implementezi DistribuitorRepository, il vei folosi aici:
        // if (idDistribuitor != null) {
        //     distribuitor = distribuitorRepository.read(idDistribuitor);
        // }
        // Deocamdata, lasam distribuitor null sau il cream placeholder daca e nevoie urgenta
        // Pentru un exemplu complet, ar trebui sa ai DistribuitorRepository functional.
        // Ca placeholder, daca stii ca exista in BD:
        // if ("DIST01".equals(idDistribuitor)) distribuitor = new Distribuitor("DIST01", "Nume Placeholder", null, null, null);


        if ("ALIMENTAR".equals(tipProdus)) {
            Date dataExpirareSql = rs.getDate("data_expirare");
            LocalDate dataExpirare = (dataExpirareSql != null) ? dataExpirareSql.toLocalDate() : null;
            return new ProdusAlimentar(id, nume, pret, categorie, distribuitor, cantitateStoc, dataExpirare);
        } else if ("ELECTRONIC".equals(tipProdus)) {
            int garantieLuni = rs.getInt("garantie_luni");
            if (rs.wasNull()) { // Verifica daca garantia_luni era NULL in BD
                 return new ProdusElectronic(id, nume, pret, categorie, distribuitor, cantitateStoc, 0); // Sau valoare default
            }
            return new ProdusElectronic(id, nume, pret, categorie, distribuitor, cantitateStoc, garantieLuni);
        } else {
            // Pentru tipul 'GENERAL' sau alt tip neasteptat.
            // Ai nevoie de o clasa concreta ProdusGeneral sau modifici Produs sa nu fie abstracta
            // Pentru moment, returnam un ProdusAlimentar cu detalii null ca placeholder
            // Aceasta parte trebuie ajustata conform structurii claselor tale!
            System.err.println("AVERTISMENT: Tip produs '" + tipProdus + "' necunoscut sau neimplementat pentru mapare. Se returneaza ca placeholder.");
            return new ProdusAlimentar(id, nume, pret, categorie, distribuitor, cantitateStoc, null); // Placeholder!
        }
    }


    public void update(Produs produs) {
        String sql = "UPDATE produse SET nume_produs = ?, pret = ?, id_categorie_fk = ?, id_distribuitor_fk = ?, " +
                     "cantitate_stoc = ?, tip_produs = ?, data_expirare = ?, garantie_luni = ? " +
                     "WHERE id_produs = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (connection == null) { /* ... mesaj eroare ... */ return; }

            pstmt.setString(1, produs.getNumeProdus());
            pstmt.setDouble(2, produs.getPret());
            pstmt.setString(3, (produs.getCategorie() != null ? produs.getCategorie().getIdCategorie() : null));
            pstmt.setString(4, (produs.getDistribuitor() != null ? produs.getDistribuitor().getIdDistribuitor() : null));
            pstmt.setInt(5, produs.getCantitateStoc());
            
            if (produs instanceof ProdusAlimentar) {
                pstmt.setString(6, "ALIMENTAR");
                ProdusAlimentar pa = (ProdusAlimentar) produs;
                pstmt.setDate(7, (pa.getDataExpirare() != null ? Date.valueOf(pa.getDataExpirare()) : null));
                pstmt.setNull(8, Types.INTEGER);
            } else if (produs instanceof ProdusElectronic) {
                pstmt.setString(6, "ELECTRONIC");
                ProdusElectronic pe = (ProdusElectronic) produs;
                pstmt.setNull(7, Types.DATE);
                pstmt.setInt(8, pe.getGarantieLuni());
            } else {
                pstmt.setString(6, "GENERAL");
                pstmt.setNull(7, Types.DATE);
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setString(9, produs.getIdProdus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("INFO DB: Produsul cu ID '" + produs.getIdProdus() + "' a fost actualizat.");
            } else {
                System.out.println("INFO DB: Produsul cu ID '" + produs.getIdProdus() + "' nu a fost gasit pentru actualizare.");
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la actualizarea produsului '" + produs.getNumeProdus() + "': " + e.getMessage());
            // e.printStackTrace();
        }
    }

    public void delete(String idProdus) {
        String sql = "DELETE FROM produse WHERE id_produs = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            if (connection == null) { /* ... mesaj eroare ... */ return; }

            pstmt.setString(1, idProdus);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("INFO DB: Produsul cu ID '" + idProdus + "' a fost sters.");
            } else {
                System.out.println("INFO DB: Produsul cu ID '" + idProdus + "' nu a fost gasit pentru stergere.");
            }
        } catch (SQLException e) {
            System.err.println("EROARE SQL la stergerea produsului cu ID '" + idProdus + "': " + e.getMessage());
            // e.printStackTrace();
        }
    }
}