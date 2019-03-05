package umoo.wang.beanmanager.common.util;

import sun.net.www.protocol.jar.JarURLConnection;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;

/**
 * Created by yuanchen on 2019/02/15.
 */
public class ClassUtil {
	private final static String CLASS_FILE_SUFFIX = ".class";
	private final static String CLASS_FILE_SEPARATOR = ".";
	private final static String CLASS_PATH_SEPARATOR = "/";

	/**
	 * 扫描指定包名下面的所有类并加载
	 *
	 * @param classLoader
	 * @param packageName
	 * @return
	 */
	public static List<Class<?>> scan(ClassLoader classLoader,
			String packageName) {
		List<Class<?>> result = new ArrayList<>();

		try {
			String packagePath = packageName.replace(CLASS_FILE_SEPARATOR,
					CLASS_PATH_SEPARATOR);
			Enumeration<URL> resources = classLoader.getResources(packagePath);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();

				if (Objects.equals(url.getProtocol(), "file")) {
					loadClassFile(classLoader, packageName, result, url);
				} else if (Objects.equals(url.getProtocol(), "jar")) {
					loadJarFile(classLoader, packageName, result, url);
				}
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static void loadJarFile(ClassLoader classLoader, String packageName,
			List<Class<?>> result, URL url) throws IOException {
		Enumeration<JarEntry> jarEntries = ((JarURLConnection) url
				.openConnection()).getJarFile().entries();

		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			String fileName = jarEntry.getName();

			if (jarEntry.isDirectory()) {
				String clazzName = fileName.replace(CLASS_PATH_SEPARATOR,
						CLASS_FILE_SEPARATOR);
				int endIndex = clazzName.lastIndexOf(CLASS_FILE_SEPARATOR);
				if (endIndex > 0) {
					scan(classLoader, clazzName.substring(0, endIndex));
				}
			}

			loadClass(classLoader, packageName, result, fileName);
		}
	}

	private static void loadClassFile(ClassLoader classLoader,
			String packageName, List<Class<?>> result, URL url)
			throws URISyntaxException {
		File[] files = new File(url.toURI()).listFiles();
		if (files != null) {
			for (File file : files) {
				String fileName = file.getName();
				if (file.isDirectory()) {
					result.addAll(scan(classLoader,
							packageName + CLASS_FILE_SEPARATOR + fileName));
				}

				loadClass(classLoader, packageName, result, fileName);
			}
		}
	}

	private static void loadClass(ClassLoader classLoader, String packageName,
			List<Class<?>> result, String fileName) {
		if (fileName.endsWith(CLASS_FILE_SUFFIX)) {
			try {
				result.add(classLoader.loadClass(
						packageName + CLASS_FILE_SEPARATOR + fileName
						.substring(0, fileName.indexOf(CLASS_FILE_SUFFIX))));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
