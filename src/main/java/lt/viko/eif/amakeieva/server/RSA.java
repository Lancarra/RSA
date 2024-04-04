package lt.viko.eif.amakeieva.server;

import java.io.*;
import java.math.BigInteger;
import java.util.Objects;

public class RSA {
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 ";


    public static void main(String[] args) {
        String choice = readStringFromConsole("Select 1 for encryption or 2 for decryption.");
        if (Objects.equals(choice, "1")) {
            BigInteger p = readBigIntegerFromConsole("Enter a prime number p:");
            BigInteger q = readBigIntegerFromConsole("Enter a prime number q:");
            String x = readStringFromConsole("Enter open text x:").toLowerCase();

            BigInteger n = p.multiply(q);
            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            BigInteger e = BigInteger.valueOf(3);
            while (!phi.gcd(e).equals(BigInteger.ONE)) {
                e = e.add(BigInteger.TWO);
            }
            BigInteger d = e.modInverse(phi);

            System.out.println("Calculated value d: " + d);

            StringBuilder encryptText = new StringBuilder();
            for (int i = 0; i < x.length(); i++) {
                BigInteger result = BigInteger.valueOf(alphabet.indexOf(x.charAt(i)) + 1).modPow(e, n);
                encryptText.append(result).append(" ");
            }

            saveToFile("encrypted_text.txt", encryptText.toString());
            saveToFile("public_key.txt", e + "," + n);
            saveToFile("private_key.txt", d + "," + n);
            System.out.println("Ciphertext: " + encryptText.toString().replace(",", ""));
        } else if (Objects.equals(choice, "2")) {
            StringBuilder decryptText = new StringBuilder();

            String encryptedText = readFromFile("encrypted_text.txt");
            String[] public_key_list = readFromFile("public_key.txt").split(",");
            String[] private_key_list = readFromFile("private_key.txt").split(",");


            for (String i : encryptedText.split(" ")) {
                if (!i.isEmpty()) {
                    BigInteger result = new BigInteger(i).modPow(new BigInteger(private_key_list[0]), new BigInteger(private_key_list[1]));
                    int decryptedIndex = result.subtract(BigInteger.ONE).intValue() % alphabet.length(); // Правильный индекс в алфавите
                    if (decryptedIndex < 0) {
                        decryptedIndex += alphabet.length();
                    }
                    if (decryptedIndex == (int) 'z') {
                        decryptText.append(' ');
                    } else {
                        decryptText.append(alphabet.charAt(decryptedIndex));
                    }
                }
            }
            System.out.println("Decrypted text: " + decryptText);
            System.out.println("Public Key:  " + public_key_list[1] + "," + public_key_list[0]);
            System.out.println("Private Key: " + private_key_list[1] + "," + private_key_list[0]);
            // Получение p и q из n и d
            BigInteger n = new BigInteger(public_key_list[1]);
            BigInteger d = new BigInteger(private_key_list[0]);
            BigInteger[] factors = factorize(n);
            BigInteger p = factors[0];
            BigInteger q = factors[1];

            if (p != null && q != null) {
                System.out.println("p: " + p);
                System.out.println("q: " + q);
            } else {
                System.out.println("Unable to determine p and q from d and n.");
            }
        }
    }
    public static BigInteger[] factorize(BigInteger n) {
        BigInteger[] factors = new BigInteger[2];

        BigInteger x = BigInteger.valueOf(2);
        BigInteger y = BigInteger.valueOf(2);
        BigInteger d = BigInteger.ONE;

        while (d.equals(BigInteger.ONE)) {
            x = f(x).mod(n);
            y = f(f(y)).mod(n);
            d = x.subtract(y).gcd(n);
        }

        factors[0] = d;
        factors[1] = n.divide(d);

        return factors;
    }

    private static BigInteger f(BigInteger x) {
        return x.pow(2).add(BigInteger.ONE);
    }

    public static BigInteger readBigIntegerFromConsole(String prompt) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println(prompt);
            String input = reader.readLine();
            return new BigInteger(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }

    public static String readStringFromConsole(String prompt) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println(prompt);
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveToFile(String fileName, String data) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(String fileName) {
        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}