package umoo.wang.beanmanager.server.persistence.entity;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/21.
 */
public interface Dao<PK, E extends PrimaryKey<PK>> {
	E selectOne(PK pk);

	List<E> listAll();

	List<E> list(E entity);

	void save(E entity);

	void delete(PK pk);
}
