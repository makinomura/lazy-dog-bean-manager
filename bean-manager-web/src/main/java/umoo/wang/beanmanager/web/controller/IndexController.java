package umoo.wang.beanmanager.web.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import umoo.wang.beanmanager.web.support.Mapping;

/**
 * Created by yuanchen on 2019/01/31.
 */
@Mapping(path = "/index")
public class IndexController {

	@Mapping(path = "/hello")
	public String hello(FullHttpRequest request) {
		return "hello world!";
	}
}
