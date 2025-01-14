//Students
//Leo Feldman		207434879
//Mor Hodaya Maman	206692592

package your_code;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

import app_interface.DisplayTypeEnum;
import app_interface.ExerciseEnum;
import app_interface.IntBufferWrapper;
import app_interface.OBJLoader;
import app_interface.TriangleFace;
import javafx.scene.image.PixelBuffer;

public class ObjectModel {
	WorldModel worldModel;

	private int imageWidth;
	private int imageHeight;

	private List<VertexData> verticesData;
	private List<TriangleFace> faces;
	private IntBufferWrapper textureImageIntBufferWrapper;

	private Matrix4f modelM = new Matrix4f();
	private Matrix4f lookatM = new Matrix4f();
	private Matrix4f projectionM = new Matrix4f();
	private Matrix4f viewportM = new Matrix4f();
	private Vector3f boundingBoxDimensions;
	private Vector3f boundingBoxCenter;

	private Vector3f lightPositionEyeCoordinates = new Vector3f();
	
	public static ExerciseEnum exercise = ExerciseEnum.EX_9___Lighting;
	
	public ObjectModel(WorldModel worldModel, int imageWidth, int imageHeight) {
		this.worldModel = worldModel;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	void initTransfomations() {
		this.modelM.identity();
		this.modelM.identity();
		this.lookatM.identity();
		this.projectionM.identity();
		this.viewportM.identity();
	}
	
	void setModelM(Matrix4f modelM) {
		this.modelM = modelM;
	}

	void setLookatM(Matrix4f lookatM) {
		this.lookatM = lookatM;
	}

	void setProjectionM(Matrix4f projectionM) {
		this.projectionM = projectionM;
	}

	void setViewportM(Matrix4f viewportM) {
		this.viewportM = viewportM;
	}

	public Vector3f getBoundingBoxDimensions() {
		return boundingBoxDimensions;
	}

	public Vector3f getBoundingBoxCenter() {
		return boundingBoxCenter;
	}

	public boolean load(String fileName) {
		OBJLoader objLoader = new OBJLoader();
		try {
			objLoader.loadOBJ(fileName);
			verticesData = objLoader.getVertices();
			faces = objLoader.getFaces();
			boundingBoxDimensions = objLoader.getBoundingBoxDimensions();
			boundingBoxCenter = objLoader.getBoundingBoxCenter();
			textureImageIntBufferWrapper = objLoader.getTextureImageIntBufferWrapper();
			return true;
		} catch (IOException e) {
			//System.err.println("Failed to load the OBJ file.");
			return false;
		}
	}
	
	public boolean objectHasTexture() {
		return textureImageIntBufferWrapper != null;
	}

	public void render(IntBufferWrapper intBufferWrapper) {
		exercise = worldModel.exercise;
		Vector4f lightPos4H = new Vector4f(worldModel.lightPositionWorldCoordinates,1f);
		Vector4f lightPosTransformed = new Matrix4f(lookatM).transform(lightPos4H);
		lightPositionEyeCoordinates = new Vector3f(lightPosTransformed.x, lightPosTransformed.y, lightPosTransformed.z);
		
		if (verticesData != null) {
			for (VertexData vertexData : verticesData) {
				vertexProcessing(intBufferWrapper, vertexData);
			}
			for (TriangleFace face : faces) {
				rasterization(intBufferWrapper,	
						verticesData.get(face.indices[0]), 
						verticesData.get(face.indices[1]), 
						verticesData.get(face.indices[2]), 
						face.color);
			}
		}
	}

	private void vertexProcessing(IntBufferWrapper intBufferWrapper, VertexData vertex) {

		// Initialize a 4D vector from the 3D vertex point
		Vector4f t = new Vector4f(vertex.pointObjectCoordinates, 1f);
	
		// Transform only model transformation
		Vector4f tModel = modelM.transform(new Vector4f(t));
		//Vector3f pointWorldCoordinates = new Vector3f(tModel.x, tModel.y, tModel.z);
		//lookAt
		Vector4f tLookAt = lookatM.transform(new Vector4f(tModel));
		vertex.pointEyeCoordinates = new Vector3f(tLookAt.x, tLookAt.y, tLookAt.z);
		Vector4f tProjection = projectionM.transform(new Vector4f(tLookAt));
		if(tProjection.w != 0) { //NDC
			tProjection.mul(1/tProjection.w);
		}
		Vector4f tViewPort = viewportM.transform(new Vector4f(tProjection));
		vertex.pointWindowCoordinates = new Vector3f(tViewPort.x, tViewPort.y, tViewPort.z);
			
		// transformation normal from object coordinates to eye coordinates v->normal
		///////////////////////////////////////////////////////////////////////////////////
		transformNormalFromObjectCoordToEyeCoordAndDrawIt(intBufferWrapper, vertex);

		vertex.lightingIntensity0to1 = lightingEquation(vertex.pointEyeCoordinates, vertex.normalEyeCoordinates, lightPositionEyeCoordinates, worldModel.lighting_Diffuse, worldModel.lighting_Specular, worldModel.lighting_Ambient, worldModel.lighting_sHininess);
	}

	private void transformNormalFromObjectCoordToEyeCoordAndDrawIt(IntBufferWrapper intBufferWrapper, VertexData vertex) {
		// transformation normal from object coordinates to eye coordinates v->normal
		///////////////////////////////////////////////////////////////////////////////////
		// --> v->NormalEyeCoordinates
		Matrix4f modelviewM = new Matrix4f(lookatM).mul(modelM);
		Matrix3f modelviewM3x3 = new Matrix3f();
		modelviewM.get3x3(modelviewM3x3);
		vertex.normalEyeCoordinates = new Vector3f();
		modelviewM3x3.transform(vertex.normalObjectCoordinates, vertex.normalEyeCoordinates);
		if (worldModel.displayNormals) {
			// drawing normals
			Vector3f t1 = new Vector3f(vertex.normalEyeCoordinates);
			Vector4f point_plusNormal_eyeCoordinates = new Vector4f(t1.mul(0.1f).add(vertex.pointEyeCoordinates),
					1);
			Vector4f t2 = new Vector4f(point_plusNormal_eyeCoordinates);
			// modelviewM.transform(t2);
			projectionM.transform(t2);
			if (t2.w != 0) {
				t2.mul(1 / t2.w);
			} else {
				System.err.println("Division by w == 0 in vertexProcessing normal transformation");
			}
			viewportM.transform(t2);
			Vector3f point_plusNormal_screen = new Vector3f(t2.x, t2.y, t2.z);
			drawLineDDA(intBufferWrapper, vertex.pointWindowCoordinates, point_plusNormal_screen, 0, 0, 1f);
		}
		
	}
	
	
	private void rasterization(IntBufferWrapper intBufferWrapper, VertexData vertex1, VertexData vertex2, VertexData vertex3, Vector3f faceColor) {

//		Vector3f faceNormal = new Vector3f(vertex2.pointEyeCoordinates).sub(vertex1.pointEyeCoordinates)
//					.cross(new Vector3f(vertex3.pointEyeCoordinates).sub(vertex1.pointEyeCoordinates))
//					.normalize();

			//intBufferWrapper.setPixel((int) vertex1.pointWindowCoordinates.x, (int) vertex1.pointWindowCoordinates.y, 1f, 1f, 1f);
			//intBufferWrapper.setPixel((int) vertex2.pointWindowCoordinates.x, (int) vertex2.pointWindowCoordinates.y, 1f, 1f, 1f);
			//intBufferWrapper.setPixel((int) vertex3.pointWindowCoordinates.x, (int) vertex3.pointWindowCoordinates.y, 1f, 1f, 1f);
			
		if (worldModel.displayType == DisplayTypeEnum.FACE_EDGES) {
			drawLineDDA(intBufferWrapper, vertex1.pointWindowCoordinates, vertex2.pointWindowCoordinates, 1f, 1f, 1f);
			drawLineDDA(intBufferWrapper, vertex1.pointWindowCoordinates, vertex3.pointWindowCoordinates, 1f, 1f, 1f);
			drawLineDDA(intBufferWrapper, vertex2.pointWindowCoordinates, vertex3.pointWindowCoordinates, 1f, 1f, 1f);
		} 
		else {
			Vector4i box = calcBoundingBox(vertex1.pointWindowCoordinates, vertex2.pointWindowCoordinates, vertex3.pointWindowCoordinates, imageWidth, imageHeight);
			BarycentricCoordinates bc = new BarycentricCoordinates(vertex1.pointWindowCoordinates, vertex2.pointWindowCoordinates, vertex3.pointWindowCoordinates);
			for(int y=box.z; y<=box.w; y++) { //Box Y Boundaries (z,w)
				for(int x=box.x; x<=box.y; x++) { //Box X Boundaries (x,y)
					FragmentData fragmentData = new FragmentData();
					bc.calcCoordinatesForPoint(x, y);
					if(bc.isPointInside()) {
						if (worldModel.displayType == DisplayTypeEnum.FACE_COLOR) {
							fragmentData.pixelColor = faceColor;
						}
						else if (worldModel.displayType == DisplayTypeEnum.INTERPOlATED_VERTEX_COLOR) { 
							fragmentData.pixelColor = bc.interpolate(vertex1.color, vertex2.color, vertex3.color);
						}
						else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_FLAT) { 
							 Vector3f fragmentNormal = (new Vector3f(vertex2.pointEyeCoordinates).sub(vertex1.pointEyeCoordinates))
									 						.cross((new Vector3f(vertex3.pointEyeCoordinates).sub(vertex1.pointEyeCoordinates)));
									 
							 fragmentData.pixelIntensity0to1 = lightingEquation(vertex1.pointEyeCoordinates, fragmentNormal, lightPositionEyeCoordinates, worldModel.lighting_Diffuse, worldModel.lighting_Specular, worldModel.lighting_Ambient, worldModel.lighting_sHininess);
						}
						else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_GOURARD) {
							fragmentData.pixelIntensity0to1 = bc.interpolate(vertex1.lightingIntensity0to1, vertex2.lightingIntensity0to1, vertex3.lightingIntensity0to1);
						}
						else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_PHONG) {
							fragmentData.normalEyeCoordinates = bc.interpolate(vertex1.normalEyeCoordinates, vertex2.normalEyeCoordinates, vertex3.normalEyeCoordinates);
							fragmentData.pointEyeCoordinates = bc.interpolate(vertex1.pointEyeCoordinates, vertex2.pointEyeCoordinates, vertex3.pointEyeCoordinates);
						}
						else if (worldModel.displayType == DisplayTypeEnum.TEXTURE) {
							fragmentData.textureCoordinates = bc.interpolate(vertex1.textureCoordinates, vertex2.textureCoordinates, vertex3.textureCoordinates);
						} else if (worldModel.displayType == DisplayTypeEnum.TEXTURE_LIGHTING) { 
							fragmentData.textureCoordinates = bc.interpolate(vertex1.textureCoordinates, vertex2.textureCoordinates, vertex3.textureCoordinates);
							fragmentData.normalEyeCoordinates = bc.interpolate(vertex1.normalEyeCoordinates, vertex2.normalEyeCoordinates, vertex3.normalEyeCoordinates);
							fragmentData.pointEyeCoordinates = bc.interpolate(vertex1.pointEyeCoordinates, vertex2.pointEyeCoordinates, vertex3.pointEyeCoordinates);
						}
						
						float pixelDepth = bc.interpolate(vertex1.pointWindowCoordinates, vertex2.pointWindowCoordinates, vertex3.pointWindowCoordinates).z();
						if(pixelDepth < worldModel.zBuffer[x][y]) {
							worldModel.zBuffer[x][y] = pixelDepth;
							Vector3f pixelColor = fragmentProcessing(fragmentData);
							intBufferWrapper.setPixel((int)x, (int)y, pixelColor);
						}	
					}
				}
			}
		}
		
	}


	private Vector3f fragmentProcessing(FragmentData fragmentData) {
		
		if (worldModel.displayType == DisplayTypeEnum.FACE_COLOR) {
			return fragmentData.pixelColor;
		} else if (worldModel.displayType == DisplayTypeEnum.INTERPOlATED_VERTEX_COLOR) {
			return fragmentData.pixelColor;
		} else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_FLAT) {
			return new Vector3f(fragmentData.pixelIntensity0to1);
		} else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_GOURARD) {
			return new Vector3f(fragmentData.pixelIntensity0to1);
		} else if (worldModel.displayType == DisplayTypeEnum.LIGHTING_PHONG) {
			return new Vector3f(lightingEquation(fragmentData.pointEyeCoordinates, fragmentData.normalEyeCoordinates, lightPositionEyeCoordinates, worldModel.lighting_Diffuse, worldModel.lighting_Specular, worldModel.lighting_Ambient, worldModel.lighting_sHininess));
		} else if (worldModel.displayType == DisplayTypeEnum.TEXTURE) {
			return textureImageIntBufferWrapper.getPixel(
					Math.round((textureImageIntBufferWrapper.getImageWidth()-1)*(fragmentData.textureCoordinates.x)-1), //x
					Math.round((textureImageIntBufferWrapper.getImageHeight()-1)*(fragmentData.textureCoordinates.y))); //y
		} else if (worldModel.displayType == DisplayTypeEnum.TEXTURE_LIGHTING) {
			Vector3f color = textureImageIntBufferWrapper.getPixel(
					Math.round((textureImageIntBufferWrapper.getImageWidth()-1)*(fragmentData.textureCoordinates.x)-1), //x
					Math.round((textureImageIntBufferWrapper.getImageHeight()-1)*(fragmentData.textureCoordinates.y))); //y
			Vector3f lighting = new Vector3f(lightingEquation(fragmentData.pointEyeCoordinates, fragmentData.normalEyeCoordinates, lightPositionEyeCoordinates, worldModel.lighting_Diffuse, worldModel.lighting_Specular, worldModel.lighting_Ambient, worldModel.lighting_sHininess));
			
			return color.mul(lighting.get(0)); 
		}
		return new Vector3f();
		
	}

	

	static void drawLineDDA(IntBufferWrapper intBufferWrapper, Vector3f p1, Vector3f p2, float r, float g, float b) {
		int x1round = Math.round(p1.x);
		int y1round = Math.round(p1.y);
		int x2round = Math.round(p2.x);
		int y2round = Math.round(p2.y);

		int dx = x2round-x1round;
		int dy = y2round-y1round;
		
		
		if((dy<-dx) || (dy == -dx && dx < 0)) {
			x1round = Math.round(p2.x);
			x2round = Math.round(p1.x);
			y1round = Math.round(p2.y);
			y2round = Math.round(p1.y);
			
			dx = x2round-x1round;
			dy = y2round-y1round;
		}
		
		if(Math.abs(y2round-y1round) <= Math.abs(x2round-x1round))
		{
			float a = (float)dy/(float)dx;
			float y = y1round;
			for(int x=x1round; x<=x2round; x++) {
				intBufferWrapper.setPixel(x, Math.round(y), r, g, b);
				y = y+a;
			}
		}
		else {
			float x=x1round;
			float a = (float)dx/(float)dy;
			for (int y=y1round; y<=y2round; y++) {
				intBufferWrapper.setPixel(Math.round(x), y, r,g,b);
				x = x+a;
			}
		}
		
	}



	static Vector4i calcBoundingBox(Vector3f p1, Vector3f p2, Vector3f p3, int imageWidth, int imageHeight) { 
		
		int minX = (int)Math.floor(Math.max(0, Math.min(Math.min(p1.x, p2.x), p3.x)));
		int maxX = (int)Math.ceil(Math.min(imageWidth-1, Math.max(Math.max(p1.x, p2.x), p3.x)));
		int minY = (int)Math.floor(Math.max(0, Math.min(Math.min(p1.y, p2.y), p3.y)));
		int maxY = (int)Math.ceil(Math.min(imageHeight-1, Math.max(Math.max(p1.y, p2.y), p3.y)));
		
		return new Vector4i(minX, maxX, minY, maxY);
	}

	
	float lightingEquation(Vector3f point, Vector3f PointNormal, Vector3f LightPos, float Kd, float Ks, float Ka, float shininess) {
		if(point != null) {
			Vector3f color = lightingEquation(point, PointNormal, LightPos, 
                    new Vector3f(Kd), new Vector3f(Ks), new Vector3f(Ka), shininess);
			
			return color.get(0);
		}
		else {
			return 0f;
		}
	}
	
	
	private static Vector3f lightingEquation(Vector3f point, Vector3f PointNormal, Vector3f LightPos, Vector3f Kd,
			Vector3f Ks, Vector3f Ka, float shininess) {

			
			Vector3f lightPos = LightPos;
			
			Vector3f lightDirection = (new Vector3f(lightPos)
					.sub(point))
					.normalize(); 
			
			Vector3f returnedColor = new Vector3f();
			
			//additions
			Vector3f rayReflection;
			float dotLightN = new Vector3f(lightDirection).dot(PointNormal);
			if(dotLightN < 0) {
				rayReflection = new Vector3f(0f,0f,0f);
			}
			else {
				rayReflection = (
							new Vector3f(PointNormal)
							.mul(dotLightN).normalize()
							.mul(2)
						)
						.sub(lightDirection)
						.normalize();
			}
			Vector3f rayOriginDirection = (new Vector3f().sub(point)).normalize();
			float dotSpectral = Math.max(0, rayOriginDirection.dot(rayReflection));
			double shinyPow = (Math.pow(dotSpectral,shininess));
			returnedColor.add(
					new Vector3f(Ks)
					.mul((float)shinyPow)
					);
			
			returnedColor.add(new Vector3f(Kd)
			.mul(	
					Math.max(0, (new Vector3f(PointNormal).normalize())
							.dot(lightDirection))
				));
			returnedColor.add(new Vector3f(Ka));
			
		return returnedColor = new Vector3f((returnedColor.x+returnedColor.y+returnedColor.z)/3);
	}	
}


