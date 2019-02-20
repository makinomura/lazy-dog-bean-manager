package umoo.wang.beanmanager.web.util;

import io.netty.handler.codec.http.FullHttpRequest;
import umoo.wang.beanmanager.common.converter.ConverterFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/31.
 */
public class HttpRequestUtil {
	public static Map<String, String> getQueryParameters(
			FullHttpRequest request) {
		Map<String, String> result = new HashMap<>();

		String[] uriSplit = request.uri().split("\\?");
		if (uriSplit.length == 1) {
			return result;
		}

		for (String pair : uriSplit[1].split("&")) {
			String[] pairSplit = pair.split("=");

			if (pairSplit.length == 1) {
				result.put(pairSplit[0], "");
			} else {
				result.put(pairSplit[0], pairSplit[1]);
			}
		}

		return result;
	}

	public static <T> T parseRequestBody(FullHttpRequest request,
			Class<T> clazz) {
		String bodyString = request.content().toString(StandardCharsets.UTF_8);

		return ConverterFactory.withType(clazz).convert(bodyString, clazz);
	}
}
