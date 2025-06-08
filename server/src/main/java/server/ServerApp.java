package server;

import server.util.CollectionManager;
import server.util.DBConfig;
import server.util.LoggerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.util.logging.Logger;


public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final int PORT = 12345;

    public static void main(String[] args) {

        LoggerConfig.configure();


        System.out.println("Тест подключения");
        try (Connection conn = DBConfig.getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new RuntimeException("Не удалось получить соединение из пула");
            }
            System.out.println("Успешно получили соединение из пула!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }


        CollectionManager collectionManager = new CollectionManager();
        collectionManager.loadFromDatabase();


        DatagramChannel channel;
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(true);
            System.out.println("Сервер запущен на порту " + PORT);
        } catch (IOException e) {
            logger.severe("Не удалось открыть UDP-канал: " + e.getMessage());
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Шатдаун-хук: завершаем работу сервера...");
            try {
                channel.close();
                DBConfig.shutdownPool();
                logger.info("Сервер корректно завершён.");
            } catch (Exception ex) {
                logger.warning("Ошибка при завершении: " + ex.getMessage());
            }
        }));


        ConnectionHandler handler = new ConnectionHandler(channel, collectionManager);
        handler.run();
    }
}
