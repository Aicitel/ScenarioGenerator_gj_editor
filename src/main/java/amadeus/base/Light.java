package amadeus.base;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Light extends BaseObject {

    public static String ENTITY_KEY = "Entity";

    private final Element entity;

    public Light(Element object) {
        super(object);
        this.entity = super.getTargetElement(ENTITY_KEY);
    }

    public Node getEntity() {
        return entity;
    }
}
