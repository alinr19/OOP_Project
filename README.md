# Proiect Gestiune Stocuri Magazin (OOP)

## Etapa 1: Sistem si Implementare

### Obiecte (Clase) Principale
* Produs
* Categorie
* Distribuitor
* Adresa
* ProdusAlimentar (mosteneste Produs)
* ProdusElectronic (mosteneste Produs)

### Actiuni Implementate
* Adaugare, Afisare, Actualizare, Stergere (CRUD) pentru Produse.
* CRUD pentru Categorii.
* CRUD pentru Distribuitori.
* Afisare produse dintr-o anumita categorie.
* Cautare produs dupa nume.

### Detalii de Implementare
Proiectul foloseste clase cu atribute private, colectii (`ArrayList`, `HashMap`) pentru managementul datelor in memorie, mostenire pentru specializarea claselor si servicii dedicate pentru logica de business. O clasa `Main` ofera un meniu interactiv pentru utilizator.

---

## Etapa 2: Persistenta si Audit

### Persistenta cu JDBC
Datele sunt salvate intr-o baza de date relationala folosind JDBC. Serviciile implementeaza operatii CRUD pentru entitatile principale si folosesc un design pattern Singleton pentru gestionarea conexiunii la baza de date.

### Serviciu de Audit
Un serviciu dedicat scrie fiecare actiune executata intr-un fisier `audit_log.csv`. Formatul fiecarei inregistrari este: `nume_actiune,timestamp`.

---
Link : https://github.com/Vladutzky/Laborator-Java/blob/main/Info/project.md

