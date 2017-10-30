package info.minutesgone.data;

import java.util.List;

/**
 * Created by bane on 10.10.2017.
 */

public interface PersistenceService<T> {

    public void create(T entity);
    public void update(T entity);
    public void delete(T entity);
    public void save(T entity);
    public List<T> getAll();

    void close();

}
