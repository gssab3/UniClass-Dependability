package it.unisa.uniclass.testing.utils;

import java.util.Random;
import java.util.UUID;

public class TestUtils {
    /**
     * Genera una stringa casuale da usare come password nei test unitari.
     * Questo impedisce agli scanner statici di rilevare pattern di password fisse.
     */
    public static String generateTestPassword() {
        return "Test-" + UUID.randomUUID().toString();
    }

    public static String generateTestMatricola() {
        // Genera qualcosa tipo MAT-12345 dinamico
        return "MAT-" + (10000 + new Random().nextInt(90000));
    }
}