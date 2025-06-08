package server;

import common.CommandResponse;
import server.util.LoggerConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class ResponseSender {
    private static final Logger logger = Logger.getLogger(ResponseSender.class.getName());
    private final DatagramChannel channel;

    public ResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }

    public void sendResponse(CommandResponse response, SocketAddress clientAddress) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            byte[] data = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(data);
            channel.send(buffer, clientAddress);
        }
    }
}