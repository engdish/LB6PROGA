package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class ClearCommand extends Command {
    public ClearCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        collectionManager.clear();
        return new CommandResponse(true, "Коллекция очищена!");
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }
}