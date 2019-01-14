package umoo.wang.beanmanager.common.util;

import java.util.Objects;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class StringUtil {

	public static boolean isNullOrEmpty(String s) {
		return s == null || Objects.equals(s, "");
	}
}
