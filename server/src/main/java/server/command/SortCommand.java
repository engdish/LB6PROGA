package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class SortCommand extends Command {
    public SortCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        collectionManager.sort();
        return new CommandResponse(true, "Коллекция успешно отсортирована");
    }

    @Override
    public String getDescription() {
        return "сортировать коллекцию по возрастанию";
    }
}