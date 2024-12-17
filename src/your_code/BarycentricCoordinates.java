package your_code;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The {@code BarycentricCoordinates} class computes and represents barycentric coordinates
 * for a triangle defined by three vertices. It provides methods to calculate
 * the barycentric coordinates of a point relative to the triangle, check if a point lies inside
 * the triangle, and interpolate properties based on these coordinates.
 */
public class BarycentricCoordinates {
    private Vector3f barycentricCoordinates;

    private Vector3f LineOppositeToV1;
    private Vector3f LineOppositeToV2;
    private Vector3f LineOppositeToV3;
    private float d1;
    private float d2;
    private float d3;
    
    /**
     * Constructs a {@code BarycentricCoordinates} object for a triangle defined by three
     * vertices. vertices are Vector3f but only the first two component are used as x and y coordinates.
     *
     * @param v1 the first vertex of the triangle.
     * @param v2 the second vertex of the triangle.
     * @param v3 the third vertex of the triangle.
     */	
	public BarycentricCoordinates(Vector3f v1, Vector3f v2, Vector3f v3) {
        LineOppositeToV1 = lineFrom2Points(v2, v3);
		LineOppositeToV2 = lineFrom2Points(v1, v3);
		LineOppositeToV3 = lineFrom2Points(v1, v2);
		d1 = semiDistanceOfPointFromLine(LineOppositeToV1, v1);
		d2 = semiDistanceOfPointFromLine(LineOppositeToV2, v2);
		d3 = semiDistanceOfPointFromLine(LineOppositeToV3, v3);
	}
	
    /**
     * Computes and updates the barycentric coordinates for a specified point
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */	
	public void calcCoordinatesForPoint(float x, float y) {
		float BarycentricAlfa = semiDistanceOfPointFromLine(LineOppositeToV1, x, y) / d1;
		float BarycentricBeta = semiDistanceOfPointFromLine(LineOppositeToV2, x, y) / d2;
		float BarycentricGama = semiDistanceOfPointFromLine(LineOppositeToV3, x, y) / d3;
		barycentricCoordinates = new Vector3f(BarycentricAlfa, BarycentricBeta, BarycentricGama);
	}

    /**
     * Checks if the currently computed barycentric coordinates indicate that the point lies
     * inside the triangle.
     *
     * @return {@code true} if the point is inside the triangle; {@code false} otherwise.
     */	
	public boolean isPointInside() {
		return ((barycentricCoordinates.get(0) >= 0) && (barycentricCoordinates.get(1) >= 0)
			&& (barycentricCoordinates.get(2) >= 0) && (barycentricCoordinates.get(0) <= 1)
			&& (barycentricCoordinates.get(1) <= 1) && (barycentricCoordinates.get(2) <= 1)); 
	}
	
	@Override
	public String toString() {
		return String.format("Alpha:%.2f, Beta:%.2f, Gamma:%.2f.",getAlpha(),getBeta(), getGamma());
	}

	//helper methods - method to compute line cooficient from 2 points
	private static Vector3f lineFrom2Points(Vector3f p1, Vector3f p2) {
		return new Vector3f(p2.y - p1.y, -(p2.x - p1.x), p1.y * p2.x - p1.x * p2.y);
	}

