package edu.ktu.ds.lab3.vaitkevicius;

import edu.ktu.ds.lab3.gui.ValidationException;
import edu.ktu.ds.lab3.utils.HashType;
import edu.ktu.ds.lab3.utils.Ks;
import edu.ktu.ds.lab3.utils.ParsableHashMap;
import edu.ktu.ds.lab3.utils.HashMapOa;
import edu.ktu.ds.lab3.utils.ParsableHashMapOa;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author eimutis
 */
public class SimpleBenchmark {

    public static final String FINISH_COMMAND = "                               ";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("edu.ktu.ds.lab3.gui.messages");

    private final Timekeeper timekeeper;

    private final String[] BENCHMARK_NAMES = {"Put", "BookRemKTU", "BookRemJava",
        "DictionaryRemKTU", "DictionaryRemJava"};
    private final int[] COUNTS = {10000, 20000, 40000, 80000};

    private final ParsableHashMap<String, Book> hashMapPut
            = new ParsableHashMap<>(String::new, Book::new, 10, 0.75f, HashType.DIVISION);

    private final edu.ktu.ds.lab3.utils.HashMap<String, Book> hashMapBookRemoveKtu
            = new edu.ktu.ds.lab3.utils.HashMap<>(10, 0.75f, HashType.DIVISION);

    private final HashMap<String, Book> hashMapBookRemoveJava = new HashMap<>();

    private final edu.ktu.ds.lab3.utils.HashMap<String, String> hashMapDictionaryRemoveKtu
            = new edu.ktu.ds.lab3.utils.HashMap<>(10, 0.75f, HashType.DIVISION);

    private final HashMap<String, String> hashMapDictionaryRemoveJava
            = new HashMap<String, String>();

    private final Queue<String> chainsSizes = new LinkedList<>();

    /**
     * For console benchmark
     */
    public SimpleBenchmark() {
        timekeeper = new Timekeeper(COUNTS);
    }

    /**
     * For Gui benchmark
     *
     * @param resultsLogger
     * @param semaphore
     */
    public SimpleBenchmark(BlockingQueue<String> resultsLogger, Semaphore semaphore) {
        semaphore.release();
        timekeeper = new Timekeeper(COUNTS, resultsLogger, semaphore);
    }

    public static void main(String[] args) {
        executeTest();
    }

    public static void executeTest() {
        // suvienodiname skaičių formatus pagal LT lokalę (10-ainis kablelis)
        Locale.setDefault(new Locale("LT"));
        Ks.out("Greitaveikos tyrimas:\n");
        new SimpleBenchmark().startBenchmark();
    }

    public void startBenchmark() {
        try {
            benchmark();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void benchmark() throws InterruptedException, FileNotFoundException {
        try {
            chainsSizes.add(MESSAGES.getString("maxChainLength"));
            chainsSizes.add("   kiekis      " + BENCHMARK_NAMES[0] + "   " + BENCHMARK_NAMES[1]);
            for (int k : COUNTS) {

                Book[] bookArray = BooksGenerator.generateShuffleBooks(k);
                String[] bookArray2 = BooksGenerator.generateShuffleIds(k);

                Book[] booksSearchArray = bookArray;
                String[] booksSearchIdsArray = bookArray2;

                shuffleProc(booksSearchArray);
                shuffleProc(booksSearchIdsArray);

                String[] wordStringArray = new String[k];
                RandomAccessFile dictionaryFile = new RandomAccessFile
                ("data\\zodynas.txt", "r");

                try {
                    for (int i = 0; i < k; i++) {
                        wordStringArray[i] = dictionaryFile.readLine();
                        hashMapDictionaryRemoveKtu.put(wordStringArray[i], wordStringArray[i]);
                        hashMapDictionaryRemoveJava.put(wordStringArray[i], wordStringArray[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                shuffleProc(wordStringArray);

                hashMapBookRemoveKtu.clear();
                hashMapBookRemoveJava.clear();
                timekeeper.startAfterPause();
                timekeeper.start();

                for (int i = 0; i < k; i++) {
                    hashMapBookRemoveKtu.put(bookArray2[i], bookArray[i]);
                    hashMapBookRemoveJava.put(bookArray2[i], bookArray[i]);
                }
                timekeeper.finish(BENCHMARK_NAMES[0]);

                for (int i = 0; i < k; i++) {
                    hashMapBookRemoveKtu.remove(booksSearchIdsArray[i]);
                }
                timekeeper.finish(BENCHMARK_NAMES[1]);

                for (int i = 0; i < k; i++) {
                    hashMapBookRemoveJava.remove(booksSearchIdsArray[i]);
                }
                timekeeper.finish(BENCHMARK_NAMES[2]);

                for (int i = 0; i < k; i++) {
                    hashMapDictionaryRemoveKtu.remove(wordStringArray[i]);
                }
                timekeeper.finish(BENCHMARK_NAMES[3]);

                for (int i = 0; i < k; i++) {
                    hashMapBookRemoveJava.remove(wordStringArray[i]);
                }
                timekeeper.finish(BENCHMARK_NAMES[4]);

                timekeeper.seriesFinish();
            }

            StringBuilder sb = new StringBuilder();
            chainsSizes.forEach(p -> sb.append(p).append(System.lineSeparator()));
            timekeeper.logResult(sb.toString());
            timekeeper.logResult(FINISH_COMMAND);
        } catch (ValidationException e) {
            timekeeper.logResult(e.getMessage());
        }
    }

    static <Type> void shuffleProc(Type[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Type a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
