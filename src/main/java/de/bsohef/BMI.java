package de.bsohef;

import java.util.Scanner;

public class BMI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double gewicht;
        do {
            System.out.println("Was ist ihr Körpergewicht(in KG)?");
            gewicht = scanner.nextDouble();
            if (gewicht < 0) {
                System.out.println("Fehler! Bitte geben Sie ein reales Gewicht ein.");
            }
        } while (gewicht < 0);

        double koerpergroesse;
        do {
            System.out.println("Wie groß sind sie (in Metern)?");
            koerpergroesse = scanner.nextDouble();
            if (koerpergroesse < 0) {
                System.out.println("Fehler! Bitte geben Sie eine reale Größe ein.");
            }
        } while (koerpergroesse < 0);


         double BMI = (gewicht/(koerpergroesse*koerpergroesse));
            if (BMI < 18.5){
                System.out.println("Sie sind untergewichtig,"+ BMI);
        } else if (BMI >= 18.5 && BMI <= 24.9 ) {
                System.out.println("Sie haben ein Normalgewicht, "+ BMI);
            } else if (BMI >= 25 && BMI <= 29.9 ) {
                System.out.println("Sie sind leicht Übergewichtig,"+ BMI);
            }
            else {
                System.out.println("Sind sind stark übergewichtig!"+ BMI);
            }
    }
}
