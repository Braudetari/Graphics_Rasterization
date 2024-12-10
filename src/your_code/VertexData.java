package your_code;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class VertexData {
	//lםaded from file
    public Vector3f pointObjectCoordinates;
    public Vector3f normalObjectCoordinates;
    public Vector2f textureCoordinates;
    
    public Vector3f color;

    //calculated in vertex processing and used by face processing
    Vector3f pointEyeCoordinates;
    Vector3f pointWindowCoordinates;
    Vector3f normalEyeCoordinates;
	float lightingIntensity0to1;
    
	
    public VertexData(Vector3f pointObjectCoordinates, Vector3f normalObjectCoordinates, Vector2f textureCoordinates, Vector3f color) {
        this.pointObjectCoordinates = pointObjectCoordinates;
        this.normalObjectCoordinates = normalObjectCoordinates;
        this.textureCoordinates = textureCoordinates;
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format(
            "Vertex [p=(%.3f, %.3f, %.3f)", 
            pointObjectCoordinates.x(), pointObjectCoordinates.y(), pointObjectCoordinates.z()
        );
    }    
}

