package server.command;

import common.CommandRequest;
import common.CommandResponse;
import common.model.Product;
import server.dao.DaoException;
import server.util.CollectionManager;

/**
 * Команда добавления продукта, если он меньше минимального, с учётом прав.
 */
public class AddIfMinCommand {
    private final CollectionManager collectionManager;

    public AddIfMinCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Добавляет продукт в коллекцию, если он минимален.
     * @param request аргумент запроса: Product
     * @param ownerId ID пользователя-владельца
     */
    public CommandResponse execute(CommandRequest request, int ownerId) {
        try {
            Product product = (Product) request.getArgument();
            boolean added = collectionManager.addIfMin(product, ownerId);
            if (added) {
                return new CommandResponse(true, "Продукт добавлен как минимальный");
            } else {
                return new CommandResponse(false, "Продукт не добавлен, он не минимальный");
            }
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка при добавлении: " + e.getMessage());
        }
    }

    public String getDescription() {
        return "добавить новый элемент, если меньше минимума";
    }
}
