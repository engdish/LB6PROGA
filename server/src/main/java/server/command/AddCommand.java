package server.command;

import common.CommandRequest;
import common.CommandResponse;
import common.model.Product;
import server.util.CollectionManager;
import server.dao.DaoException;

/**
 * AddCommand с поддержкой ownerId.
 */
public class AddCommand {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Добавляет продукт в БД и в память при успехе.
     * @param request аргумент запроса содержит Product
     * @param ownerId ID пользователя-владельца
     */
    public CommandResponse execute(CommandRequest request, int ownerId) {
        try {
            Product product = (Product) request.getArgument();
            Product saved = collectionManager.add(product, ownerId);
            return new CommandResponse(true, "Продукт успешно добавлен с ID=" + saved.getId());
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка добавления продукта: " + e.getMessage());
        }
    }

    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }
}
