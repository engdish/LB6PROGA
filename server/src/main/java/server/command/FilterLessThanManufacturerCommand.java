package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.util.CollectionManager;

public class FilterLessThanManufacturerCommand extends Command {
    public FilterLessThanManufacturerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        String manufacturerName = (String) request.getArgument();
        String result = collectionManager.filterLessThanManufacturer(manufacturerName);
        if (result.isEmpty()) {
            return new CommandResponse(true, "Нет элементов с производителем выше указанного");
        }
        return new CommandResponse(true, result);
    }

    @Override
    public String getDescription() {
        return "отобразить элементы у которых значение поля производителя меньше указанного";
    }
}