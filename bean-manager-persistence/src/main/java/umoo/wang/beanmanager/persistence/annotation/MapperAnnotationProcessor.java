package umoo.wang.beanmanager.persistence.annotation;

import umoo.wang.beanmanager.persistence.support.Mapper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Created by yuanchen on 2019/01/28. 注解处理器 自动根据实体类生成对应的Mapper文件
 */
@SupportedAnnotationTypes(value = {
		"umoo.wang.beanmanager.persistence.annotation.GenerateMapperClass" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MapperAnnotationProcessor extends AbstractProcessor {

	private final static String MAPPER_CLAZZ = Mapper.class.getCanonicalName();

	private final static String GENERATED_MAPPER_PACKAGE = "umoo.wang.beanmanager.persistence.generated.mapper";

	private final static String GENERATED_MAPPER_NAME = "%sMapper";

	private final static String GENERATED_MAPPER_CODE_TEMPLATE = "package %s;\n"
			+ "\nimport %s;\n"
			+ "\npublic interface %s extends Mapper<%s, %s> {\n" + "}\n";

	private Filer filer;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		filer = processingEnv.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		Set<? extends Element> entities = roundEnv
				.getElementsAnnotatedWith(GenerateMapperClass.class);

		for (Element entity : entities) {
			String entityName = entity.getSimpleName().toString(); // 实体类名
			String mapperName = String.format(GENERATED_MAPPER_NAME,
					entityName);// 生成的Mapper名称

			String entityQualifiedName = ((TypeElement) entity)
					.getQualifiedName().toString();// 实体类全名

			String pkQualifiedName = ((TypeElement) ((DeclaredType) ((DeclaredType) ((TypeElement) entity)
					.getInterfaces().get(0)).getTypeArguments().get(0))
							.asElement()).getQualifiedName().toString();// 主键类全名
			String javaCode = String.format(GENERATED_MAPPER_CODE_TEMPLATE,
					GENERATED_MAPPER_PACKAGE, MAPPER_CLAZZ, mapperName,
					pkQualifiedName, entityQualifiedName);

			try {
				JavaFileObject javaFile = filer.createSourceFile(
						GENERATED_MAPPER_PACKAGE + "." + mapperName);
				Writer writer = javaFile.openWriter();
				writer.write(javaCode);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}
}
