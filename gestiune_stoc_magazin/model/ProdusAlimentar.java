package gestiune_stoc_magazin.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProdusAlimentar extends Produs {
    private LocalDate dataExpirare;

    public ProdusAlimentar(String idProdus, String numeProdus, double pret, Categorie categorie, Distribuitor distribuitor, int cantitateStoc, LocalDate dataExpirare) {
        super(idProdus, numeProdus, pret, categorie, distribuitor, cantitateStoc);
        this.dataExpirare = dataExpirare;
    }

    public LocalDate getDataExpirare() {
        return dataExpirare;
    }

    public void setDataExpirare(LocalDate dataExpirare) {
        this.dataExpirare = dataExpirare;
    }

    @Override
    public String getDetaliiSpecifice() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Fara diacritice pentru consola Windows
        return "Data expirare: " + (dataExpirare != null ? dataExpirare.format(formatter) : "N/A");
    }
}