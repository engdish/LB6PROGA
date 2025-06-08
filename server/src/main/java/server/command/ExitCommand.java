package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class ExitCommand extends Command {
    public ExitCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        return new CommandResponse(false, "Сервер не поддерживает команду 'выход'");
    }

    @Override
    public String getDescription() {
        return "выйти из программы(клиент)";
    }
}