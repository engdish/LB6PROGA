package client;

import client.util.Console;

public class ClientApp {
    public static void main(String[] args) {
        Console console = new Console();
        NetworkClient networkClient = new NetworkClient("localhost", 12345);
        CommandReader reader = new CommandReader(console, networkClient);
        reader.run();
    }
}