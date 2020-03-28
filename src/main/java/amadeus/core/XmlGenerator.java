package amadeus.core;

import amadeus.base.Avatar;
import amadeus.base.Light;
import org.w3c.dom.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static amadeus.Process.posZ;

public class XmlGenerator {

    private boolean isBindIllumination = true;
    private String targetAvatarPath = "";
    private List<Double> border;
    private double randomRate = 0.3;
    private double cellWidth;
    private double cellHeight;
    private int lineCount;
    private int columnCount;

    private final Random random = new Random();

    public XmlGenerator(boolean isBindIllumination, String targetAvatarPath, List<Double> border,
                        int lineCount, int columnCount, double randomRate) {
        this.isBindIllumination = isBindIllumination;
        this.targetAvatarPath = targetAvatarPath;
        this.border = border;
        this.lineCount = lineCount;
        this.randomRate = randomRate;
        this.columnCount = columnCount;
        this.cellWidth = (border.get(2) - border.get(0)) / columnCount;
        this.cellHeight = (border.get(3) - border.get(1)) / lineCount;
    }

    public void generateXml(Element rootTemplate, Element avatarTemplate, Element lightTemplate, String outputFilePath) throws Exception{
        Avatar baseAvatar = new Avatar(avatarTemplate);
        List<Double>initPos = baseAvatar.getInitPos();
        for(int lineIndex=0; lineIndex < lineCount; lineIndex++){
            for(int columnIndex=0; columnIndex < columnCount; columnIndex++){
                Avatar avatar = new Avatar((Element)avatarTemplate.cloneNode(true));
                avatar.setAvatarPath(targetAvatarPath);
                avatar.setName("avatar" + lineIndex + "-" + columnIndex);
                List<Double>posXY = this.calculatePos(columnIndex, lineIndex);
                avatar.setInitPos(posXY.get(0), posXY.get(1), posZ);
                double ori = random.nextInt(360);
                avatar.setInitOri( ori,0.0, 0.0);
                Element element = (Element)avatar.getElement().cloneNode(true);
                Element importedNode = (Element)rootTemplate.getOwnerDocument().importNode(element,true);
                rootTemplate.appendChild(importedNode);

//                Light light = new Light((Element)lightTemplate.cloneNode(true));
//                light.setInitPos(posXY.get(0), posXY.get(1), posZ);
//                rootTemplate.appendChild(rootTemplate.getOwnerDocument().importNode(light.getElement(),true));
            }
        }
        this.trans2File(rootTemplate, outputFilePath);
    }

    public void trans2File(Element root, String filePath) throws Exception{
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer trans = tff.newTransformer();
        trans.setOutputProperty("encoding", "utf-8");
        DOMSource domSource = new DOMSource(root);

        StreamResult sr = new StreamResult(new File(filePath));
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(domSource, sr);
    }

    private double[] randomPos(double cellWidth, double cellHeight){
        //rate * 100 to get lower and upper based on 100
        //E.X rate = 0.3, the random range would be 70 ~ 130
        return new double[]{
            (random.nextInt((int)(randomRate * 100 * 2) )- randomRate * 100) / 100.0 * cellWidth,
            (random.nextInt((int)(randomRate * 100 * 2) )- randomRate * 100) / 100.0 * cellHeight
        };
    }

    private List<Double> calculatePos(int columnIndex, int lineIndex){
        double incX = columnIndex * cellWidth + cellWidth / 2 ;
        double incY = lineIndex * cellHeight + cellHeight / 2;
        double[] random = this.randomPos(cellWidth, cellHeight);
        return Arrays.asList(incX + border.get(0) + random[0] , incY + border.get(1) + random[1]);
    }
}
