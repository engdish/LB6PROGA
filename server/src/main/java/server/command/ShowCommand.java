package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class ShowCommand extends Command {
    public ShowCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        String result = collectionManager.show();
        if (result.isEmpty()) {
            return new CommandResponse(true, "Коллекция пуста!");
        }
        return new CommandResponse(true, result);
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции";
    }
}