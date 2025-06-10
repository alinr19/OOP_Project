package gestiune_stoc_magazin.model;

import java.util.Objects;

public class Categorie {
    private String idCategorie;
    private String numeCategorie;
    private String descriere;

    public Categorie(String idCategorie, String numeCategorie, String descriere) {
        this.idCategorie = idCategorie;
        this.numeCategorie = numeCategorie; // Stocam cu diacritice, afisam fara daca e nevoie
        this.descriere = descriere;
    }

    // Getters
    public String getIdCategorie() { return idCategorie; }
    public String getNumeCategorie() { return numeCategorie; }
    public String getDescriere() { return descriere; }

    // Setters
    public void setNumeCategorie(String numeCategorie) { this.numeCategorie = numeCategorie; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    @Override
    public String toString() {
        // Fara diacritice pentru consola Windows in afisare
        String numeCategorieAfisare = numeCategorie.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                                 .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");
        String descriereAfisare = descriere.replace("ă", "a").replace("â", "a").replace("î", "i").replace("ș", "s").replace("ț", "t")
                                           .replace("Ă", "A").replace("Â", "A").replace("Î", "I").replace("Ș", "S").replace("Ț", "T");

        return String.format("Categorie ID: %s\n  Nume: %s\n  Descriere: %s",
               idCategorie, numeCategorieAfisare, descriereAfisare);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return Objects.equals(idCategorie, categorie.idCategorie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategorie);
    }
}