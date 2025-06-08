package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public abstract class Command {
    protected final CollectionManager collectionManager;

    public Command(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public abstract CommandResponse execute(CommandRequest request);

    public abstract String getDescription();
}