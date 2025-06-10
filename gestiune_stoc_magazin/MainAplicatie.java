package gestiune_stoc_magazin;
import gestiune_stoc_magazin.model.*;
import gestiune_stoc_magazin.service.ServiciuGestiuneStoc;
import gestiune_stoc_magazin.util.DatabaseConnectionManager; // Asigura-te ca acest import este prezent
import gestiune_stoc_magazin.audit.AuditService; // IMPORT PENTRU TESTUL DE AUDIT
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainAplicatie {

    private static ServiciuGestiuneStoc serviciu = new ServiciuGestiuneStoc();
    private static Scanner scanner = new Scanner(System.in);

    private static String noDiacritics(String text) {
        if (text == null) return "N/A";
        return text.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                   .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
    }

    private static void afiseazaMeniu() {
        System.out.println("\n***************************************************");
        System.out.println("*                     MENIU                        *");
        System.out.println("****************************************************");
        System.out.println("1.  Adauga Categorie");
        System.out.println("2.  Afiseaza Toate Categoriile");
        System.out.println("3.  Adauga Distribuitor");
        System.out.println("4.  Afiseaza Toti Distribuitorii");
        System.out.println("5.  Adauga Produs");
        System.out.println("6.  Afiseaza Toate Produsele");
        System.out.println("7.  Actualizeaza Stoc Produs");
        System.out.println("8.  Sterge Produs");
        System.out.println("9.  Cauta Produs dupa ID");
        System.out.println("10. Afiseaza Produse din Categorie");
        System.out.println("11. Afiseaza Produse cu Stoc Redus");
        System.out.println("0.  Iesire");
        System.out.println("---------------------------------------------------");
        System.out.print("Introduceti optiunea : ");
    }

    public static void main(String[] args) {
        System.out.println(noDiacritics("***************************************************"));
        System.out.println(noDiacritics("*            Bun venit la magazn                  *"));
        System.out.println(noDiacritics("***************************************************"));

        ServiciuGestiuneStoc serviciu = new ServiciuGestiuneStoc(); // Linia existenta

   
        System.out.println("DEBUG: Se incearca initializarea si logarea cu AuditService...");
        AuditService auditTest = AuditService.getInstance(); 
        auditTest.logAction("Test Logare Directa din Main - Pornire Aplicatie");
        auditTest.logAction("Inca un Test Logare din Main");
        System.out.println("DEBUG: Apelurile la AuditService din main au fost efectuate.");
  

        int optiune;
        do {
            afiseazaMeniu();
            try {
                optiune = scanner.nextInt();
                scanner.nextLine(); 

                switch (optiune) {
                    case 1:
                        gestioneazaAdaugaCategorie();
                        break;
                    case 2:
                        serviciu.afiseazaToateCategoriile();
                        break;
                    case 3:
                        gestioneazaAdaugaDistribuitor();
                        break;
                    case 4:
                        serviciu.afiseazaTotiDistribuitorii();
                        break;
                    case 5:
                        gestioneazaAdaugaProdus();
                        break;
                    case 6:
                        serviciu.afiseazaToateProdusele();
                        break;
                    case 7:
                        gestioneazaActualizeazaStoc();
                        break;
                    case 8:
                        gestioneazaStergeProdus();
                        break;
                    case 9:
                        gestioneazaCautaProdusDupaId();
                        break;
                    case 10:
                        gestioneazaAfiseazaProduseDinCategorie();
                        break;
                    case 11:
                        gestioneazaAfiseazaProduseCuStocRedus();
                        break;
                    case 0:
                        System.out.println(noDiacritics("La revedere! Aplicatia se inchide..."));
                        // Inchide conexiunea la BD la iesire
                        DatabaseConnectionManager.getInstance().closeConnection();
                        // Poti adauga un log de audit si pentru oprirea aplicatiei
                        AuditService.getInstance().logAction("Aplicatie Oprita");
                        break;
                    default:
                        System.out.println(noDiacritics("Optiune invalida. Va rugam reincercati."));
                }
            } catch (InputMismatchException e) {
                System.out.println(noDiacritics("Input invalid. Va rugam introduceti un numar."));
                scanner.nextLine(); 
                optiune = -1; 
            }
            if (optiune != 0) {
                System.out.println(noDiacritics("\nApasati Enter pentru a continua..."));
                scanner.nextLine(); 
            }
        } while (optiune != 0);

        scanner.close();
    }

    private static void gestioneazaAdaugaCategorie() {
        System.out.println(noDiacritics("\n--- Adaugare Categorie Noua ---"));
        System.out.print(noDiacritics("ID Categorie: "));
        String id = scanner.nextLine();
        System.out.print(noDiacritics("Nume Categorie (ex: Fructe): "));
        String nume = scanner.nextLine();
        System.out.print(noDiacritics("Descriere Categorie: "));
        String descriere = scanner.nextLine();
        serviciu.adaugaCategorie(new Categorie(id, nume, descriere));
    }

    private static void gestioneazaAdaugaDistribuitor() {
        System.out.println(noDiacritics("\n--- Adaugare Distribuitor Nou ---"));
        System.out.print(noDiacritics("ID Distribuitor: "));
        String id = scanner.nextLine();
        System.out.print(noDiacritics("Nume Distribuitor: "));
        String nume = scanner.nextLine();
        System.out.print(noDiacritics("Persoana Contact: "));
        String contact = scanner.nextLine();
        System.out.print(noDiacritics("Telefon: "));
        String telefon = scanner.nextLine();

        System.out.println(noDiacritics("-- Detalii Adresa Distribuitor --"));
        System.out.print(noDiacritics("Strada: "));
        String strada = scanner.nextLine();
        System.out.print(noDiacritics("Numar: "));
        String nr = scanner.nextLine();
        System.out.print(noDiacritics("Oras: "));
        String oras = scanner.nextLine();
        System.out.print(noDiacritics("Judet: "));
        String judet = scanner.nextLine();
        System.out.print(noDiacritics("Cod Postal: "));
        String codPostal = scanner.nextLine();
        Adresa adresa = new Adresa(strada, nr, oras, judet, codPostal);
        
        serviciu.adaugaDistribuitor(new Distribuitor(id, nume, contact, telefon, adresa));
    }

    private static void gestioneazaAdaugaProdus() {
        System.out.println(noDiacritics("\n--- Adaugare Produs Nou ---"));
        System.out.print(noDiacritics("ID Produs: "));
        String idProdus = scanner.nextLine();
        System.out.print(noDiacritics("Nume Produs: "));
        String numeProdus = scanner.nextLine();

        double pret = 0;
        boolean pretValid = false;
        while (!pretValid) { 
            System.out.print(noDiacritics("Pret Produs: "));
            try {
                pret = scanner.nextDouble();
                scanner.nextLine(); 
                if (pret < 0) {
                    System.out.println(noDiacritics("Pretul nu poate fi negativ."));
                } else {
                    pretValid = true;
                }
            } catch (InputMismatchException e) {
                System.out.println(noDiacritics("Format pret invalid. Introduceti un numar."));
                scanner.nextLine(); 
            }
        }
        
        System.out.print(noDiacritics("ID Categorie existenta (ex: CAT001): "));
        String idCategorie = scanner.nextLine();
        Categorie categorie = serviciu.getCategorieById(idCategorie); 
        if (categorie == null) {
            System.out.println(noDiacritics("Categorie inexistenta in BD. Produsul nu poate fi adaugat."));
            AuditService.getInstance().logAction("Esec Adaugare Produs (User Input): Categorie inexistenta ID=" + idCategorie);
            return;
        }

        System.out.print(noDiacritics("ID Distribuitor existent (ex: DIST01): "));
        String idDistribuitor = scanner.nextLine();
        Distribuitor distribuitor = serviciu.getDistribuitorById(idDistribuitor); 
        if (distribuitor == null) {
            System.out.println(noDiacritics("Distribuitor inexistent in BD. Produsul va fi adaugat fara distribuitor sau adaugati distribuitorul mai intai."));
            AuditService.getInstance().logAction("Info Adaugare Produs (User Input): Distribuitor inexistent ID=" + idDistribuitor + ". Se adauga fara.");
        }

        int cantitateStoc = 0;
        boolean stocValid = false;
        while(!stocValid) { 
            System.out.print(noDiacritics("Cantitate Stoc Initial: "));
            try {
                cantitateStoc = scanner.nextInt();
                scanner.nextLine(); 
                 if (cantitateStoc < 0) {
                    System.out.println(noDiacritics("Stocul nu poate fi negativ."));
                } else {
                    stocValid = true;
                }
            } catch (InputMismatchException e) {
                System.out.println(noDiacritics("Format cantitate invalid. Introduceti un numar intreg."));
                scanner.nextLine(); 
            }
        }

        System.out.print(noDiacritics("Tip Produs (1 = Alimentar, 2 = Electronic, altceva = General/AlimentarPlaceholder): "));
        int tipOptiune = -1;
        String tipOptiuneStr = scanner.nextLine();
        try {
             tipOptiune = Integer.parseInt(tipOptiuneStr);
        } catch (NumberFormatException e) {
            System.out.println(noDiacritics("Optiune tip invalida, se va crea un produs placeholder."));
        }

        if (tipOptiune == 1) { 
            LocalDate dataExpirare = null;
            boolean dataValida = false;
            while(!dataValida) { 
                System.out.print(noDiacritics("Data Expirare (format dd/MM/yyyy): "));
                String dataStr = scanner.nextLine();
                try {
                    dataExpirare = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    dataValida = true;
                } catch (DateTimeParseException e) {
                    System.out.println(noDiacritics("Format data invalid. Reincercati."));
                }
            }
            serviciu.adaugaProdus(new ProdusAlimentar(idProdus, numeProdus, pret, categorie, distribuitor, cantitateStoc, dataExpirare));
        } else if (tipOptiune == 2) { 
            int garantieLuni = 0;
            boolean garantieValida = false;
            while(!garantieValida) { 
                System.out.print(noDiacritics("Garantie (luni): "));
                 try {
                    garantieLuni = scanner.nextInt();
                    scanner.nextLine(); 
                    if (garantieLuni < 0) {
                         System.out.println(noDiacritics("Garantia nu poate fi negativa."));
                    } else {
                        garantieValida = true;
                    }
                } catch (InputMismatchException e) {
                    System.out.println(noDiacritics("Format garantie invalid. Introduceti un numar intreg."));
                    scanner.nextLine(); 
                }
            }
            serviciu.adaugaProdus(new ProdusElectronic(idProdus, numeProdus, pret, categorie, distribuitor, cantitateStoc, garantieLuni));
        } else { 
             System.out.println(noDiacritics("Tip produs neimplementat in meniu sau invalid, se adauga ca ProdusAlimentar placeholder (fara data expirare)."));
             serviciu.adaugaProdus(new ProdusAlimentar(idProdus, numeProdus, pret, categorie, distribuitor, cantitateStoc, null));
        }
    }

    private static void gestioneazaActualizeazaStoc() {
        System.out.println(noDiacritics("\n--- Actualizare Stoc Produs ---"));
        System.out.print(noDiacritics("ID Produs pentru actualizare stoc: "));
        String idProdus = scanner.nextLine();
        
        int cantitateNoua = 0;
        boolean cantitateValida = false;
        while(!cantitateValida) { 
             System.out.print(noDiacritics("Cantitate Noua in Stoc: "));
            try {
                cantitateNoua = scanner.nextInt();
                scanner.nextLine(); 
                if (cantitateNoua < 0) {
                    System.out.println(noDiacritics("Cantitatea nu poate fi negativa."));
                } else {
                    cantitateValida = true;
                }
            } catch (InputMismatchException e) {
                System.out.println(noDiacritics("Format cantitate invalid. Introduceti un numar intreg."));
                scanner.nextLine(); 
            }
        }
        serviciu.actualizeazaStocProdus(idProdus, cantitateNoua);
    }

    private static void gestioneazaStergeProdus() {
        System.out.println(noDiacritics("\n--- Stergere Produs ---"));
        System.out.print(noDiacritics("ID Produs de sters: "));
        String idProdus = scanner.nextLine();
        serviciu.stergeProdus(idProdus);
    }
    
    private static void gestioneazaCautaProdusDupaId() {
        System.out.println(noDiacritics("\n--- Cautare Produs dupa ID ---"));
        System.out.print(noDiacritics("ID Produs cautat: "));
        String idProdus = scanner.nextLine();
        Produs produsGasit = serviciu.getProdusById(idProdus); 
        if (produsGasit != null) {
            System.out.println(noDiacritics("Produs gasit:"));
            System.out.println(produsGasit.toString()); 
            AuditService.getInstance().logAction("Succes Cautare Produs: ID=" + idProdus);
        } else {
            System.out.println(noDiacritics("Produsul cu ID " + idProdus + " nu a fost gasit."));
            AuditService.getInstance().logAction("Esec Cautare Produs (ID Inexistent): ID=" + idProdus);
        }
    }

    private static void gestioneazaAfiseazaProduseDinCategorie() {
        System.out.println(noDiacritics("\n--- Afisare Produse dintr-o Categorie ---"));
        System.out.print(noDiacritics("ID Categorie: "));
        String idCategorie = scanner.nextLine();
        
        Categorie c = serviciu.getCategorieById(idCategorie); 
        if (c == null) {
            System.out.println(noDiacritics("Categoria cu ID " + idCategorie + " nu exista."));
            AuditService.getInstance().logAction("Esec Afisare Produse din Categorie (Categorie Inexistenta): ID=" + idCategorie);
            return;
        }

        AuditService.getInstance().logAction("Initiere Afisare Produse din Categorie: ID=" + idCategorie + ", Nume=" + noDiacritics(c.getNumeCategorie()));
        System.out.println(noDiacritics("Produse din categoria '" + noDiacritics(c.getNumeCategorie()) + "':"));
        List<Produs> produseFiltrate = serviciu.getProduseDinCategorie(idCategorie); 
        if (produseFiltrate.isEmpty()) {
            System.out.println(noDiacritics("Nu s-au gasit produse in aceasta categorie."));
        } else {
            for (int i = 0; i < produseFiltrate.size(); i++) {
                System.out.println(produseFiltrate.get(i).toString());
                if (i < produseFiltrate.size() - 1) {
                    System.out.println("---------------------------------------"); 
                }
            }
        }
        System.out.println("---------------------------------------");
    }

    private static void gestioneazaAfiseazaProduseCuStocRedus() {
        System.out.println(noDiacritics("\n--- Afisare Produse cu Stoc Redus ---"));
        int limitaStoc = 0;
        boolean limitaValida = false;
        while(!limitaValida) { 
            System.out.print(noDiacritics("Introduceti limita inferioara de stoc: "));
            try {
                limitaStoc = scanner.nextInt();
                scanner.nextLine(); 
                if (limitaStoc < 0) {
                    System.out.println(noDiacritics("Limita nu poate fi negativa."));
                } else {
                    limitaValida = true;
                }
            } catch (InputMismatchException e) {
                System.out.println(noDiacritics("Format limita invalid. Introduceti un numar intreg."));
                scanner.nextLine(); 
            }
        }
        AuditService.getInstance().logAction("Initiere Afisare Produse cu Stoc Redus: Limita=" + limitaStoc);
        List<Produs> produseFiltrate = serviciu.getProduseCuStocRedus(limitaStoc); 
        if (produseFiltrate.isEmpty()) {
            System.out.println(noDiacritics("Nu exista produse cu stoc sub " + limitaStoc + " unitati."));
        } else {
            System.out.println(noDiacritics("Urmatoarele produse au stoc sub " + limitaStoc + " unitati:"));
            produseFiltrate.forEach(p -> 
                System.out.printf("  - %s (ID: %s) - Cantitate: %d unitati%n", 
                                  noDiacritics(p.getNumeProdus()), p.getIdProdus(), p.getCantitateStoc()));
        }
    }
}