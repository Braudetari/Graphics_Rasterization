package app_interface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import your_code.VertexData;

public class OBJLoader {
	private boolean useSphericalMapping;
	private List<VertexData> vertices;
	private List<TriangleFace> faces;
	private IntBufferWrapper textureImageIntBufferWrapper;

	float minPositionX = Float.MAX_VALUE;
	float maxPositionX = Float.MIN_VALUE;
	float minPositionY = Float.MAX_VALUE;
	float maxPositionY = Float.MIN_VALUE;
	float minPositionZ = Float.MAX_VALUE;
	float maxPositionZ = Float.MIN_VALUE;

	public OBJLoader() {
		this(true); // Default to spherical mapping
	}

	public OBJLoader(boolean useSphericalMapping) {
		this.useSphericalMapping = useSphericalMapping;
	}

	public Vector3f getBoundingBoxDimensions() {
		return new Vector3f(maxPositionX - minPositionX, maxPositionX - minPositionX, maxPositionX - minPositionX);
	}

	public Vector3f getBoundingBoxCenter() {
		return new Vector3f((maxPositionX + minPositionX) / 2, (maxPositionX + minPositionX) / 2,
				(maxPositionX + minPositionX) / 2);
	}

	public void loadOBJ(String filePath) throws IOException {
	    try (InputStream inputStream = new FileInputStream(filePath)) {
	        Obj obj = ObjReader.read(inputStream);
	        Obj objWithTriangulatedFaces = ObjUtils.triangulate(obj);
	        this.vertices = new ArrayList<>();
	        this.faces = new ArrayList<>();

	        List<Vector3f> positions = new ArrayList<>(objWithTriangulatedFaces.getNumVertices());
	        List<Vector3f> normals = new ArrayList<>(objWithTriangulatedFaces.getNumNormals());
	        List<Vector2f> texCoords = new ArrayList<>(objWithTriangulatedFaces.getNumTexCoords());
	        
	        for (int i = 0; i < objWithTriangulatedFaces.getNumVertices(); i++) {
	            FloatTuple vertexTuple = objWithTriangulatedFaces.getVertex(i);
	            Vector3f position = new Vector3f(vertexTuple.getX(), vertexTuple.getY(), vertexTuple.getZ());
	            if(position.x<minPositionX) minPositionX = position.x; 
	            if(position.x>maxPositionX) maxPositionX = position.x; 
	            if(position.y<minPositionY) minPositionY = position.y; 
	            if(position.y>maxPositionY) maxPositionY = position.y; 
	            if(position.z<minPositionZ) minPositionZ = position.z; 
	            if(position.z>maxPositionZ) maxPositionZ = position.z; 
	            positions.add(position);
	        }

	        for (int i = 0; i < objWithTriangulatedFaces.getNumNormals(); i++) {
	            FloatTuple normalTuple = objWithTriangulatedFaces.getNormal(i);
	            normals.add(new Vector3f(normalTuple.getX(), normalTuple.getY(), normalTuple.getZ()));
	        }

	        for (int i = 0; i < objWithTriangulatedFaces.getNumTexCoords(); i++) {
	            FloatTuple texCoordTuple = objWithTriangulatedFaces.getTexCoord(i);
	            texCoords.add(new Vector2f(texCoordTuple.getX(), texCoordTuple.getY()));
	        }

	        Map<String, Integer> vertexMap = new HashMap<>();
	        Random random = new Random();

	        for (int i = 0; i < objWithTriangulatedFaces.getNumFaces(); i++) {
	            ObjFace face = objWithTriangulatedFaces.getFace(i);
	            int[] indices = new int[3];

	            for (int j = 0; j < 3; j++) {
	                int vertexIndex = face.getVertexIndex(j);
	                Vector3f position = positions.get(vertexIndex);

	                Vector3f normal = null;
	                if (face.containsNormalIndices()) {
	                    normal = normals.get(face.getNormalIndex(j));
	                }

	                Vector2f textureCoord = null;
	                if (face.containsTexCoordIndices()) {
	                    textureCoord = texCoords.get(face.getTexCoordIndex(j));
	                }

	                String vertexKey = position.toString() + "_" +
	                        (normal != null ? normal.toString() : "null") + "_" +
	                        (textureCoord != null ? textureCoord.toString() : "null");

	                if (vertexMap.containsKey(vertexKey)) {
	                    indices[j] = vertexMap.get(vertexKey);
	                } else {
//	    	            Vector3f vertexColor = new Vector3f( (float)random.nextDouble(), (float)random.nextDouble(), (float)random.nextDouble());
	                	Vector3f vertexColor;
	                	if(i==0) {
		                	switch(j) {
			                	case 0:
				    	            vertexColor = new Vector3f( 1, 0, 0);
			                		break;
			                	case 1:
				    	            vertexColor = new Vector3f( 0, 1, 0);
			                		break;
			                	case 2:
				    	            vertexColor = new Vector3f( 0, 0, 1);
			                		break;
			                	default:
			    	    	        vertexColor = new Vector3f( (float)random.nextDouble(), (float)random.nextDouble(), (float)random.nextDouble());
		                	}
	                	} else
	                		vertexColor = new Vector3f( (float)random.nextDouble(), (float)random.nextDouble(), (float)random.nextDouble());

	                    VertexData vertex = new VertexData(position, normal, textureCoord, vertexColor);
	                    vertices.add(vertex);
	                    indices[j] = vertices.size() - 1;
	                    vertexMap.put(vertexKey, indices[j]);
	                }
	            }

	            Vector3f faceColor = new Vector3f( (float)random.nextDouble(), (float)random.nextDouble(), (float)random.nextDouble());
	            //Vector3f faceNormal = calculateFaceNormals(indices);
	            //faces.add(new TriangleFace(indices, faceColor, faceNormal));
	            faces.add(new TriangleFace(indices, faceColor));
	        }

	        if (objWithTriangulatedFaces.getNumNormals() == 0) {
	            calculateNormalsSmoothShading();
	        }

	        if (objWithTriangulatedFaces.getNumTexCoords() == 0) {
	            if (useSphericalMapping) {
	                calculateSphericalMapping();
	            } else {
	                calculatePlanarMapping();
	            }
	        }

	        try {
	        	String texturefilePath = filePath.substring(0,filePath.lastIndexOf('.')+1) + "bmp";
		        textureImageIntBufferWrapper = new IntBufferWrapper(texturefilePath);
		        System.out.println();
	        } catch(IOException e) {}
	    }
	}

