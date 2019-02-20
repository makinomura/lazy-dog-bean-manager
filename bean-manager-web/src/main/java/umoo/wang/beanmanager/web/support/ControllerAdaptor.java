package umoo.wang.beanmanager.web.support;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import umoo.wang.beanmanager.common.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/31.
 */
public class ControllerAdaptor extends AbstractRequestProcessor {

	private Object controller;

	private Map<MappingConfig, Method> mappingMethod;

	private ThreadLocal<Method> currentHandleMethod = new ThreadLocal<>();

	public ControllerAdaptor(Object controller) {
		this.controller = controller;
		doScan();
	}

	@Override
	public boolean support(FullHttpRequest request) {

		Method bestMatchMethod = null;
		String bestMatchPath = "/";

		for (Map.Entry<MappingConfig, Method> entry : mappingMethod
				.entrySet()) {
			MappingConfig config = entry.getKey();
			if (request.method().equals(config.method)
					&& request.uri().startsWith(config.path)) {
				if (config.path.length() > bestMatchPath.length()) {
					bestMatchPath = config.path;
					bestMatchMethod = entry.getValue();
				}
			}
		}

		if (bestMatchMethod != null) {
			currentHandleMethod.set(bestMatchMethod);
			return true;
		}

		return false;
	}

	@Override
	public FullHttpResponse process(FullHttpRequest request) {
		Method method = currentHandleMethod.get();
		if (method == null) {
			boolean support = support(request);
			if (!support) {
				return error(null);
			}

			method = currentHandleMethod.get();
		}

		try {
			return ok(method.invoke(controller, request));
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return error(e.getMessage());
		} finally {
			currentHandleMethod.set(null);
		}
	}

	private void doScan() {
		mappingMethod = new HashMap<>();

		Class<?> clazz = controller.getClass();
		Mapping clazzMapping = clazz.getAnnotation(Mapping.class);

		String basePath = "";
		if (clazzMapping != null) {
			basePath = clazzMapping.path();
		}

		basePath = appendPrefix(basePath);

		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			method.setAccessible(true);
			Mapping mapping = method.getAnnotation(Mapping.class);

			if (mapping != null && method.getParameterCount() == 1
					&& method.getParameterTypes()[0] == FullHttpRequest.class) {
				String path = mapping.path();
				path = appendPrefix(path);

				MappingConfig mappingConfig = new MappingConfig(
						new HttpMethod(mapping.method()), basePath + path);

				mappingMethod.put(mappingConfig, method);
			}
		}
	}

	private String appendPrefix(String path) {
		if (!path.startsWith("/") && !StringUtil.isNullOrEmpty(path)) {
			path = "/" + path;
		}

		return path;
	}

	@Data
	@AllArgsConstructor
	private static class MappingConfig {
		HttpMethod method;
		String path;
	}
}
