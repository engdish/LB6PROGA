package client;

import common.CommandRequest;
import common.CommandResponse;
import common.model.Product;
import client.util.Console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class CommandReader {
    private final Console console;
    private final NetworkClient networkClient;
    private final Map<String, Runnable> commands = new HashMap<>();
    private String username;
    private String passwordHash;

    public CommandReader(Console console, NetworkClient networkClient) {
        this.console = console;
        this.networkClient = networkClient;

        console.println("=== Аутентификация ===");
        console.println("Если у вас нет аккаунта, пропустите этот шаг");
        this.username = console.readString("Введите имя пользователя:", true);
        String rawPassword = console.readString("Введите пароль:", true);
        this.passwordHash = sha1Hex(rawPassword);
        console.println("=== Готово. Введите 'help' для списка команд или создайте аккаунт, введя 'register'. ===");

        registerCommands();
    }

    private void registerCommands() {
        commands.put("help", this::executeHelp);
        commands.put("info", this::executeInfo);
        commands.put("show", this::executeShow);
        commands.put("add", this::executeAdd);
        commands.put("update", this::executeUpdate);
        commands.put("remove_by_id", this::executeRemoveById);
        commands.put("clear", this::executeClear);
        commands.put("execute_script", this::executeScript);
        commands.put("exit", this::executeExit);
        commands.put("add_if_min", this::executeAddIfMin);
        commands.put("reorder", this::executeReorder);
        commands.put("sort", this::executeSort);
        commands.put("filter_less_than_manufacturer", this::executeFilterLessThanManufacturer);
        commands.put("filter_greater_than_manufacturer", this::executeFilterGreaterThanManufacturer);
        commands.put("print_unique_price", this::executePrintUniquePrice);
        commands.put("register", this::executeRegister);
        commands.put("login", this::executeLogin);
    }

    public void run() {
        while (true) {
            console.print(username + "> ");
            String line = console.readln();
            if (line == null || line.isBlank()) continue;
            String[] parts = line.trim().split("\\s+", 2);
            String cmd = parts[0];
            Runnable action = commands.get(cmd);
            if (action == null) {
                console.println("Команда '" + cmd + "' не найдена!");
                continue;
            }
            try {
                action.run();
            } catch (Exception e) {
                console.println("Ошибка выполнения команды: " + e.getMessage());
            }
        }
    }



    private void executeHelp() {
        sendSimpleRequest("help", null);
    }

    private void executeInfo() {
        sendSimpleRequest("info", null);
    }

    private void executeShow() {
        sendSimpleRequest("show", null);
    }

    private void executeAdd() {
        Product product = console.readProduct(0);
        sendSimpleRequest("add", product);
    }

    private void executeUpdate() {
        console.println("Введите ID для обновления:");
        int id = Integer.parseInt(console.readln().trim());
        Product product = console.readProduct(id);
        sendSimpleRequest("update", new Object[]{id, product});
    }

    private void executeRemoveById() {
        console.println("Введите ID для удаления:");
        int id = Integer.parseInt(console.readln().trim());
        sendSimpleRequest("remove_by_id", id);
    }

    private void executeClear() {
        sendSimpleRequest("clear", null);
    }


    private void executeScript() {
        console.println("Укажите путь к файлу скрипта:");
        String path = console.readln().trim();
        try {
            for (String line : Files.readAllLines(Path.of(path))) {
                if (line.isBlank() || line.startsWith("#")) continue;
                console.println("-> " + line);
                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : null;
                sendSimpleRequest(cmd, arg);
            }
        } catch (IOException e) {
            console.println("Ошибка чтения скрипта: " + e.getMessage());
        }
    }

    private void executeExit() {
        console.println("Выход...");
        System.exit(0);
    }

    private void executeAddIfMin() {
        sendSimpleRequest("add_if_min", console.readProduct(0));
    }

    private void executeReorder() {
        sendSimpleRequest("reorder", null);
    }

    private void executeSort() {
        sendSimpleRequest("sort", null);
    }

    private void executeFilterLessThanManufacturer() {
        console.println("Введите имя производителя для фильтра:");
        sendSimpleRequest("filter_less_than_manufacturer", console.readln().trim());
    }

    private void executeFilterGreaterThanManufacturer() {
        console.println("Введите имя производителя для фильтра:");
        sendSimpleRequest("filter_greater_than_manufacturer", console.readln().trim());
    }

    private void executePrintUniquePrice() {
        sendSimpleRequest("print_unique_price", null);
    }

    private void executeRegister() {
        console.println("=== Регистрация ===");
        String user = console.readString("Придумайте логин:", false);
        String pass = console.readString("Придумайте пароль:", false);
        String hash = sha1Hex(pass);

        try {
            CommandRequest req = new CommandRequest("register", new String[]{user, hash}, username, passwordHash);
            CommandResponse resp = networkClient.sendRequest(req);
            console.println(resp.getMessage());
            if (resp.isSuccess()) {
                this.username = user;
                this.passwordHash = hash;
            }
        } catch (IOException e) {
            console.println("Ошибка сети: " + e.getMessage());
        }
    }

    private void executeLogin() {
        console.println("=== Вход ===");
        String user = console.readString("Логин:", false);
        String pass = console.readString("Пароль:", false);
        String hash = sha1Hex(pass);

        try {
            CommandRequest req = new CommandRequest("login", new String[]{user, hash}, username, passwordHash);
            CommandResponse resp = networkClient.sendRequest(req);
            console.println(resp.getMessage());
            if (resp.isSuccess()) {
                this.username = user;
                this.passwordHash = hash;
            }
        } catch (IOException e) {
            console.println("Ошибка сети: " + e.getMessage());
        }
    }

    // ==============================================

    private void sendSimpleRequest(String command, Object arg) {
        try {
            CommandRequest req = new CommandRequest(command, arg, username, passwordHash);
            CommandResponse resp = networkClient.sendRequest(req);
            console.println(resp.getMessage());
        } catch (IOException e) {
            console.println("Ошибка сети: " + e.getMessage());
        }
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
