package your_code;

import org.joml.Matrix4f;

import app_interface.IntBufferWrapper;
import app_interface.Utilities;

public class YoursUtilities {
	public static Matrix4f createViewportMatrix(float x, float y, float width, float height) {
	    Matrix4f viewportMatrix = new Matrix4f();

	    // Scale normalized device coordinates to window coordinates
	    viewportMatrix.m00(width / 2.0f);  // Scale X
	    viewportMatrix.m11(height / 2.0f); // Conditionally scale Y to flip if needed
	    viewportMatrix.m22(0.5f);  // Scale Z

	    // Translate the scaled coordinates to fit the viewport
	    viewportMatrix.m30(x + width / 2.0f);  // Translate X
	    viewportMatrix.m31(y + height / 2.0f); // Translate Y
	    viewportMatrix.m32(0.5f); // Translate Z

	    return viewportMatrix;
	}

    public static void saveFloatArrayAsBMP(float[][] array, String filename) {
        saveFloatArrayAsBMPorCSV(array, filename, true);
    }
    public static void saveFloatArrayAsCSV(float[][] array, String filename) {
        saveFloatArrayAsBMPorCSV(array, filename, false);
    }
    
    
       
    
    
    private static void saveFloatArrayAsBMPorCSV(float[][] array, String filename, boolean saveAsBmp) {
    	int imageWidth  = array[0].length;
    	int imageHeight = array.length;
    	IntBufferWrapper intBufferWrapper = new IntBufferWrapper(imageWidth, imageHeight);
    	for(int i=0; i<imageHeight; i++)
    		for(int j=0; j<imageWidth; j++) {
    			int c = (int)array[i][j]*255;
    			intBufferWrapper.setPixel(i, j, c, c, c);
    		}
    	if(saveAsBmp)
    		Utilities.saveIntBufferAsBMP(intBufferWrapper.getIntBuffer(), imageWidth, imageHeight, filename);
       	else
       	    Utilities.saveIntBufferAsCSV(intBufferWrapper.getIntBuffer(), imageWidth, imageHeight, filename);
    }
    
}


