package server.command;

import common.CommandRequest;
import common.CommandResponse;
import common.model.Product;
import server.dao.DaoException;
import server.util.CollectionManager;


public class UpdateCommand {
    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Обновляет продукт по ID и ownerId.
     * @param request аргумент запроса: массив [id, Product]
     * @param ownerId ID пользователя-владельца
     */
    public CommandResponse execute(CommandRequest request, int ownerId) {
        try {
            Object[] args = (Object[]) request.getArgument();
            int id = (Integer) args[0];
            Product updatedProduct = (Product) args[1];
            boolean updated = collectionManager.update(id, updatedProduct, ownerId);
            if (updated) {
                return new CommandResponse(true, "Элемент " + id + " успешно обновлён");
            } else {
                return new CommandResponse(false, "Элемент " + id + " не найден или нет прав");
            }
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка обновления: " + e.getMessage());
        } catch (ClassCastException e) {
            return new CommandResponse(false, "Неверные аргументы команды обновления");
        }
    }

    public String getDescription() {
        return "обновить элемент по ID";
    }
}
