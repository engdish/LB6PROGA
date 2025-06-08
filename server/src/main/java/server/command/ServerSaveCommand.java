package server.command;

import common.CommandRequest;
import common.CommandResponse;


public class ServerSaveCommand {
    public ServerSaveCommand() {

    }


    public CommandResponse execute(CommandRequest request, int ownerId) {
        return new CommandResponse(false, "Команда сохранения в файл не поддерживается в режиме с БД");
    }

    public String getDescription() {
        return "сохранить коллекцию в файл (не поддерживается)";
    }
}