	//helper methods - method to compute the semi distance of point from line
	private static float semiDistanceOfPointFromLine(Vector3f line, Vector3f p) {
		return line.get(0) * p.x + line.get(1) * p.y + line.get(2);
	}
	private static float semiDistanceOfPointFromLine(Vector3f line, float x, float y) {
		return line.get(0) * x + line.get(1) *y + line.get(2);
	}
	
	
	public float getAlpha() {
		return barycentricCoordinates.get(0);
	}
	public float getBeta() {
		return barycentricCoordinates.get(1);
	}
	public float getGamma() {
		return barycentricCoordinates.get(2);
	}
	public Vector3f getCoordinates() {
		return new Vector3f(barycentricCoordinates);
	}

	
	public static void main(String[] args) {
		
		Vector3f v1 = new Vector3f(5,1,0);
		Vector3f v2 = new Vector3f(10,3,0);
		Vector3f v3 = new Vector3f(7,4,0);
		BarycentricCoordinates bc = new BarycentricCoordinates(v1, v2, v3);
		bc.calcCoordinatesForPoint(7, 2);
		System.out.println(bc);
		System.out.println(bc.isPointInside());
		bc.calcCoordinatesForPoint(7, 1);
		System.out.println(bc);
		System.out.println(bc.isPointInside());
		
//		////////////////////////////////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
//		/////////////// solution ///////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
//		Vector3f v1 = new Vector3f(1,1,10);
//		Vector3f v2 = new Vector3f(10,1,20);
//		Vector3f v3 = new Vector3f(5.5f,7,30);
//		BarycentricCoordinates barycentricCoordinates = new BarycentricCoordinates(v1,v2,v3);
//		barycentricCoordinates.calcCoordinatesForPoint(5.5f, 2);
//		System.out.println(barycentricCoordinates);
//		System.out.println(barycentricCoordinates.isPointInside());
//		barycentricCoordinates.calcCoordinatesForPoint(5.5f, 0);
//		System.out.println(barycentricCoordinates);
//		System.out.println(barycentricCoordinates.isPointInside());
//		System.out.println();
//		
//
//		Vector3f v1_ = new Vector3f(0,0,0);
//		Vector3f v2_ = new Vector3f(10,0,0);
//		Vector3f v3_ = new Vector3f(5,10,0);
//		BarycentricCoordinates barycentricCoordinates_ = new BarycentricCoordinates(v1_,v2_,v3_);
//		barycentricCoordinates_.calcCoordinatesForPoint(5,0);
//		System.out.println(barycentricCoordinates_);
//		System.out.println(barycentricCoordinates_.interpolate(10, 30, 60));
//		barycentricCoordinates_.calcCoordinatesForPoint(5,5);
//		System.out.println(barycentricCoordinates_);
//		System.out.println(barycentricCoordinates_.interpolate(10, 30, 60));
//		
//		////////////////////////////////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
//		/////////////// solution ///////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////
	
	}
	

	
	
	
	
	//interpolation methods
	///////////////////////////////////////////////////
	
    /**
     * Interpolates a scalar property across the triangle using the current barycentric coordinates.
     *
     * @param p1_property the property value at the first vertex.
     * @param p2_property the property value at the second vertex.
     * @param p3_property the property value at the third vertex.
     * @return the interpolated property value.
     */	
	public float interpolate(float p1_property, float p2_property, float p3_property) {
		return    barycentricCoordinates.get(0) * p1_property 
				+ barycentricCoordinates.get(1) * p2_property
				+ barycentricCoordinates.get(2) * p3_property;
	}
    /**
     * Interpolates a 2D vector property across the triangle using the current barycentric coordinates.
     *
     * @param p1_property the property value at the first vertex.
     * @param p2_property the property value at the second vertex.
     * @param p3_property the property value at the third vertex.
     * @return the interpolated property as a {@code Vector2f}.
     */	
	public Vector2f interpolate(Vector2f p1_property, Vector2f p2_property, Vector2f p3_property) {
		float x = p1_property.x * barycentricCoordinates.get(0) 
				+ p2_property.x * barycentricCoordinates.get(1)
				+ p3_property.x * barycentricCoordinates.get(2);
		float y = p1_property.y * barycentricCoordinates.get(0) 
				+ p2_property.y * barycentricCoordinates.get(1)
				+ p3_property.y * barycentricCoordinates.get(2);
		return new Vector2f(x, y);
	}
    /**
     * Interpolates a 3D vector property across the triangle using the current barycentric coordinates.
     *
     * @param p1_property the property value at the first vertex.
     * @param p2_property the property value at the second vertex.
     * @param p3_property the property value at the third vertex.
     * @return the interpolated property as a {@code Vector3f}.
     */	
	public Vector3f interpolate(Vector3f p1_property, Vector3f p2_property, Vector3f p3_property) {
		float x = p1_property.x * barycentricCoordinates.x 
				+ p2_property.x * barycentricCoordinates.y
				+ p3_property.x * barycentricCoordinates.z;
		float y = p1_property.y * barycentricCoordinates.x 
				+ p2_property.y * barycentricCoordinates.y
				+ p3_property.y * barycentricCoordinates.z;
		float z = p1_property.z * barycentricCoordinates.x 
				+ p2_property.z * barycentricCoordinates.y
				+ p3_property.z * barycentricCoordinates.z;
		return new Vector3f(x, y, z);
	}
}
