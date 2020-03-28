package amadeus;

import amadeus.core.XmlGenerator;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
public class Process {

    private static boolean isBindIllumination = true;
    private static String targetAvatarPath = "avatar\\mjh507_100_f000_h507.avatar";
    private static List<Double>border = Arrays.asList(-69488.2, 85892.6, -62598.5,82240.2);
    private static int lineCount = 7;
    private static int columnCount = 10;
    public static double posZ = 2756.9;

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
                border =  Arrays.stream(argvs[index+1].split(",")).map(Double::valueOf)
                        .collect(Collectors.toList());
            }
            if(Objects.equals(argvs[index], "--modePath")){
                targetAvatarPath = argvs[index+1];
            }
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document root = builder.parse("src/main/resources/templates/template.xml");
            Document avatarXml = builder.parse("src/main/resources/templates/avatar.xml");
            Document lightXml = builder.parse("src/main/resources/templates/light.xml");
            XmlGenerator xmlGenerator = new XmlGenerator(isBindIllumination, targetAvatarPath, border,
                    lineCount, columnCount, 0.6);
            xmlGenerator.generateXml(root.getDocumentElement(), avatarXml.getDocumentElement(),lightXml.getDocumentElement(),
                    "Z:\\bianjiqi\\magic\\zuihoude2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
