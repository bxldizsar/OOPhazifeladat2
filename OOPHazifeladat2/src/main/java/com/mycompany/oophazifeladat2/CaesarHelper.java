/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oophazifeladat2;

/**
 *
 * @author boldi
 */
public class CaesarHelper {
    private static final int SHIFT = 3;
    // Minden betűt/számjegyet SHIFT hellyel előre tol
    public static String caesarEncode(String text){
        String result = "";
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if(c >= 'a' &&  c <= 'z')
            {
               c = (char) ((c - 'a' + SHIFT) % 26 + 'a');
            }
           else if (c >= 'A' && c <= 'Z') {
                c = (char) ((c - 'A' + SHIFT) % 26 + 'A');
            } else if (c >= '0' && c <= '9') {
                c = (char) ((c - '0' + SHIFT) % 10 + '0');
            }
            result = result + c; // más karakter változatlan
        }
        return result;
    }

    // Visszafelé tolja el
    public static String caesarDecode(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) ((c - 'a' - SHIFT + 26) % 26 + 'a');
            } else if (c >= 'A' && c <= 'Z') {
                c = (char) ((c - 'A' - SHIFT + 26) % 26 + 'A');
            } else if (c >= '0' && c <= '9') {
                c = (char) ((c - '0' - SHIFT + 10) % 10 + '0');
            }
            result = result + c;
        }
        return result;
    }

    // Kipróbáló method: eredeti -> kódolt -> visszafejtett
    public static void testCaesar() {
        String original = "Titok123";
        String encoded = caesarEncode(original);
        String decoded = caesarDecode(encoded);
        System.out.println("Eredeti:       " + original);
        System.out.println("Kódolt:        " + encoded);
        System.out.println("Visszafejtett: " + decoded);
    }
}
       