	public List<VertexData> getVertices() {
		return vertices;
	}

	public List<TriangleFace> getFaces() {
		return faces;
	}

	public IntBufferWrapper getTextureImageIntBufferWrapper() {
		return textureImageIntBufferWrapper;
	}

//	private Vector3f calculateFaceNormals(int[] faceIndices) {
//			Vector3f v0 = vertices.get(faceIndices[0]).point3D;
//			Vector3f v1 = vertices.get(faceIndices[1]).point3D;
//			Vector3f v2 = vertices.get(faceIndices[2]).point3D;
//
//			Vector3f edge1 = new Vector3f(v1).sub(v0);
//			Vector3f edge2 = new Vector3f(v2).sub(v0);
//			Vector3f normal = edge1.cross(edge2).normalize();
//			
//			return normal;
//	}
	
/*	
	private void calculateNormalsFlatShading() {
		for (TriangleFace face : faces) {
			Vector3f v0 = vertices.get(face.indices[0]).point3D;
			Vector3f v1 = vertices.get(face.indices[1]).point3D;
			Vector3f v2 = vertices.get(face.indices[2]).point3D;

			Vector3f edge1 = new Vector3f(v1).sub(v0);
			Vector3f edge2 = new Vector3f(v2).sub(v0);
			Vector3f normal = edge1.cross(edge2).normalize();

			for (int index : face.indices) {
				vertices.get(index).normal = normal;
			}
		}
	}
*/
	private void calculateNormalsSmoothShading() {
		Vector3f[] vertexNormals = new Vector3f[vertices.size()];
		for (int i = 0; i < vertexNormals.length; i++) {
			vertexNormals[i] = new Vector3f(0, 0, 0);
		}

		for (TriangleFace face : faces) {
			Vector3f v0 = vertices.get(face.indices[0]).pointObjectCoordinates;
			Vector3f v1 = vertices.get(face.indices[1]).pointObjectCoordinates;
			Vector3f v2 = vertices.get(face.indices[2]).pointObjectCoordinates;

			Vector3f edge1 = new Vector3f(v1).sub(v0);
			Vector3f edge2 = new Vector3f(v2).sub(v0);
			Vector3f normal = edge1.cross(edge2).normalize();

			for (int index : face.indices) {
				vertexNormals[index].add(normal);
			}
		}

		// Normalize the accumulated normals
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).normalObjectCoordinates = vertexNormals[i].normalize();
		}
	}

	private void calculateSphericalMapping() {
		for (VertexData vertex : vertices) {
			Vector3f position = vertex.pointObjectCoordinates;
			float u = 0.5f + (float) (Math.atan2(position.z, position.x) / (2 * Math.PI));
			float v = 0.5f - (float) (Math.asin(position.y / position.length()) / Math.PI);
			vertex.textureCoordinates = new Vector2f(u, v);
		}
	}

	private void calculatePlanarMapping() {
		for (VertexData vertex : vertices) {
			Vector3f position = vertex.pointObjectCoordinates;
			float u = position.x;
			float v = position.z;
			vertex.textureCoordinates = new Vector2f(u, v);
		}
	}

	public static void main(String[] args) {
		OBJLoader objLoader = new OBJLoader();
		try {
			// objLoader.loadOBJ("./Models/FirstExample.obj");
			// objLoader.loadOBJ("./Models/tests/SeperateNoramlIndices.obj");
			// objLoader.loadOBJ("./Models/tests/test_model.obj");
			objLoader.loadOBJ("./Models/tests/complex_cube.obj");
			// objLoader.loadOBJ("./Models/cow.obj");
			// objLoader.loadOBJ("./Models/pumpkin.obj");

			List<VertexData> vertices = objLoader.getVertices();
			List<TriangleFace> faces = objLoader.getFaces();

			System.out.println("Vertices:");
			for (VertexData vertex : vertices) {
				System.out.println("Position: " + vertex.pointObjectCoordinates + ", Normal: " + vertex.normalObjectCoordinates + ", Texture: "
						+ vertex.textureCoordinates);
			}

			System.out.println("Faces:");
			for (TriangleFace face : faces) {
				System.out.println("Indices: " + Arrays.toString(face.indices));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
