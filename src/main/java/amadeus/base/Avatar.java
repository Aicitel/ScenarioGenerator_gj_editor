package amadeus.base;

import org.w3c.dom.*;

public class Avatar extends BaseObject {

    public static String ENTITY_KEY = "Entity";
    public static String AVATAR_PATH = "avatar";

    private final Element entity;

    public Avatar(Element object) {
        super(object);
        this.entity = super.getTargetElement(ENTITY_KEY);
    }

    public void setAvatarPath(String path){
        this.entity.setAttribute(AVATAR_PATH, path);
    }

    public String getAvatarPath(){
        return this.entity.getAttribute(AVATAR_PATH);
    }

    public Node getEntity() {
        return entity;
    }

}
