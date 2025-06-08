package server.command;

import common.CommandRequest;
import common.CommandResponse;
import server.dao.DaoException;
import server.util.CollectionManager;


public class RemoveByIdCommand {
    private final CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Удаляет продукт из БД и памяти при совпадении ownerId.
     * @param request аргумент запроса: ID (Integer)
     * @param ownerId ID пользователя-владельца
     */
    public CommandResponse execute(CommandRequest request, int ownerId) {
        try {
            int id = (Integer) request.getArgument();
            boolean removed = collectionManager.removeById(id, ownerId);
            if (removed) {
                return new CommandResponse(true, "Продукт " + id + " успешно удалён");
            } else {
                return new CommandResponse(false, "Продукт " + id + " не найден или нет прав");
            }
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка удаления продукта: " + e.getMessage());
        } catch (ClassCastException e) {
            return new CommandResponse(false, "Неверный аргумент для команды удаления");
        }
    }

    public String getDescription() {
        return "удалить элемент по ID";
    }
}