package amadeus;

import amadeus.core.XmlGenerator;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
public class Process {

    private static boolean isBindIllumination = false;
    private static String targetAvatarPath = "models\\gear\\gear_121.model";
    private static String targetAvatarName = "妙法";
    //the width & heights extension which based on target avatar position
    private static List<Double>distance = Arrays.asList(500.0, 200.0);
    private static int lineCount = 3;
    private static double random = 0.0;
    private static int columnCount = 4;

    public static void main(String[] argvs){

        for(int index=0;index<argvs.length;index++){
            if(Objects.equals(argvs[index], "--light")){
                isBindIllumination = Boolean.parseBoolean(argvs[index+1]);
            }
            if(Objects.equals(argvs[index], "--linecount")){
                lineCount = Integer.parseInt(argvs[index+1]);
            }
            if(Objects.equals(argvs[index], "--columncount")){
                columnCount = Integer.parseInt(argvs[index+1]);
            }
            if(Objects.equals(argvs[index], "--border")){
                distance =  Arrays.stream(argvs[index+1].split(",")).map(Double::valueOf)
                        .collect(Collectors.toList());
            }
            if(Objects.equals(argvs[index], "--modePath")){
                targetAvatarPath = argvs[index+1];
            }
            if(Objects.equals(argvs[index], "--name")){
                targetAvatarName = argvs[index+1];
            }
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document root = builder.parse(new File("Z:\\bianjiqi\\magic\\zhipan2_test.xml"));
            Document lightXml = builder.parse("src/main/resources/templates/light.xml");
            XmlGenerator xmlGenerator = new XmlGenerator(isBindIllumination, targetAvatarName, distance,
                    lineCount, columnCount, random);
            xmlGenerator.init(root.getDocumentElement());
            xmlGenerator.generateXml(root.getDocumentElement(), lightXml.getDocumentElement(), null,
                    "Z:\\bianjiqi\\magic\\magic233.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
