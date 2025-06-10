package gestiune_stoc_magazin.service;

import gestiune_stoc_magazin.model.*;
import gestiune_stoc_magazin.repository.CategorieRepository;
import gestiune_stoc_magazin.repository.ProdusRepository;
import gestiune_stoc_magazin.repository.DistribuitorRepository;
// import gestiune_stoc_magazin.repository.AdresaRepository; // Folosit indirect prin DistribuitorRepository

import gestiune_stoc_magazin.audit.AuditService; // Asigura-te ca ai acest import

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiciuGestiuneStoc {
    private CategorieRepository categorieRepository;
    private ProdusRepository produsRepository;
    private DistribuitorRepository distribuitorRepository;
    
    private AuditService auditService;

    // Helper pentru normalizare string-uri (eliminare diacritice pentru consola)
    private String normalizeString(String text) {
        if (text == null) return "N/A";
        return text.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                   .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
    }

    public ServiciuGestiuneStoc() {
        this.categorieRepository = CategorieRepository.getInstance();
        this.produsRepository = ProdusRepository.getInstance();
        this.distribuitorRepository = DistribuitorRepository.getInstance();
        this.auditService = AuditService.getInstance();
    }

    // --- Operații pentru Produse ---
    public void adaugaProdus(Produs produs) {
        String idProdusLog = (produs != null ? produs.getIdProdus() : "null");
        auditService.logAction("Initiere Adaugare Produs: ID=" + idProdusLog);

        if (produs == null) {
            System.out.println("EROARE: Incercare de adaugare produs null.");
            auditService.logAction("Esec Adaugare Produs: Input null");
            return;
        }
        if (produsRepository.read(produs.getIdProdus()) != null) {
             System.out.println("AVERTISMENT: Produsul cu ID " + produs.getIdProdus() + " (" + normalizeString(produs.getNumeProdus()) + ") exista deja in BD. Nu a fost adaugat.");
             auditService.logAction("Esec Adaugare Produs (ID Duplicat): " + produs.getIdProdus());
             return;
        }
        
        // Asiguram ca obiectele Categorie si Distribuitor din Produs sunt cele din BD (sau null)
        // si ca acestea exista in BD inainte de a le asocia.
        if (produs.getCategorie() != null && produs.getCategorie().getIdCategorie() != null) {
            Categorie catDinDB = categorieRepository.read(produs.getCategorie().getIdCategorie());
            if (catDinDB == null) {
                System.err.println("EROARE: Categoria cu ID '" + produs.getCategorie().getIdCategorie() + "' specificata pentru produs nu exista in BD. Produsul '" + normalizeString(produs.getNumeProdus()) + "' nu va fi adaugat.");
                auditService.logAction("Esec Adaugare Produs (Categorie Inexistenta): " + produs.getCategorie().getIdCategorie() + " pentru Produs ID=" + idProdusLog);
                return;
            }
            produs.setCategorie(catDinDB);
        }

        if (produs.getDistribuitor() != null && produs.getDistribuitor().getIdDistribuitor() != null) {
            Distribuitor distDinDB = distribuitorRepository.read(produs.getDistribuitor().getIdDistribuitor());
            if (distDinDB == null) {
                System.err.println("INFO: Distribuitorul cu ID '" + produs.getDistribuitor().getIdDistribuitor() + "' specificat pentru produs nu exista in BD. Produsul '" + normalizeString(produs.getNumeProdus()) + "' va fi adaugat fara distribuitor.");
                auditService.logAction("Info Adaugare Produs (Distribuitor Inexistent): " + produs.getDistribuitor().getIdDistribuitor() + " pentru Produs ID=" + idProdusLog + ". Se adauga fara distribuitor.");
                produs.setDistribuitor(null); // Important pentru a nu avea FK invalid
            } else {
                 produs.setDistribuitor(distDinDB);
            }
        }
        
        produsRepository.create(produs);
        auditService.logAction("Finalizare Adaugare Produs: " + produs.getIdProdus() + ", Nume=" + normalizeString(produs.getNumeProdus()));
    }

    public Produs getProdusById(String idProdus) {
        auditService.logAction("Cautare Produs dupa ID: " + idProdus);
        return produsRepository.read(idProdus);
    }

    public void actualizeazaStocProdus(String idProdus, int cantitateNoua) {
        auditService.logAction("Initiere Actualizare Stoc Produs: " + idProdus + " -> " + cantitateNoua);
        Produs p = produsRepository.read(idProdus);
        if (p != null) {
            p.setCantitateStoc(cantitateNoua);
            produsRepository.update(p); // Presupune ca update() din repository afiseaza mesajul de succes/esec
            auditService.logAction("Finalizare Actualizare Stoc Produs: " + idProdus); // Sau logheaza succes/esec pe baza return-ului din update
        } else {
            auditService.logAction("Esec Actualizare Stoc Produs (ID Inexistent): " + idProdus);
            System.out.println("EROARE: Produsul cu ID " + idProdus + " nu a fost gasit in BD pentru actualizare stoc.");
        }
    }
    
    public boolean stergeProdus(String idProdus) {
        auditService.logAction("Initiere Stergere Produs: ID=" + idProdus);
        Produs p = produsRepository.read(idProdus); // Verificam daca exista inainte de a incerca stergerea
        if (p != null) {
            produsRepository.delete(idProdus);
            // Mesajul de succes/eroare este in repository.delete()
            auditService.logAction("Finalizare Stergere Produs: ID=" + idProdus);
            return true; 
        }
        auditService.logAction("Esec Stergere Produs (ID Inexistent): ID=" + idProdus);
        System.out.println("AVERTISMENT: Produsul cu ID " + idProdus + " nu a fost gasit in BD pentru stergere.");
        return false;
    }

    public void afiseazaToateProdusele() {
        auditService.logAction("Afisare Toate Produsele");
        System.out.println("\n------------- LISTA PRODUSE (din Baza de Date) -------------");
        List<Produs> produseDinDB = produsRepository.readAll();
        if (produseDinDB.isEmpty()) {
            System.out.println("INFO: Nu exista produse inregistrate in baza de date.");
        } else {
            for (int i = 0; i < produseDinDB.size(); i++) {
                System.out.println(produseDinDB.get(i).toString());
                if (i < produseDinDB.size() - 1) {
                    System.out.println("---------------------------------------"); 
                }
            }
        }
        System.out.println("---------------------------------------"); 
        System.out.println(); 
    }

    public List<Produs> getProduseDinCategorie(String idCategorie) {
        auditService.logAction("Cautare Produse din Categorie: ID=" + idCategorie);
        // Ideal, aceasta ar fi o metoda in ProdusRepository: readByCategoryId(idCategorie)
        // Filtrare temporara in memorie (ineficienta pentru seturi mari de date)
        List<Produs> allProducts = produsRepository.readAll();
        if (allProducts.isEmpty() && categorieRepository.read(idCategorie) == null) {
            // System.out.println("INFO: Categoria cu ID " + idCategorie + " nu exista sau nu sunt produse.");
            return new ArrayList<>(); // Returneaza lista goala
        }
        return allProducts.stream()
                .filter(p -> p.getCategorie() != null && p.getCategorie().getIdCategorie().equals(idCategorie))
                .collect(Collectors.toList());
    }
    
    public List<Produs> getProduseCuStocRedus(int limitaInferioara) {
        auditService.logAction("Cautare Produse cu Stoc Redus: Limita=" + limitaInferioara);
        // Ideal, aceasta ar fi o metoda in ProdusRepository: readWithStockBelow(limita)
        List<Produs> allProducts = produsRepository.readAll();
        return allProducts.stream()
                .filter(p -> p.getCantitateStoc() < limitaInferioara)
                .collect(Collectors.toList());
    }

    // --- Operații pentru Categorii ---
    public void adaugaCategorie(Categorie categorie) {
        String idCatLog = (categorie != null ? categorie.getIdCategorie() : "null");
        auditService.logAction("Initiere Adaugare Categorie: ID=" + idCatLog);
        if (categorie == null) { 
            System.out.println("EROARE: Incercare de adaugare categorie null.");
            auditService.logAction("Esec Adaugare Categorie: Input null");
            return; 
        }
        if (categorieRepository.read(categorie.getIdCategorie()) != null) {
             System.out.println("AVERTISMENT: O categorie cu ID-ul '" + categorie.getIdCategorie() + "' exista deja in BD. Categoria '" + normalizeString(categorie.getNumeCategorie()) + "' nu a fost adaugata.");
            auditService.logAction("Esec Adaugare Categorie (ID Duplicat): " + categorie.getIdCategorie());
            return;
        }
        categorieRepository.create(categorie);
        auditService.logAction("Finalizare Adaugare Categorie: " + categorie.getIdCategorie() + ", Nume=" + normalizeString(categorie.getNumeCategorie()));
    }

    public void afiseazaToateCategoriile() {
        auditService.logAction("Afisare Toate Categoriile");
        System.out.println("\n------- LISTA CATEGORII (din Baza de Date) -------");
        List<Categorie> categorii = categorieRepository.readAll(); 
        if (categorii.isEmpty()) { System.out.println("INFO: Nu exista categorii inregistrate in baza de date."); }
        else { 
            Iterator<Categorie> iterator = categorii.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().toString()); 
                 if (iterator.hasNext()) {
                    System.out.println("......................................."); 
                }
            }
        }
        System.out.println("......................................."); 
        System.out.println(); 
    }
    
    public Categorie getCategorieById(String idCategorie) {
        auditService.logAction("Cautare Categorie dupa ID: " + idCategorie);
        return categorieRepository.read(idCategorie);
    }

    // --- Operații pentru Distribuitori ---
    public void adaugaDistribuitor(Distribuitor distribuitor) {
        String idDistLog = (distribuitor != null ? distribuitor.getIdDistribuitor() : "null");
        auditService.logAction("Initiere Adaugare Distribuitor: ID=" + idDistLog);
        if (distribuitor == null) { 
            System.out.println("EROARE: Incercare de adaugare distribuitor null.");
            auditService.logAction("Esec Adaugare Distribuitor: Input null");
            return;
        }
        if (distribuitorRepository.read(distribuitor.getIdDistribuitor()) != null) {
             System.out.println("AVERTISMENT: Un distribuitor cu ID-ul '" + distribuitor.getIdDistribuitor() + "' exista deja in BD. Distribuitorul '" + normalizeString(distribuitor.getNumeDistribuitor()) + "' nu a fost adaugat.");
            auditService.logAction("Esec Adaugare Distribuitor (ID Duplicat): " + distribuitor.getIdDistribuitor());
            return;
        }
        distribuitorRepository.create(distribuitor); 
        auditService.logAction("Finalizare Adaugare Distribuitor: " + distribuitor.getIdDistribuitor() + ", Nume=" + normalizeString(distribuitor.getNumeDistribuitor()));
    }
    
    public Distribuitor getDistribuitorById(String idDistribuitor) {
        auditService.logAction("Cautare Distribuitor dupa ID: " + idDistribuitor);
        return distribuitorRepository.read(idDistribuitor);
    }

    public void afiseazaTotiDistribuitorii() {
        auditService.logAction("Afisare Toti Distribuitorii");
        System.out.println("\n---------- LISTA DISTRIBUITORI (din Baza de Date) ----------");
        List<Distribuitor> distribuitori = distribuitorRepository.readAll();
        if (distribuitori.isEmpty()) {
            System.out.println("INFO: Nu exista distribuitori inregistrati in baza de date.");
        } else {
            Iterator<Distribuitor> iterator = distribuitori.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().toString()); 
                if (iterator.hasNext()) {
                     System.out.println("***************************************"); 
                }
            }
        }
        System.out.println("***************************************"); 
        System.out.println(); 
    }
}