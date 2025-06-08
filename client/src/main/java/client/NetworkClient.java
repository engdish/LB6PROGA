package client;

import common.CommandRequest;
import common.CommandResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.TimeUnit;

public class NetworkClient {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private static final int BUFFER_SIZE = 65535;
    private static final long TIMEOUT_MS = 5000;

    public NetworkClient(String host, int port) {
        try {
            this.channel = DatagramChannel.open();
            this.channel.configureBlocking(false);
            this.serverAddress = new InetSocketAddress(host, port);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения клиента: " + e.getMessage(), e);
        }
    }

    public CommandResponse sendRequest(CommandRequest request) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            byte[] data = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(data);
            channel.send(buffer, serverAddress);

            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
                if (channel.receive(buffer) != null) {
                    buffer.flip();
                    byte[] responseData = new byte[buffer.remaining()];
                    buffer.get(responseData);
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {
                        return (CommandResponse) ois.readObject();
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Прервано во время ожидания ответа", e);
                }
            }
            throw new IOException("Нет ответа от сервера");
        } catch (ClassNotFoundException e) {
            throw new IOException("Неверный формат ответа", e);
        }
    }
}