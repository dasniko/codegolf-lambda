package dasniko.codegolf;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
class CodeGolf {

    private static final String BUCKETNAME = "codegolf";
    private static final String CLASSNAME = "Codegolf";
    private static final String METHODNAME = "play";

    private static final File ROOT = new File(System.getProperty("java.io.tmpdir"));

    private static AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    @SneakyThrows
    String play(String packageName) {

        String key = packageName + ".class";
        S3Object s3Object = s3.getObject(new GetObjectRequest(BUCKETNAME, key));
        if (null == s3Object) {
            return "No class file found.";
        }
        saveClass(packageName, s3Object);

        Class<?> cls = getClazz(packageName);
        Method method = cls.getDeclaredMethod(METHODNAME, String.class);
        Object instance = cls.newInstance();

        Integer weekday;
        try {
            Map<String, Integer> testData = testDataGenerator.getTestData();
            for (Map.Entry<String, Integer> entry : testData.entrySet()) {
                weekday = run(method, instance, entry.getKey());
                if (weekday.intValue() != entry.getValue().intValue()) {
                    throw new RuntimeException("Test for input <" + entry.getKey() + "> failed, was <" + weekday + ">, should be <" + entry.getValue() + ">");
                }
            }
        } catch (RuntimeException e) {
            log.error("Assertion Error", e);
            return e.getMessage();
        }

        return "OK";
    }

    @SneakyThrows
    private void saveClass(String packageName, S3Object s3Object) {
        File sourceFile = new File(ROOT, packageName + "/Codegolf.class");
        sourceFile.getParentFile().mkdirs();
        byte[] bytes = IOUtils.toByteArray(s3Object.getObjectContent());
        Files.write(sourceFile.toPath(), bytes);
        s3Object.close();
    }

    @SneakyThrows
    private Class<?> getClazz(String packageName) {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{ROOT.toURI().toURL()});
        return Class.forName(packageName + "." + CLASSNAME, true, classLoader);
    }

    @SneakyThrows
    private Integer run(Method method, Object instance, String input) {
        return (Integer) method.invoke(instance, input);
    }

}
