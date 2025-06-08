package server.command;

import common.CommandRequest;
import common.CommandResponse;

import java.util.Map;

public class HelpCommand extends Command {
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        super(null); // CollectionManager не нужен для help
        this.commands = commands;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        StringBuilder helpMessage = new StringBuilder("Доступные команды:\n");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            helpMessage.append(entry.getKey()).append(": ").append(entry.getValue().getDescription()).append("\n");
        }
        return new CommandResponse(true, helpMessage.toString());
    }

    @Override
    public String getDescription() {
        return "вывести доступные команды";
    }
}