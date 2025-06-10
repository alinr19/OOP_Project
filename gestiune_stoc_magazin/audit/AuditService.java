package gestiune_stoc_magazin.audit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
private static AuditService instance;
private static final String AUDIT\_FILE\_PATH = "audit\_log.csv";
private static final DateTimeFormatter TIMESTAMP\_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
private static final String CSV\_HEADER = "nume\_actiune,timestamp";
private static final String CSV\_SEPARATOR = ",";

private AuditService() {
Path path = Paths.get(AUDIT_FILE_PATH);
try {
if (!Files.exists(path)) {
Files.writeString(path, CSV_HEADER + System.lineSeparator(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
System.out.println("INFO AUDIT: Fisierul de audit '" + AUDIT_FILE_PATH + "' a fost creat cu header.");
} else if (Files.size(path) == 0) {
Files.writeString(path, CSV_HEADER + System.lineSeparator(), StandardOpenOption.APPEND);
System.out.println("INFO AUDIT: Header adaugat in fisierul de audit existent si gol '" + AUDIT_FILE_PATH + "'.");
} else {
System.out.println("INFO AUDIT: Fisierul de audit '" + AUDIT_FILE_PATH + "' exista si nu este gol.");
}
} catch (IOException e) {
System.err.println("EROARE AUDIT (CONSTRUCTOR): Nu s-a putut initializa/verifica fisierul de audit: " + e.getMessage());
e.printStackTrace(); // Afiseaza eroarea completa
}
}

public static synchronized AuditService getInstance() {
if (instance == null) {
System.out.println("DEBUG AUDIT: Se creeaza instanta AuditService...");
instance = new AuditService();
}
return instance;
}

public synchronized void logAction(String actionName) {
System.out.println("DEBUG AUDIT: Se incearca logarea actiunii: " + actionName); // Mesaj de debug
String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
String processedActionName = actionName.replace(""", """");
String logEntry = """ + processedActionName + """ + CSV_SEPARATOR + """ + timestamp + """;

try {
    Path path = Paths.get(AUDIT_FILE_PATH);
    // Asiguram ca fisierul are header daca a fost creat gol intre timp (desi constructorul ar trebui sa acopere)
    if (!Files.exists(path) || Files.size(path) == 0) {
        System.out.println("DEBUG AUDIT: Fisierul de audit nu exista sau e gol inainte de logAction. Se adauga header.");
        Files.writeString(path, CSV_HEADER + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.WRITE); // Scrie header-ul (va suprascrie daca e gol)
    }
    Files.writeString(path, logEntry + System.lineSeparator(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    System.out.println("INFO AUDIT: Actiune logata cu succes: " + actionName); // Mesaj de succes
} catch (IOException e) {
    System.err.println("EROARE AUDIT (LOGACTION): la scrierea actiunii '" + actionName + "': " + e.getMessage());
    e.printStackTrace(); // Afiseaza eroarea completa
}
}


}
