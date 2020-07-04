package amadeus.core;

import amadeus.base.Avatar;
import amadeus.base.Light;
import amadeus.exception.MoveTrackNotFoundException;
import amadeus.exception.TargetElementNotFoundException;
import org.w3c.dom.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class XmlGenerator {

    private boolean isBindIllumination;
    private String targetAvatarName;
    private List<Double> distance;
    private List<Double> border;
    private List<Double> rotateVector;
    private double randomRate;
    private double cellWidth;
    private double cellHeight;
    private int lineCount;
    private int columnCount;
    private double posZ;

    private Avatar targetAvatar = null;

    private final Random random = new Random();

    public XmlGenerator(boolean isBindIllumination, String targetAvatarName, List<Double> distance,
                        int lineCount, int columnCount, double randomRate) {
        this.isBindIllumination = isBindIllumination;
        this.targetAvatarName = targetAvatarName;
        this.distance = distance;
        this.lineCount = lineCount;
        this.randomRate = randomRate;
        this.columnCount = columnCount;
    }

    public void init(Element rootTemplate){
        NodeList nodes = rootTemplate.getChildNodes();
        for(int nodeIndex =0; nodeIndex < nodes.getLength(); nodeIndex++){
            if(nodes.item(nodeIndex) instanceof Element) {
                Element node = (Element) nodes.item(nodeIndex);
                if (Objects.equals(targetAvatarName,node.getAttribute("name"))){
                    try {
                        this.targetAvatar = new Avatar((Element) nodes.item(nodeIndex).cloneNode(true));
//                        rootTemplate.removeChild(nodes.item(nodeIndex));
                    } catch (MoveTrackNotFoundException e){
                        throw new MoveTrackNotFoundException(String.format("%s doesn't has move axis", targetAvatarName));
                    }
                    this.posZ = this.targetAvatar.getInitPos().get(2);
                }
            }
        }
        if(this.targetAvatar == null){
            throw new TargetElementNotFoundException(String.format("Target avatar: %s not found!", targetAvatarName));
        }
        this.rotateVector = Arrays.asList(
                Math.sin(Math.toRadians( this.targetAvatar.getInitOri().get(0))),
                Math.cos(Math.toRadians( this.targetAvatar.getInitOri().get(0))));
        this.border = Arrays.asList(
                this.targetAvatar.getInitPos().get(0) - distance.get(0) / 2 ,
                this.targetAvatar.getInitPos().get(1) - distance.get(1) / 2 ,
                this.targetAvatar.getInitPos().get(0) + distance.get(0) / 2 ,
                this.targetAvatar.getInitPos().get(1) + distance.get(1) / 2 );

        this.cellWidth = (border.get(2) - border.get(0)) / columnCount;
        this.cellHeight = (border.get(3) - border.get(1)) / lineCount;
    }

    private double[] join(List<Double>delta, List<Double>vector){
        double deltaX = delta.get(0);
        double deltaY = delta.get(1);
//        double resultX = deltaX / vector.get(1) - deltaY / vector.get(0);
//        double resultY = - deltaX / vector.get(0) - deltaY / vector.get(1);
        double resultX = deltaX * vector.get(1) - deltaY * vector.get(0);
        double resultY = deltaX * vector.get(0) + deltaY * vector.get(1);
        return new double[]{resultX, resultY};
    }

    public void generateXml(Element rootTemplate, Element lightTemplate, OutputStream os, String outputFilePath) throws Exception{
//        Avatar baseAvatar = new Avatar(avatarTemplate);
//        List<Double>initPos = baseAvatar.getInitPos();
        for(int lineIndex=0; lineIndex < lineCount; lineIndex++){
            for(int columnIndex=0; columnIndex < columnCount; columnIndex++){
                Avatar avatar = new Avatar((Element) targetAvatar.getElement().cloneNode(true));
                //Avatar avatar = new Avatar((Element)avatarTemplate.cloneNode(true));
//                avatar.setAvatarPath(targetAvatarPath);
                avatar.setName(targetAvatarName + lineIndex + "-" + columnIndex);
                List<Double>posXY = this.calculatePos(columnIndex, lineIndex);
                avatar.setInitPos(posXY.get(0), posXY.get(1), posZ);

                //double ori = random.nextInt(360);
                //avatar.setInitOri( ori,0.0, 0.0);

                Element element = (Element)avatar.getElement().cloneNode(true);
                Element importedNode = (Element)rootTemplate.getOwnerDocument().importNode(element,true);
                rootTemplate.appendChild(importedNode);

                if(isBindIllumination) {
                    Light light = new Light((Element) lightTemplate.cloneNode(true));
                    light.setInitPos(posXY.get(0), posXY.get(1), posZ);
                    rootTemplate.appendChild(rootTemplate.getOwnerDocument().importNode(light.getElement(), true));
                }
            }
        }
        if(outputFilePath == null){
            this.trans2Os(rootTemplate, os);
        } else {
            this.trans2File(rootTemplate, outputFilePath);
        }
    }

    private void trans2File(Element root, String filePath) throws Exception{
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer trans = tff.newTransformer();
        trans.setOutputProperty("encoding", "utf-8");
        DOMSource domSource = new DOMSource(root);

        StreamResult sr = new StreamResult(new File(filePath));
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(domSource, sr);
    }

    private void trans2Os(Element root, OutputStream os) throws Exception{
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer trans = tff.newTransformer();
        trans.setOutputProperty("encoding", "utf-8");
        DOMSource domSource = new DOMSource(root);

        StreamResult sr = new StreamResult(os);
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(domSource, sr);
    }

    private double[] randomPos(double cellWidth, double cellHeight){
        if(randomRate == 0){
            return new double[]{0,0};
        }
        //rate * 100 to get lower and upper based on 100
        //E.X rate = 0.3, the random range would be 70 ~ 130
        return new double[]{
            (random.nextInt((int)(randomRate * 100 * 2) )- randomRate * 100) / 100.0 * cellWidth,
            (random.nextInt((int)(randomRate * 100 * 2) )- randomRate * 100) / 100.0 * cellHeight
        };
    }

    private List<Double> calculatePos(int columnIndex, int lineIndex){
        double incX = (columnIndex - (columnCount - 1) / 2.0) * cellWidth;
        double incY = (lineIndex  - (lineCount - 1 ) / 2.0)   * cellHeight;
        double[] rotateInc = this.join(Arrays.asList(incX, incY), this.rotateVector);
        double[] random = this.randomPos(cellWidth, cellHeight);
        return Arrays.asList(
                (rotateInc[0] + this.targetAvatar.getInitPos().get(0) + random[0]) ,
                (rotateInc[1] + this.targetAvatar.getInitPos().get(1) + random[1]));
    }
}

