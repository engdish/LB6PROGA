package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class ReorderCommand extends Command {
    public ReorderCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        collectionManager.reorder();
        return new CommandResponse(true, "Коллекция успешно пересортирована");
    }

    @Override
    public String getDescription() {
        return "сортировать коллекцию в обратном порядке";
    }
}