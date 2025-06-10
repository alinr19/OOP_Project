package gestiune_stoc_magazin.model;

public class ProdusElectronic extends Produs {
    private int garantieLuni; 

    public ProdusElectronic(String idProdus, String numeProdus, double pret, Categorie categorie, Distribuitor distribuitor, int cantitateStoc, int garantieLuni) {
        super(idProdus, numeProdus, pret, categorie, distribuitor, cantitateStoc);
        this.garantieLuni = garantieLuni;
    }

    public int getGarantieLuni() {
        return garantieLuni;
    }

    public void setGarantieLuni(int garantieLuni) {
        this.garantieLuni = garantieLuni;
    }

    @Override
    public String getDetaliiSpecifice() {
        // Fara diacritice pentru consola Windows
        return "Garantie: " + garantieLuni + " luni";
    }
}