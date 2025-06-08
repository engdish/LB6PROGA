package server.util;

import common.model.Product;
import server.dao.DaoException;
import server.dao.JdbcProductDao;
import server.dao.ProductDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Менеджер коллекции, теперь без файловой подсистемы.
 * Хранит все объекты в памяти, загружая из БД при старте.
 * Синхронизирует доступ через ReentrantLock.
 */
public class CollectionManager {
    private final List<Product> memoryCollection = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final ProductDao productDao = new JdbcProductDao();
    private final LocalDateTime initializationDate = LocalDateTime.now();

    /**
     * Загружает все продукты из БД в память.
     * Вызывать один раз в начале работы сервера.
     */
    public void loadFromDatabase() {
        lock.lock();
        try {
            memoryCollection.clear();
            memoryCollection.addAll(productDao.findAll());
            System.out.println("Loaded " + memoryCollection.size() + " products from DB");
        } catch (DaoException e) {
            throw new RuntimeException("Не удалось загрузить коллекцию из БД", e);
        } finally {
            lock.unlock();
        }
    }

    /** Возвращает все элементы коллекции (копия списка). */
    public List<Product> getAll() {
        lock.lock();
        try {
            return new ArrayList<>(memoryCollection);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавляет новый продукт:
     * 1) сохраняет его в БД (и получает сгенерированный id/дата)
     * 2) добавляет в память только при успешном сохранении
     */
    public Product add(Product product, int ownerId) throws DaoException {
        Product saved = productDao.save(product, ownerId);
        lock.lock();
        try {
            memoryCollection.add(saved);
        } finally {
            lock.unlock();
        }
        return saved;
    }

    /**
     * Обновляет продукт по id и ownerId:
     * 1) обновляет в БД
     * 2) при успехе обновляет в памяти
     * @return true, если обновлено
     */
    public boolean update(int id, Product updatedProduct, int ownerId) throws DaoException {
        boolean dbOk = productDao.update(updatedProduct, ownerId);
        if (dbOk) {
            lock.lock();
            try {
                memoryCollection.removeIf(p -> p.getId() == id);
                memoryCollection.add(updatedProduct);
            } finally {
                lock.unlock();
            }
        }
        return dbOk;
    }

    /**
     * Удаляет продукт по id и ownerId:
     * 1) удаляет из БД
     * 2) при успехе удаляет из памяти
     * @return true, если удалено
     */
    public boolean removeById(int id, int ownerId) throws DaoException {
        boolean dbOk = productDao.deleteById(id, ownerId);
        if (dbOk) {
            lock.lock();
            try {
                memoryCollection.removeIf(p -> p.getId() == id);
            } finally {
                lock.unlock();
            }
        }
        return dbOk;
    }

    /** Полностью очищает коллекцию в памяти (не в БД!). */
    public void clear() {
        lock.lock();
        try {
            memoryCollection.clear();
        } finally {
            lock.unlock();
        }
    }

    /** Информация о коллекции: тип, дата инициализации, размер. */
    public String getInfo() {
        lock.lock();
        try {
            return String.format("Type: %s, Initialization Date: %s, Number of Elements: %d",
                    memoryCollection.getClass().getSimpleName(),
                    initializationDate,
                    memoryCollection.size());
        } finally {
            lock.unlock();
        }
    }

    /** Добавляет, если новый продукт меньше текущего минимума. */
    public boolean addIfMin(Product product, int ownerId) throws DaoException {
        lock.lock();
        try {
            Product min = memoryCollection.stream()
                    .min(Product::compareTo)
                    .orElse(null);
            if (min == null || product.compareTo(min) < 0) {
                add(product, ownerId);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /** Сортирует коллекцию в обратном порядке. */
    public void reorder() {
        lock.lock();
        try {
            memoryCollection.sort((a, b) -> b.compareTo(a));
        } finally {
            lock.unlock();
        }
    }

    /** Сортирует коллекцию по возрастанию id. */
    public void sort() {
        lock.lock();
        try {
            memoryCollection.sort(Product::compareTo);
        } finally {
            lock.unlock();
        }
    }

    /** Фильтрует продукты, у которых имя производителя меньше заданного. */
    public String filterLessThanManufacturer(String name) {
        lock.lock();
        try {
            return memoryCollection.stream()
                    .filter(p -> p.getManufacturer() != null
                            && p.getManufacturer().getName().compareTo(name) < 0)
                    .map(Product::toString)
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        } finally {
            lock.unlock();
        }
    }

    /** Фильтрует продукты, у которых имя производителя больше заданного. */
    public String filterGreaterThanManufacturer(String name) {
        lock.lock();
        try {
            return memoryCollection.stream()
                    .filter(p -> p.getManufacturer() != null
                            && p.getManufacturer().getName().compareTo(name) > 0)
                    .map(Product::toString)
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        } finally {
            lock.unlock();
        }
    }

    /** Возвращает уникальные значения цен (каждая в новой строке). */
    public String printUniquePrice() {
        lock.lock();
        try {
            return memoryCollection.stream()
                    .map(p -> String.format("%.2f", p.getPrice()))
                    .distinct()
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        } finally {
            lock.unlock();
        }
    }

    /** Строковое представление всей коллекции. */
    public String show() {
        lock.lock();
        try {
            return memoryCollection.stream()
                    .map(Product::toString)
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        } finally {
            lock.unlock();
        }
    }
}
