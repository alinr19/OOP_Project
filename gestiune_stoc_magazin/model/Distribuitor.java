package gestiune_stoc_magazin.model;

import java.util.Objects;

public class Distribuitor {
    private String idDistribuitor;
    private String numeDistribuitor;
    private String persoanaContact;
    private String telefon;
    private Adresa adresa;

    public Distribuitor(String idDistribuitor, String numeDistribuitor, String persoanaContact, String telefon, Adresa adresa) {
        this.idDistribuitor = idDistribuitor;
        this.numeDistribuitor = numeDistribuitor;
        this.persoanaContact = persoanaContact;
        this.telefon = telefon;
        this.adresa = adresa;
    }

    // Getters
    public String getIdDistribuitor() { return idDistribuitor; }
    public String getNumeDistribuitor() { return numeDistribuitor; }
    public String getPersoanaContact() { return persoanaContact; }
    public String getTelefon() { return telefon; }
    public Adresa getAdresa() { return adresa; }

    // Setters
    public void setNumeDistribuitor(String numeDistribuitor) { this.numeDistribuitor = numeDistribuitor; }
    public void setPersoanaContact(String persoanaContact) { this.persoanaContact = persoanaContact; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setAdresa(Adresa adresa) { this.adresa = adresa; }

    @Override
    public String toString() {
        String numeDistribuitorAfisare = numeDistribuitor.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                                         .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
        String persoanaContactAfisare = persoanaContact.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                                       .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");

        return String.format("Distribuitor ID: %s\n" +
               "  Nume: %s\n" +
               "  Contact: %s, Tel: %s\n" +
               "  Adresa: %s",
               idDistribuitor, numeDistribuitorAfisare, persoanaContactAfisare, telefon,
               (adresa != null ? adresa.toString() : "N/A")); // Adresa.toString() este deja fara diacritice
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distribuitor that = (Distribuitor) o;
        return Objects.equals(idDistribuitor, that.idDistribuitor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDistribuitor);
    }
}