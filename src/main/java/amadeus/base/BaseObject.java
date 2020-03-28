package amadeus.base;

import org.w3c.dom.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BaseObject {

    public static final String TYPE_KEY = "type";
    public static final String MOVE_AXIS_TYPE = "TRACK_MOVE";
    public static final String POS_KEY = "pos";
    public static final String SCALE_KEY = "scale";
    public static final String ORI_KEY = "ori";

    protected final Element element;

    protected String name;
    //TODO
    //protected List<Element>keyFrames;

    protected Element moveAxis;

    public BaseObject(Element element){
        this.element = element;
        this.moveAxis = this.getTargetTypeElement(MOVE_AXIS_TYPE);
    }

    protected Element getTargetElement(String tag){
        for(int index=0; index<this.element.getChildNodes().getLength(); index++){
            if(element.getChildNodes().item(index) instanceof Element) {
                Element child = (Element) element.getChildNodes().item(index);
                if (Objects.equals(child.getNodeName(), tag)) {
                    return child;
                }
            }
        }
        return null;
    }


    protected Element getTargetTypeElement(String type){
        for(int index=0; index<this.element.getChildNodes().getLength(); index++){
            if(element.getChildNodes().item(index) instanceof Element) {
                Element child = (Element) element.getChildNodes().item(index);
                if (child.hasAttribute(TYPE_KEY)
                        && Objects.equals(child.getAttribute(TYPE_KEY), type)) {
                    return child;
                }
            }
        }
        return null;
    }

    public Element getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getMoveAxis() {
        return moveAxis;
    }

    public void setMoveAxis(Element moveAxis) {
        this.moveAxis = moveAxis;
    }

    public void setInitScale(double x, double y, double z){
        Element initPos = this.getInitTrackFrameInner();
        initPos.setAttribute(SCALE_KEY, this.generatePosString(x,y,z));
    }

    public List<Double> getInitScale(){
        Element initPos = this.getInitTrackFrameInner();
        return Arrays.stream(initPos.getAttribute(SCALE_KEY).split(","))
                .map(Double::valueOf).collect(Collectors.toList());
    }

    public void setInitOri(double x, double y, double z){
        Element initPos = this.getInitTrackFrameInner();
        initPos.setAttribute(ORI_KEY, this.generateOriString(x,y,z));
    }

    public List<Double> getInitOri(double x, double y, double z){
        Element initPos = this.getInitTrackFrameInner();
        return Arrays.stream(initPos.getAttribute(ORI_KEY).split(","))
                .map(Double::valueOf).collect(Collectors.toList());
    }

    public void setInitPos(double x, double y, double z){
        Element initPos = this.getInitTrackFrameInner();
        initPos.setAttribute(POS_KEY, this.generatePosString(x,y,z));
    }

    public List<Double> getInitPos(){
        Element initPos = this.getInitTrackFrameInner();
        return Arrays.stream(initPos.getAttribute(POS_KEY).split(","))
                .map(Double::valueOf).collect(Collectors.toList());
    }

    protected String generatePosString(double x, double y, double z){
        return String.format("%s,%s,%s",x, y ,z);
    }

    protected String generateOriString(double x, double y, double z){
        return String.format("%s,%s,%s",x, y ,z);
    }

    protected Element getInitTrackFrameInner(){
        if(this.moveAxis.getChildNodes().getLength() == 0){
            throw new NotImplementedException();
        }
        return this.getFirstNonTextChild(this.moveAxis, 3);
                //.getChildNodes().item(0)  //key frames
                //.getChildNodes().item(0)  //key frame info
                //.getChildNodes().item(0);  // dk why here is another list which named eye node
    }

    protected Element getFirstNonTextChild(Element element){
        for(int index=0; index<element.getChildNodes().getLength();index++){
            if(element.getChildNodes().item(index) instanceof Element) {
                return (Element)element.getChildNodes().item(index);
            }
        }
        return null;
    }

    protected Element getFirstNonTextChild(Element element, int layer){
        layer--;
        for(int index=0; index<element.getChildNodes().getLength();index++){
            if(element.getChildNodes().item(index) instanceof Element) {
                if(layer <=0) {
                    return (Element) element.getChildNodes().item(index);
                } else{
                    return getFirstNonTextChild((Element) element.getChildNodes().item(index), layer);
                }
            }
        }
        return null;
    }
}
