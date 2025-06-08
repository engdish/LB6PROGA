package server.command;

import common.CommandRequest;
import common.CommandResponse;

/**
 * Команда сохранения коллекции не поддерживается в версии с БД.
 */
public class ServerSaveCommand {
    public ServerSaveCommand() {
        // ничего не нужно
    }

    /**
     * Просто возвращает сообщение о том, что команда не поддерживается.
     */
    public CommandResponse execute(CommandRequest request, int ownerId) {
        return new CommandResponse(false, "Команда сохранения в файл не поддерживается в режиме с БД");
    }

    public String getDescription() {
        return "сохранить коллекцию в файл (не поддерживается)";
    }
}
