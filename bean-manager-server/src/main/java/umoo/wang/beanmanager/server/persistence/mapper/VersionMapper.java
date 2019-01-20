package umoo.wang.beanmanager.server.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import umoo.wang.beanmanager.common.cache.Cache;
import umoo.wang.beanmanager.common.cache.GenericLocalCache;
import umoo.wang.beanmanager.server.persistence.MapperManager;
import umoo.wang.beanmanager.server.persistence.entity.Version;

import java.util.function.Function;

/**
 * Created by yuanchen on 2019/01/20.
 */
public interface VersionMapper {
	@Select("SELECT * FROM `version_t` where id = #{id}")
	Version select(@Param("id") Integer id);

	class VersionCache implements Cache<Integer, Version> {
		private MapperManager manager;
		// TODO 非配置表应该接入外部缓存
		private Cache<Integer, Version> delegate = new GenericLocalCache<>();

		public VersionCache(MapperManager manager) {
			this.manager = manager;
		}

		@Override
		public Version get(Integer key) {
			return delegate.get(key, id -> {
				VersionMapper mapper = manager.getMapper(VersionMapper.class);
				return mapper.select(id);
			});
		}

		@Override
		public Version get(Integer key,
				Function<Integer, Version> cacheLoader) {
			return delegate.get(key, cacheLoader);
		}

		@Override
		public Version delete(Integer key) {
			return delegate.delete(key);
		}

		@Override
		public Version set(Integer key, Version value) {
			return delegate.set(key, value);
		}
	}
}
