/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ktu.ds.lab3.utils;

import java.util.Arrays;

/**
 *
 * @author NZXT-PC
 */
public class HashMapOa<K, V> implements Map<K, V> {

    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public static final HashType DEFAULT_HASH_TYPE = HashType.DIVISION;

    // Maišos lentelė
    protected Node<K, V>[] table;
    // Lentelėje esančių raktas-reikšmė porų kiekis
    protected int size = 0;
    // Apkrovimo faktorius
    protected float loadFactor;
    // Maišos metodas
    protected HashType ht;
    //--------------------------------------------------------------------------
    //  Maišos lentelės įvertinimo parametrai
    //--------------------------------------------------------------------------
    // Permaišymų kiekis
    protected int rehashesCounter = 0;
    // Einamas poros indeksas maišos lentelėje
    protected int index = 0;

    /* Klasėje sukurti 4 perkloti konstruktoriai, nustatantys atskirus maišos 
     * lentelės parametrus. Jei kuris nors parametras nėra nustatomas - 
     * priskiriama standartinė reikšmė.
     */
    public HashMapOa() {
        this(DEFAULT_HASH_TYPE);
    }

    public HashMapOa(HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, ht);
    }

    public HashMapOa(int initialCapacity, HashType ht) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, ht);
    }

    public HashMapOa(float loadFactor, HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, loadFactor, ht);
    }

    public HashMapOa(int initialCapacity, float loadFactor, HashType ht) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if ((loadFactor <= 0.0) || (loadFactor > 1.0)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.table = new Node[initialCapacity];
        this.loadFactor = loadFactor;
        this.ht = ht;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        index = 0;
        rehashesCounter = 0;
    }

    @Override
    public String[][] toArray() {
        String[][] result = new String[table.length][];
        int count = 0;
        for (Node<K, V> n : table) {
            String[] list = new String[1];
            list[0] = n.toString();
            result[count] = list;
            count++;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Node<K, V> node : table) {
            if (node != null) {
                result.append(node.toString()).append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private int findPosition(K key) {
        int index = hash(key, ht);
        int index0 = index;
        for (int i = 0; i < table.length; i++) {

            if (table[index] == null || table[index].key.equals(key)) {
//            if (table[index] == null || table[index].key == key) {
                return index;
            }
//            index = (index0 + i + 1) % table.length;
            index = (index0 + i) % table.length;
        }

        return -1;
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null in put(Key key, Value value)");
        }
        int index = findPosition(key);
        if (index == -1) {
            rehash();
            put(key, value);
            return value;
        }
        if (table[index] == null) {
            table[index] = new Node<K, V>(key, value);
            size++;
            if (size > table.length * loadFactor) {
                rehash();
            }
        } else {
            table[index].value = value;
        }
        return value;
    }

    /*  -----------------------------------------------------------------------------------------
    tai yra metodas nr 4.
    Jei argumente nurodytas raktas neturi reikšmės šiame atvaizdyje, tada
    argumente nurodyta raktas - reikšmė pora įrašoma ir grąžinama null. Kitu 
    atveju grąžinama atvaizdyje jau egzistuojanti raktą atitinkanti reikšmė.
     */
    public V putIfAbsent(K key, V value) {
        if (contains(key)) {
            return get(key);
        } else {
            put(key, value);
            return null;
        }
    }

    public double averageChainSize() {
        if (table == null) {
            throw new NullPointerException("Null pointer");
        }
        return size * 1.0 / (table.length - numberOfEmpties());
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in get(Key key)");
        }

        index = findPosition(key);
        return (table[index] != null) ? table[index].value : null;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException("Null pointer in remove");
        }
        int index = findPosition(key);
        if (table[index] != null) {
            V value = table[index].value;
            table[index] = null;
            size--;
            return value;
        } else {
            return null;
        }
    }

    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }

    /*  -----------------------------------------------------------------------------------------
    tai yra metodas nr 1.
    grąžinantį true, jei atvaizdyje egzistuoja vienas 
    ar daugiau raktų metodo argumente nurodytai reikšmei.
     */
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Null pointer in put");
        }

        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                if (table[i].value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Permaišymas
     *
     * @param node
     */
    private void rehash() {
        HashMapOa<K, V> newMap = new HashMapOa<>(table.length * 2, loadFactor, ht);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                newMap.put(table[i].key, table[i].value);
            }
        }
        table = newMap.table;
        rehashesCounter++;
    }

    private int hash(K key, HashType hashType) {
        int h = key.hashCode();
        switch (hashType) {
            case DIVISION:
                return Math.abs(h) % table.length;
            case MULTIPLICATION:
                double k = (Math.sqrt(5) - 1) / 2;
                return (int) (((k * Math.abs(h)) % 1) * table.length);
            case JCF7:
                h ^= (h >>> 20) ^ (h >>> 12);
                h = h ^ (h >>> 7) ^ (h >>> 4);
                return h & (table.length - 1);
            case JCF8:
                h = h ^ (h >>> 16);
                return h & (table.length - 1);
            default:
                return Math.abs(h) % table.length;
        }
    }

    /**
     * Grąžina formuojant maišos lentelę įvykusių permaišymų kiekį.
     *
     * @return Permaišymų kiekis.
     */
    public int getRehashesCounter() {
        return rehashesCounter;
    }

    /**
     * Grąžina maišos lentelės talpą.
     *
     * @return Maišos lentelės talpa.
     */
    public int getTableCapacity() {
        return table.length;
    }

    public int numberOfEmpties() {
        int numberOfEmpties = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                numberOfEmpties++;
            }
        }
        return numberOfEmpties;
    }

    public int getNumberOfCollisions() {
        if (table == null) {
            throw new NullPointerException("Null pointer");
        }
        int numberOfCollisions = size + numberOfEmpties() - table.length;
        return numberOfCollisions;
    }

    public java.util.Set<K> keySet() {
        java.util.Set<K> test = new java.util.HashSet<>();
        for (Node<K, V> element : table) {
            if (element != null) {
                Node<K, V> value = element;
                test.add(value.key);
            }
        }
        return test;
    }

    protected static class Node<K, V> {

        // Raktas        
        protected K key;
        // Reikšmė
        protected V value;
        // Rodyklė į sekantį grandinėlės mazgą

        protected Node() {
        }

        protected Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
