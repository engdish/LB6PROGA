package client.util;

import common.model.*;
import java.util.Arrays;
import java.util.Scanner;


public class Console {
    private final Scanner scanner = new Scanner(System.in);

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public String readln() {
        try {
            if (!scanner.hasNextLine()) {
                println("Ввод завершен (EOF). Выход...");
                System.exit(0);
            }
            return scanner.nextLine();
        } catch (Exception e) {
            println("Ошибка ввода. Выход...");
            System.exit(1);
            return "";
        }
    }

    public Product readProduct(int id) {
        String name = readString("Введите название продукта (не может быть пустым):", false);
        float x = readFloat("Введите координату X:");
        float y = readFloat("Введите координату Y:");
        Coordinates coordinates = new Coordinates(x, y);
        double price = readDouble("Введите цену (больше 0):", true);

        UnitOfMeasure unit = readEnum("Введите единицу измерения (METERS, PCS, LITERS) или оставьте пустым:", UnitOfMeasure.class);

        String orgName = readString("Введите имя организации (не может быть пустым):", false);
        String fullName = readString("Введите полное имя организации (или оставьте пустым):", true);

        OrganizationType type = readEnum("Введите тип организации (COMMERCIAL, PUBLIC, GOVERNMENT, PRIVATE_LIMITED_COMPANY) или оставьте пустым:", OrganizationType.class);

        Organization manufacturer = new Organization(orgName, fullName.isEmpty() ? null : fullName, type);
        return new Product(name, coordinates, price, unit, manufacturer);
    }

    public String readString(String prompt, boolean allowEmpty) {
        while (true) {
            println(prompt);
            String input = readln().trim();
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }
            println("Строка не может быть пустой. Попробуйте снова.");
        }
    }

    private float readFloat(String prompt) {
        while (true) {
            println(prompt);
            try {
                return Float.parseFloat(readln().trim());
            } catch (NumberFormatException e) {
                println("Ошибка ввода. Введите число!");
            }
        }
    }

    private double readDouble(String prompt, boolean positive) {
        while (true) {
            println(prompt);
            try {
                double value = Double.parseDouble(readln().trim());
                if (!positive || value > 0) {
                    return value;
                }
                println("Значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                println("Ошибка ввода. Введите число!");
            }
        }
    }

    private <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        println(prompt);
        println("Доступные варианты: " + Arrays.toString(enumClass.getEnumConstants()));
        String input = readln().trim().toUpperCase();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            println("Неверное значение. Попробуйте снова.");
            return readEnum(prompt, enumClass);
        }
    }
}
