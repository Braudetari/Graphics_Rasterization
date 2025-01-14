//Students
//Leo Feldman		207434879
//Mor Hodaya Maman	206692592

package your_code;

import java.nio.IntBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
	
	public Matrix4f Move4DHMatrix(Matrix4f m, float tx, float ty, float tz) {
		return m.mul(new Matrix4f(	
				1f, 0f, 0f, tx,
				0f, 1f, 0f, ty,
				0f, 0f, 1f, tz,
				0f, 0f, 0f, 1f
			).transpose());
	}
	
	public Matrix4f Scale4DHMatrix(Matrix4f m, float sx, float sy, float sz) {
		return m.mul(new Matrix4f(
					sx, 0f, 0f, 0f,
					0f, sy, 0f, 0f,
					0f, 0f, sz, 0f,
					0f, 0f, 0f, 1f
				).transpose());
	}
	
	public void render(IntBufferWrapper intBufferWrapper) {
		counter+=1;
		intBufferWrapper.imageClear();
		clearZbuffer();
		object1.initTransfomations();

		if (exercise.ordinal() == ExerciseEnum.EX_3_1_Object_transformation___translation.ordinal()) {
			
			Matrix4f transMat = new Matrix4f(
						1f, 0f, 0f, (float)Math.sin(counter/(float)10)*(imageWidth/4),
						0f, 1f, 0f, 0f,
						0f, 0f, 1f, 0f,
						0f, 0f, 0f, 1f
					).transpose();
			
			object1.setModelM(transMat);
		}
	
		if (exercise.ordinal() == ExerciseEnum.EX_3_2_Object_transformation___scale.ordinal()) {
			Matrix4f CenterMat = new Matrix4f(
						1f, 0f, 0f, -(float)(imageWidth)/2,
						0f, 1f, 0f, -(float)(imageHeight)/2,
						0f, 0f, 1f, 0f,
						0f, 0f, 0f, 1f
					).transpose();

			Matrix4f ResizeMat = new Matrix4f(
						1f+0.1f*(float)Math.sin((float)counter/10), 0f,	0f,	0f,
						0f, 1f+0.1f*(float)Math.sin((float)counter/10), 0f, 0f,
						0f, 0f, 1f, 0f,
						0f, 0f, 0f, 1f
					).transpose();
			
			Matrix4f DecenterMat = new Matrix4f(
					1f, 0f, 0f, (float)(imageWidth)/2,
					0f, 1f, 0f, (float)(imageHeight)/2,
					0f, 0f, 1f, 0f,
					0f, 0f, 0f, 1f
				).transpose();
			
			Matrix4f Result = DecenterMat .mul(ResizeMat.mul(CenterMat));
			
			object1.setModelM(Result);
		}

		if (exercise.ordinal() == ExerciseEnum.EX_3_3_Object_transformation___4_objects.ordinal()) {
			
		Matrix4f m = new Matrix4f(
					1f,0f,0f,0f,
					0f,1f,0f,0f,
					0f,0f,1f,0f,
					0f,0f,0f,1f
				).transpose();
		
		Matrix4f ResizeMat = new Matrix4f(
					1/2f, 0f,	0f,	0f,
					0f, 1/2f, 0f, 0f,
					0f, 0f, 1f, 0f,
					0f, 0f, 0f, 1f
				).transpose();
		
		
			m = ResizeMat.mul(m);
			object1.setModelM(m);
			object1.render(intBufferWrapper);
			m = Move4DHMatrix(m, 0f,imageHeight,0f);
			object1.setModelM(m);
			object1.render(intBufferWrapper);
			m = Move4DHMatrix(m, imageWidth,0f,0f);
			object1.setModelM(m);
			object1.render(intBufferWrapper);
			m = Move4DHMatrix(m, 0f,-imageHeight,0f);
			object1.setModelM(m);
		}

		if(projectionType==ProjectionTypeEnum.ORTHOGRAPHIC) {
			Vector3f objectDimension = object1.getBoundingBoxDimensions();
			Vector3f objectCenter = object1.getBoundingBoxCenter();
			float objectRight = 1.5f;
			float objectLeft = -1.5f;
			float objectTop = 1.5f;
			float objectBottom = -1.5f;
			float objectNear = 0f;
			float objectFar = 100f;
			
			Matrix4f projectionM = new Matrix4f().ortho(objectLeft, objectRight, objectBottom, objectTop, objectNear, objectFar);
			
			object1.setProjectionM(projectionM);
		}

		if(projectionType==ProjectionTypeEnum.PERSPECTIVE) {
			float yFOV = (float)(30f/180f*Math.PI);
			float aspect = imageWidth/(float)imageHeight;
			float zNear = 1;
			float zFar = 100;
			Matrix4f projectionM = new Matrix4f().perspective(yFOV, aspect, zNear, zFar);
			object1.setProjectionM(projectionM);
		}
		Matrix4f viewportM = YoursUtilities.createViewportMatrix(0f,0f, imageWidth, imageHeight);
		
		object1.setViewportM(viewportM);
		
		//lookAt
		//CameraPos = eye
		//CameraLookAtCenter = center
		//cameraUp = up
//		Vector3f lookatW = (new Vector3f(cameraPos).sub(cameraLookAtCenter)).normalize();
//		Vector3f lookatU = (new Vector3f(cameraUp).cross(lookatW)).normalize();
//		Vector3f lookatV = new Vector3f(lookatW).cross(lookatU);
//		
//		Matrix4f transformEyeToCenter = new Matrix4f(
//			1f, 0f, 0f, -cameraPos.x,
//			0f, 1f, 0f, -cameraPos.y,
//			0f, 0f, 1f, -cameraPos.z,
//			0f, 0f, 0f, 1f
//		).transpose();
//		
//		Matrix4f transformAligner = new Matrix4f(
//				lookatU.x, lookatU.y, lookatU.z, 0f,
//				lookatV.x, lookatV.y, lookatV.z, 0f,
//				lookatW.x, lookatW.y, lookatW.z, 0f,
//				0f, 0f, 0f, 1f
//		).transpose();
//		
//		Matrix4f transformLookAtM = new Matrix4f(transformAligner).mul(transformEyeToCenter);
		
		Matrix4f transformLookAtM = new Matrix4f().lookAt(cameraPos, cameraLookAtCenter, cameraUp);
		object1.setLookatM(transformLookAtM);
		object1.render(intBufferWrapper);
	}
	
	private void clearZbuffer() {
		for(int i=0; i<imageHeight; i++)
			for(int j=0; j<imageWidth; j++)
				zBuffer[i][j] = 1;
	}	
}
