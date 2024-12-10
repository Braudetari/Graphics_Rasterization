package your_code;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

import app_interface.IntBufferWrapper;

public class UnitTests {
	public static void main(String[] args) {
		System.out.println("Line rasterization unit tests");
		System.out.println("==============================");
		testDrawLineDDA();

		System.out.println("Bounding box test");
		System.out.println("=======================");
        testCalcBoundingBox();
	}

	
	// Line rasterization unit tests
	////////////////////////////////////////////////////////
	public static void testDrawLineDDA() {

		IntBufferWrapper intBuffer = new IntBufferWrapper(600, 400);

		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(550, 150, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(550, 200, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(550, 250, 0), 1f,1f,1f);

		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(350, 350, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(300, 350, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(250, 300, 0), 1f,1f,1f);

		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(200, 150, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(200, 200, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(200, 250, 0), 1f,1f,1f);
		
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(250, 100, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(300,  50, 0), 1f,1f,1f);
		ObjectModel.drawLineDDA(intBuffer, new Vector3f(300, 200, 0), new Vector3f(350,  50, 0), 1f,1f,1f);
		
		intBuffer.saveToBMP("line drawing test.bmp");
		System.out.println("image saved to \"line drawing test.bmp\", check the image.\n");
	}

	
	// Bounding box test
	////////////////////////////////////////////////////////
    private static void testCalcBoundingBox() {
        Vector3f p1 = new Vector3f(1.5f, 6.3f, 0.0f);
        Vector3f p2 = new Vector3f(4.8f, 7.7f, 0.0f);
        Vector3f p3 = new Vector3f(2.1f, 5.1f, 0.0f);
        int imageWidth = 10;
        int imageHeight = 10;
        Vector4i boundingBox = ObjectModel.calcBoundingBox(p1, p2, p3, imageWidth, imageHeight);
        Vector4i expected = new Vector4i(1, 5, 5, 8);
        if (boundingBox.equals(expected)) {
            System.out.println("testCalcBoundingBox 1 passed.");
        } else {
            System.out.println("testCalcBoundingBox 1 failed. Result: " + boundingBox + ", Expected: " + expected);
        }
        
        p1 = new Vector3f(-1.5f, 6.3f, 0.0f);
        p2 = new Vector3f(4.8f, 11.7f, 0.0f);
        p3 = new Vector3f(2.1f, 5.1f, 0.0f);
        imageWidth = 10;
        imageHeight = 10;
        boundingBox = ObjectModel.calcBoundingBox(p1, p2, p3, imageWidth, imageHeight);
        expected = new Vector4i(0, 5, 5, 9);
        if (boundingBox.equals(expected)) {
            System.out.println("testCalcBoundingBox 2 passed.");
        } else {
            System.out.println("testCalcBoundingBox 2 failed. Result: " + boundingBox + ", Expected: " + expected);
        }
    }
}







