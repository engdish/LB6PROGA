package common;

import java.io.Serial;
import java.io.Serializable;
import java.net.SocketAddress;

public class CommandRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String commandType;
    private final Object argument;
    private transient SocketAddress clientAddress;
    private final String username;
    private final String passwordHash;

    public CommandRequest(String commandType, Object argument, String username, String passwordHash) {
        this.commandType = commandType;
        this.argument = argument;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }

    public String getPasswordHash() { return passwordHash; }

    public String getCommandType() {
        return commandType;
    }

    public Object getArgument() {
        return argument;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(SocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
}