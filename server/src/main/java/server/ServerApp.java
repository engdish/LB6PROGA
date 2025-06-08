package server;

import server.util.CollectionManager;
import server.util.DBConfig;
import server.util.LoggerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

/**
 * Основной класс запуска сервера с многопоточностью через ConnectionHandler.
 */
public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // 1. Настройка логирования
        LoggerConfig.configure();

        // 2. Тест подключения к БД
        System.out.println("=== Тест подключения через HikariCP ===");
        try (Connection conn = DBConfig.getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new RuntimeException("Не удалось получить соединение из пула");
            }
            System.out.println("Успешно получили соединение из пула!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 3. Загрузка коллекции из БД
        CollectionManager collectionManager = new CollectionManager();
        collectionManager.loadFromDatabase();

        // 4. Настройка UDP-канала
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

        // 5. Грейсфул-шатдаун
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

        // 6. Запуск ConnectionHandler
        ConnectionHandler handler = new ConnectionHandler(channel, collectionManager);
        handler.run();
    }
}
