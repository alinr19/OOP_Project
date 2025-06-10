package gestiune_stoc_magazin.model;

import java.util.Objects;

public abstract class Produs {
    protected String idProdus;
    protected String numeProdus;
    protected double pret;
    protected Categorie categorie;
    protected Distribuitor distribuitor;
    protected int cantitateStoc;

    public Produs(String idProdus, String numeProdus, double pret, Categorie categorie, Distribuitor distribuitor, int cantitateStoc) {
        this.idProdus = idProdus;
        this.numeProdus = numeProdus;
        this.pret = pret;
        this.categorie = categorie;
        this.distribuitor = distribuitor;
        this.cantitateStoc = cantitateStoc;
    }

    // Getters
    public String getIdProdus() { return idProdus; }
    public String getNumeProdus() { return numeProdus; }
    public double getPret() { return pret; }
    public Categorie getCategorie() { return categorie; }
    public Distribuitor getDistribuitor() { return distribuitor; }
    public int getCantitateStoc() { return cantitateStoc; }

    // Setters
    public void setNumeProdus(String numeProdus) { this.numeProdus = numeProdus; }
    public void setPret(double pret) { this.pret = pret; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public void setDistribuitor(Distribuitor distribuitor) { this.distribuitor = distribuitor; }
    public void setCantitateStoc(int cantitateStoc) {
        if (cantitateStoc >= 0) {
            this.cantitateStoc = cantitateStoc;
        } else {
            String numeProdusAfisare = this.numeProdus.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                                     .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
            System.err.println("EROARE: Cantitatea in stoc nu poate fi negativa pentru produsul " + numeProdusAfisare);
        }
    }

    public abstract String getDetaliiSpecifice(); // Subclasele vor implementa asta (fara diacritice daca e nevoie)

    @Override
    public String toString() {
        String numeProdusAfisare = numeProdus.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                             .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
        String numeCategorieAfisare = (categorie != null ? categorie.getNumeCategorie() : "N/A")
                                             .replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                             .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
        String numeDistribuitorAfisare = (distribuitor != null ? distribuitor.getNumeDistribuitor() : "N/A")
                                             .replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                             .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");

        return String.format(
            "Produs ID: %s\n" +
            "  Nume: %s\n" +
            "  Pret: %.2f RON\n" +
            "  Categorie: %s\n" +
            "  Distribuitor: %s\n" +
            "  Stoc: %d unitati\n" + // Am scos diacritic din "unități"
            "  Detalii Specifice: %s",
            idProdus,
            numeProdusAfisare,
            pret,
            numeCategorieAfisare,
            numeDistribuitorAfisare,
            cantitateStoc,
            getDetaliiSpecifice()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Produs)) return false; 
        Produs produs = (Produs) o;
        return Objects.equals(idProdus, produs.idProdus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProdus);
    }
}