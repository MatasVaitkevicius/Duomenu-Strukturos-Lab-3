package edu.ktu.ds.lab3.vaitkevicius;

import edu.ktu.ds.lab3.utils.HashType;
import edu.ktu.ds.lab3.utils.Ks;
import edu.ktu.ds.lab3.utils.ParsableHashMap;
import edu.ktu.ds.lab3.utils.ParsableMap;

import java.util.Locale;

public class ManualTest {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // suvienodiname skaičių formatus
        executeTest();
    }

    public static void executeTest() {
        Book c1 = new Book("AAAuotirus", "PAVAADINIMAS", 1997, 1700);
        Book c2 = new Book.Builder().buildRandom();
        Book c3 = new Book("Gendaris Lunara 2001 700");
        Book c4 = new Book("Ziusepe France 1946 9500");
        Book c5 = new Book("Razda Grande 2001 80.3");
        Book c6 = new Book.Builder().buildRandom();
        Book c7 = new Book.Builder().buildRandom();
        
        // Raktų masyvas
        String[] booksIds = {"TA156", "TA102", "TA178", "TA171", "TA105", "TA106", "TA107", "TA108"};
        int id = 0;
        ParsableMap<String, Book> booksMap
                = new ParsableHashMap<>(String::new, Book::new, HashType.DIVISION);

        // Reikšmių masyvas
        Book[] books = {c1, c2, c3, c4, c5, c6, c7};
        for (Book c : books) {
            booksMap.put(booksIds[id++], c);
        }
        booksMap.println("Porų išsidėstymas atvaizdyje pagal raktus");
        Ks.oun("Ar egzistuoja pora atvaizdyje?");
        Ks.oun(booksMap.contains(booksIds[6]));
        Ks.oun(booksMap.contains(booksIds[7]));
        Ks.oun("Pašalinamos poros iš atvaizdžio:");
        Ks.oun(booksMap.remove(booksIds[1]));
        Ks.oun(booksMap.remove(booksIds[7]));
        booksMap.println("Porų išsidėstymas atvaizdyje pagal raktus");
        Ks.oun("Atliekame porų paiešką atvaizdyje:");
        Ks.oun(booksMap.get(booksIds[2]));
        Ks.oun(booksMap.get(booksIds[7]));
        Ks.oun("Išspausdiname atvaizdžio poras String eilute:");
        Ks.ounn(booksMap);
    }
}
