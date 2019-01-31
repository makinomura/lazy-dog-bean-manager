package umoo.wang.beanmanager.web.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import umoo.wang.beanmanager.persistence.SqlSessionManager;
import umoo.wang.beanmanager.persistence.generated.mapper.VersionMapper;
import umoo.wang.beanmanager.web.persistence.entity.Version;
import umoo.wang.beanmanager.web.support.Mapping;
import umoo.wang.beanmanager.web.util.HttpRequestUtil;

import java.util.Map;

/**
 * Created by yuanchen on 2019/01/31.
 */
@Mapping(path = "/version")
public class VersionController {

	@Mapping(path = "")
	public Version hello(FullHttpRequest request) {
		Map<String, String> queryParameters = HttpRequestUtil
				.getQueryParameters(request);

		String id = queryParameters.getOrDefault("id", "1");

		Version v = SqlSessionManager.execute(true, session -> {
			VersionMapper mapper = session.getMapper(VersionMapper.class);

			return mapper.selectOne(Integer.parseInt(id));
		});
		return v;
	}
}
