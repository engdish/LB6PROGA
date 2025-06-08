package server;

import common.CommandRequest;
import common.CommandResponse;
import common.model.Product;
import server.command.*;
import server.dao.JdbcUserDao;
import server.dao.UserDao;
import server.dao.DaoException;
import server.model.User;
import server.util.CollectionManager;

import java.util.Map;
import java.util.HashMap;

public class CommandProcessor {
    private final Map<String, CommandHandler> commands = new HashMap<>();
    private final UserDao userDao = new JdbcUserDao();

    public CommandProcessor(CollectionManager collectionManager) {
        registerCommands(collectionManager);
    }

    private void registerCommands(CollectionManager cm) {
        commands.put("help", (req, ownerId) -> {
            StringBuilder sb = new StringBuilder("Доступные команды:\n");
            for (String name : commands.keySet()) {
                sb.append(" - ").append(name).append("\n");
            }
            return new CommandResponse(true, sb.toString());
        });

        commands.put("info", (req, owner) -> new InfoCommand(cm).execute(req));
        commands.put("show", (req, owner) -> new ShowCommand(cm).execute(req));
        commands.put("add", (req, owner) -> { Product p = (Product) req.getArgument();
            try {
                Product saved = cm.add(p, owner);
                return new CommandResponse(true, "Добавлен элемент ID=" + saved.getId());
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка при добавлении: " + e.getMessage());
            }
        });
        commands.put("update", (req, owner) -> {
            Object[] args = (Object[]) req.getArgument();
            int id = (Integer) args[0];
            Product p = (Product) args[1];
            try {
                boolean ok = cm.update(id, p, owner);
                return new CommandResponse(ok,
                        ok ? "Элемент " + id + " обновлён" : "Элемент " + id + " не найден");
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка обновления: " + e.getMessage());
            }
        });
        commands.put("remove_by_id", (req, owner) -> {
            int id = (Integer) req.getArgument();
            try {
                boolean ok = cm.removeById(id, owner);
                return new CommandResponse(ok,
                        ok ? "Элемент " + id + " удалён" : "Элемент " + id + " не найден");
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка удаления: " + e.getMessage());
            }
        });
        commands.put("clear", (req, owner) -> { cm.clear(); return new CommandResponse(true, "Коллекция очищена"); });
        commands.put("add_if_min", (req, owner) -> {
            Product p = (Product) req.getArgument();
            try {
                boolean ok = cm.addIfMin(p, owner);
                return new CommandResponse(ok,
                        ok ? "Добавлено (минимум)" : "Не добавлено (не минимальное)");
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка: " + e.getMessage());
            }
        });
        commands.put("reorder", (req, owner) -> { cm.reorder(); return new CommandResponse(true, "Пересортировано"); });
        commands.put("sort", (req, owner) -> { cm.sort(); return new CommandResponse(true, "Отсортировано"); });
        commands.put("filter_less_than_manufacturer", (req, owner) -> new FilterLessThanManufacturerCommand(cm).execute(req));
        commands.put("filter_greater_than_manufacturer", (req, owner) -> new FilterGreaterThanManufacturerCommand(cm).execute(req));
        commands.put("print_unique_price", (req, owner) -> new PrintUniquePriceCommand(cm).execute(req));
        commands.put("server_save", (req, ownerId) -> new ServerSaveCommand().execute(req, ownerId));

    }



    public CommandResponse process(CommandRequest request) {
        String type = request.getCommandType();

        // 1. Обработка register
        if ("register".equals(type)) {
            String[] creds = (String[]) request.getArgument();
            try {
                User newUser = userDao.register(new User(creds[0], creds[1]));
                return new CommandResponse(true, "Пользователь зарегистрирован, ID=" + newUser.getId());
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка регистрации: " + e.getMessage());
            }
        }


        if ("login".equals(type)) {
            String[] creds = (String[]) request.getArgument();
            try {
                boolean ok = userDao.validate(creds[0], creds[1]);
                return new CommandResponse(ok, ok ? "Авторизация успешна" : "Неверные логин/пароль");
            } catch (DaoException e) {
                return new CommandResponse(false, "Ошибка авторизации: " + e.getMessage());
            }
        }


        try {
            boolean valid = userDao.validate(request.getUsername(), request.getPasswordHash());
            if (!valid) {
                return new CommandResponse(false, "Неверные учётные данные");
            }
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка авторизации: " + e.getMessage());
        }


        User user;
        try {
            user = userDao.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        } catch (DaoException e) {
            return new CommandResponse(false, "Ошибка получения пользователя: " + e.getMessage());
        }
        int ownerId = user.getId();


        CommandHandler handler = commands.get(type);
        if (handler == null) {
            return new CommandResponse(false, "Неизвестная команда: " + type);
        }
        return handler.handle(request, ownerId);
    }


    @FunctionalInterface
    public interface CommandHandler {
        CommandResponse handle(CommandRequest request, int ownerId);
    }
}
