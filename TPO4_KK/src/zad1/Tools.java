/**
 *
 *  @author Kłodawski Kamil S24777
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class Tools {

    static Options createOptionsFromYaml(String fileName) throws Exception {

        Yaml yaml = new Yaml(new Constructor());
        InputStream inputStream = Files.newInputStream(new File(fileName).toPath());

        Map<String, Object> yamlMap = yaml.load(inputStream);
        String host = (String) yamlMap.get("host");
        int port = (int) yamlMap.get("port");
        boolean concurMode = (boolean) yamlMap.get("concurMode");
        boolean showSendRes = (boolean) yamlMap.get("showSendRes");
        Map<String, List<String>> clientsMap = (Map<String, List<String>>) yamlMap.get("clientsMap");

        return new Options(host,port,concurMode,showSendRes,clientsMap);
    }

}
