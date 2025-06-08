package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class FilterGreaterThanManufacturerCommand extends Command {
    public FilterGreaterThanManufacturerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        String manufacturerName = (String) request.getArgument();
        String result = collectionManager.filterGreaterThanManufacturer(manufacturerName);
        if (result.isEmpty()) {
            return new CommandResponse(true, "Нет элементов с производителем больше указанного");
        }
        return new CommandResponse(true, result);
    }

    @Override
    public String getDescription() {
        return "отображать элементы у которых значение поля производителя выше указанного";
    }
}