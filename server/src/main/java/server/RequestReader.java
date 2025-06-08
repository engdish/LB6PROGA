package server;

import common.CommandRequest;
import server.util.LoggerConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class RequestReader {
    private static final Logger logger = Logger.getLogger(RequestReader.class.getName());
    private final DatagramChannel channel;
    private static final int BUFFER_SIZE = 65535;

    public RequestReader(DatagramChannel channel) {
        this.channel = channel;
    }

    public CommandRequest readRequest() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketAddress clientAddress = channel.receive(buffer);
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            CommandRequest request = (CommandRequest) ois.readObject();
            request.setClientAddress(clientAddress);
            return request;
        }
    }
}