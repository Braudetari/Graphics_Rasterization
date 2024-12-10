package your_code;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import app_interface.DisplayTypeEnum;
import app_interface.ExerciseEnum;
import app_interface.IntBufferWrapper;
import app_interface.ProjectionTypeEnum;

public class WorldModel {

	// type of rendering
	public ProjectionTypeEnum projectionType;
	public DisplayTypeEnum displayType;
	public boolean displayNormals;
	public YourSelectionEnum yourSelection;
	
	// camera location parameters
	public Vector3f cameraPos = new Vector3f();
	public Vector3f cameraLookAtCenter = new Vector3f();
	public Vector3f cameraUp = new Vector3f();
	public float horizontalFOV;

	// transformation parameters
	public float modelScale;

	// lighting parameters
	public float lighting_Diffuse;
	public float lighting_Specular;
	public float lighting_Ambient;
	public float lighting_sHininess;
	public Vector3f lightPositionWorldCoordinates = new Vector3f();
	
	public ExerciseEnum exercise;

	private int imageWidth;
	private int imageHeight;

	private ObjectModel object1;
	
	float zBuffer[][];
	
	private int counter = 0;
	
	public WorldModel(int imageWidth, int imageHeight) {
		this.imageWidth  = imageWidth;
		this.imageHeight = imageHeight;
		this.zBuffer = new float[imageWidth][imageHeight];
	}


	public boolean load(String fileName) {
		object1 = new ObjectModel(this, imageWidth, imageHeight);
		return object1.load(fileName);
	}
	
	public boolean modelHasTexture() {
		return object1.objectHasTexture();
	}
	
	public void render(IntBufferWrapper intBufferWrapper) {
		counter+=1;
		intBufferWrapper.imageClear();
		clearZbuffer();
		object1.initTransfomations();

		if (exercise.ordinal() == ExerciseEnum.EX_3_1_Object_transformation___translation.ordinal()) {

		}
	
		if (exercise.ordinal() == ExerciseEnum.EX_3_2_Object_transformation___scale.ordinal()) {

		}

		if (exercise.ordinal() == ExerciseEnum.EX_3_3_Object_transformation___4_objects.ordinal()) {

		}

		if(projectionType==ProjectionTypeEnum.ORTHOGRAPHIC) {

		}

		if(projectionType==ProjectionTypeEnum.PERSPECTIVE) {

		}
		
		object1.render(intBufferWrapper);
	}
	
	private void clearZbuffer() {
		for(int i=0; i<imageHeight; i++)
			for(int j=0; j<imageWidth; j++)
				zBuffer[i][j] = 1;
	}	
}
