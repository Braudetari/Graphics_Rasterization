package app_interface;

import java.util.Arrays;

import org.joml.Vector3f;

public class TriangleFace {
    public int[] indices;
    public Vector3f color;
    public Vector3f normal;

    public TriangleFace(int[] indices, Vector3f color) {
//    public TriangleFace(int[] indices, short[] color, Vector3f normal) {
        this.indices = indices;
        this.color = color;
//        this.normal = normal;
    }

	@Override
	public String toString() {
		return String.format("TriangleFace [indices= %s, color= %.3f,%.3f,%.3f]", Arrays.toString(indices), color.get(0), color.get(1), color.get(2));
	}
	
	public static void main(String[] args) {
		int[] ind = {1,2,3};
		TriangleFace tf = new TriangleFace(ind, new Vector3f(0.11111f,0.22222f,0.3333f));
		System.out.println(tf);
	}
}
