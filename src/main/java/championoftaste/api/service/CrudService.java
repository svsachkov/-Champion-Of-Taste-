package championoftaste.api.service;

import championoftaste.exception.ApiRequestException;

import java.util.List;

/**
 * Интерфейс, описывающий CRUD (Create, Read, Update, Delete) операции над объектом (сущностью).
 *
 * @param <T> класс объекта (сущности)
 */
public interface CrudService<T> {

    /**
     * Создаёт новый объект (сущность).
     *
     * @param entity переданный объект (сущность)
     * @throws ApiRequestException выбрасывается в случае, если не удалось создать новый объект (сущность)
     */
    void create(T entity) throws ApiRequestException;

    /**
     * Возвращает список имеющихся объектов (сущностей).
     *
     * @return список объектов (сущностей)
     */
    List<T> readAll();

    /**
     * Возвращает объект (сущность) по заданному id.
     *
     * @param id - id объекта (сущности)
     * @return объект (сущность) с заданным id
     */
    T read(Integer id);

    /**
     * Обновляет объект (сущность), в соответствии с переданным объектом (сущностью).
     *
     * @param id      - id объекта (сущности) для обновления
     * @param updated - объект (сущность), в соответствии с которым надо обновить
     * @return true - данные обновлены, иначе false (объекта (сущности) для обновления не найден)
     * @throws ApiRequestException выбрасывается в случае, если произошла ошибка при обновлении (не удалось обновить)
     */
    boolean update(Integer id, T updated) throws ApiRequestException;

    /**
     * Удаляет объект (сущность) с заданным id.
     *
     * @param id - id объекта (сущности) для удаления
     * @return true - объект удалён, иначе false (не удалось найти объект (сущность) для удаления)
     */
    boolean delete(Integer id);
}
