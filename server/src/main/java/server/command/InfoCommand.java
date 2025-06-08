package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class InfoCommand extends Command {
    public InfoCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        return new CommandResponse(true, collectionManager.getInfo());
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции";
    }
}