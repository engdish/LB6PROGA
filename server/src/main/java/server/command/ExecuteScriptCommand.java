package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

import java.io.File;
import java.util.Scanner;

public class ExecuteScriptCommand extends Command {
    public ExecuteScriptCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        String fileName = (String) request.getArgument();
        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            return new CommandResponse(false, "Файл не существует или не может быть прочитан!");
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String commandLine = scanner.nextLine().trim();
                if (!commandLine.isEmpty()) {
                    return new CommandResponse(false, "Выполнение скрипта не поддерживается на сервере!");
                }
            }
            return new CommandResponse(true, "Скрипт исполнен успешно!");
        } catch (Exception e) {
            return new CommandResponse(false, "Ошибка выполнения скрипта: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "выполнить скрипт из указанного файла";
    }
}