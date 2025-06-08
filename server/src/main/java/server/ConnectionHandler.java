package server;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public class ConnectionHandler {

    public ConnectionHandler(DatagramChannel channel, CollectionManager collectionManager) {
        this(channel, collectionManager, Runtime.getRuntime().availableProcessors());
    }

    private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
    private final DatagramChannel channel;
    private final CollectionManager collectionManager;
    private final ExecutorService readPool;
    private final ForkJoinPool processPool = ForkJoinPool.commonPool();

    public ConnectionHandler(DatagramChannel channel, CollectionManager collectionManager, int readThreads) {
        this.channel = channel;
        this.collectionManager = collectionManager;
        this.readPool = Executors.newFixedThreadPool(readThreads);
    }

    public void run() {
        RequestReader reader = new RequestReader(channel);
        ResponseSender sender = new ResponseSender(channel);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.info("Ожидание запроса...");


                readPool.submit(() -> {
                    try {
                        var request = reader.readRequest();
                        logger.info("Полученный запрос: " + request.getCommandType());

                        processPool.submit(() -> {
                            try {
                                CommandProcessor processor = new CommandProcessor(collectionManager);
                                CommandResponse response = processor.process(request);

                                // Отправка ответа в новом потоке
                                new Thread(() -> {
                                    try {
                                        sender.sendResponse(response, request.getClientAddress());
                                        logger.info("Ответ отправлен.");
                                    } catch (IOException e) {
                                        logger.warning("Ошибка отправки ответа: " + e.getMessage());
                                    }
                                }).start();

                            } catch (Exception e) {
                                logger.warning("Ошибка обработки запроса: " + e.getMessage());
                            }
                        });

                    } catch (IOException | ClassNotFoundException e) {
                        logger.warning("Ошибка чтения запроса: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                logger.severe("Критическая ошибка в ConnectionHandler: " + e.getMessage());
                break;
            }
        }

        // Завершение работы и освобождение ресурсов
        shutdown();
    }

    private void shutdown() {
        try {
            readPool.shutdownNow();
            processPool.shutdown();
            channel.close();
            logger.info("ConnectionHandler завершён.");
        } catch (IOException e) {
            logger.severe("Ошибка закрытия канала: " + e.getMessage());
        }
    }
}
