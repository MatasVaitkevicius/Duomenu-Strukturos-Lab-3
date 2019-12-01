package edu.ktu.ds.lab3.vaitkevicius;

import edu.ktu.ds.lab3.utils.Ks;
import edu.ktu.ds.lab3.utils.Parsable;

import java.time.LocalDate;
import java.util.*;

/**
 * @author EK
 */
public final class Book implements Parsable<Book> {

    // Bendri duomenys visiems automobiliams (visai klasei)
    private static final int minYear = 1990;
    private static final int currentYear = LocalDate.now().getYear();
    private static final double minPrice = 100.0;
    private static final double maxPrice = 333000.0;

    private String author = "";
    private String title = "";
    private int year = -1;
    private double price = -1.0;

    public Book() {
    }

    public Book(String author, String title, int year, double price) {
        this.author = author;
        this.title = title;
        this.year = year;
        this.price = price;
        validate();
    }

    public Book(String dataString) {
        this.parse(dataString);
        validate();
    }

    public Book(Builder builder) {
        this.author = builder.author;
        this.title = builder.title;
        this.year = builder.year;
        this.price = builder.price;
        validate();
    }

    private void validate() {
        String errorType = "";
        if (year < minYear || year > currentYear) {
            errorType = "Netinkami gamybos metai, turi būti ["
                    + minYear + ":" + currentYear + "]";
        }
        if (price < minPrice || price > maxPrice) {
            errorType += " Kaina už leistinų ribų [" + minPrice
                    + ":" + maxPrice + "]";
        }

        if (!errorType.isEmpty()) {
            Ks.ern("Automobilis yra blogai sugeneruotas: " + errorType);
        }
    }

    @Override
    public void parse(String dataString) {
        try {   // duomenys, atskirti tarpais
            Scanner scanner = new Scanner(dataString);
            author = scanner.next();
            title = scanner.next();
            year = scanner.nextInt();
            price = scanner.nextDouble();
        } catch (InputMismatchException e) {
            Ks.ern("Blogas duomenų formatas -> " + dataString);
        } catch (NoSuchElementException e) {
            Ks.ern("Trūksta duomenų -> " + dataString);
        }
    }

    @Override
    public String toString() {
        return author + "_" + title + ":" + year + " "
                + String.format("%4.1f", price);
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.author);
        hash = 29 * hash + Objects.hashCode(this.title);
        hash = 29 * hash + this.year;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.price) ^ (Double.doubleToLongBits(this.price) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if (this.year != other.year) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    // Car klases objektų gamintojas
    public static class Builder {

        private final static Random RANDOM = new Random(1949);  // Atsitiktinių generatorius
        private final static String[][] TITLES = { // galimų autorių ir jo knygu pavadinimų masyvas
            {"Jonas Biliūnas", "Kliudžiau", "Liūdna Pasaka", "Ir Rados stebuklas",
                "Laimės žiburys"},
            {"Maironis", "Kur bėga Šešupė",
                "Lietuva", "Mūsu vargai", "Pavasario Balsai", "Nuo Birutės kalno"},
            {"Balys Sruoga", "Dievų miškas", "Dievų takais"},
            {"Salomėja Nėris", "Anksti rytą", "Pėdos smėly", "Laumės dovanos"},
            {"Kazys Binkis", "Eilėraščiai", "Atžalynas", "Meškeriotojas",
                "100 pavasarių"},
            {"Vincas Krėvė", "Skirgaila", "Skerdžius", "Miglose"}
        };

        private String author = "";
        private String title = "";
        private int year = -1;
        private double price = -1.0;

        public Book build() {
            return new Book(this);
        }

        public Book buildRandom() {
            int ma = RANDOM.nextInt(TITLES.length);        // pavadinimo indeksas  0..
            int mo = RANDOM.nextInt(TITLES[ma].length - 1) + 1;// autoriaus indeksas 1..
            return new Book(TITLES[ma][0],
                    TITLES[ma][mo],
                    1990 + RANDOM.nextInt(20),// metai tarp 1990 ir 2009
                    800 + RANDOM.nextDouble() * 88000);// kaina tarp 800 ir 88800
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }
    }
}
