package gestiune_stoc_magazin.model;

public class Adresa {
    private int idAdresa; // ADAUGAT: ID-ul din baza de date
    private String strada;
    private String numar;
    private String oras;
    private String judet;
    private String codPostal;

    // Constructor folosit la crearea unei noi adrese (ID-ul va fi generat de BD)
    public Adresa(String strada, String numar, String oras, String judet, String codPostal) {
        this.strada = strada;
        this.numar = numar;
        this.oras = oras;
        this.judet = judet;
        this.codPostal = codPostal;
    }

    // Constructor folosit la citirea din BD (include ID-ul)
    public Adresa(int idAdresa, String strada, String numar, String oras, String judet, String codPostal) {
        this.idAdresa = idAdresa;
        this.strada = strada;
        this.numar = numar;
        this.oras = oras;
        this.judet = judet;
        this.codPostal = codPostal;
    }

    // Getters
    public int getIdAdresa() { return idAdresa; } // ADAUGAT
    public String getStrada() { return strada; }
    public String getNumar() { return numar; }
    public String getOras() { return oras; }
    public String getJudet() { return judet; }
    public String getCodPostal() { return codPostal; }

    // Setters
    public void setIdAdresa(int idAdresa) { this.idAdresa = idAdresa; } // ADAUGAT
    public void setStrada(String strada) { this.strada = strada; }
    public void setNumar(String numar) { this.numar = numar; }
    public void setOras(String oras) { this.oras = oras; }
    public void setJudet(String judet) { this.judet = judet; }
    public void setCodPostal(String codPostal) { this.codPostal = codPostal; }

    @Override
    public String toString() {
        String orasAfisare = (oras != null ? oras.replace("ș", "s").replace("ț", "t").replace("ă", "a").replace("î", "i").replace("â", "a") : "N/A");
        String judetAfisare = (judet != null ? judet.replace("ș", "s").replace("ț", "t").replace("ă", "a").replace("î", "i").replace("â", "a") : "N/A");
        
        return String.format("Str. %s, Nr. %s, %s, Jud. %s, CP %s (ID Adresa: %d)",
               strada, numar, orasAfisare, judetAfisare, codPostal, idAdresa);
    }
}