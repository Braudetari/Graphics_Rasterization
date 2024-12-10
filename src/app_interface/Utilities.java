package app_interface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.IntBuffer;

import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Utilities {
    public static String openFileChooser(Stage stage, String fileExtension, String initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        
        // Set initial directory (optional)
        File initialDirFile = new File(initialDirectory);
        if (initialDirFile.exists() && initialDirFile.isDirectory()) 
        	fileChooser.setInitialDirectory(initialDirFile);
        else
        	fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // Set file extension filters (optional)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Files", "*." + fileExtension)
//                new FileChooser.ExtensionFilter("All Files", "*.*"),
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
//                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        // Open the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }	
    
    public static String saveFileChooser(Stage stage, String fileExtension, String initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        
        // Set initial directory (optional)
        File initialDirFile = new File(initialDirectory);
        if (initialDirFile.exists() && initialDirFile.isDirectory()) 
        	fileChooser.setInitialDirectory(initialDirFile);
        else
        	fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // Set file extension filter
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Files", "*." + fileExtension)
        );
        
        // Set initial file name (optional)
        fileChooser.setInitialFileName("untitled." + fileExtension);
        
        // Open the file chooser dialog
        File selectedFile = fileChooser.showSaveDialog(stage);
        
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }   
   
    public static void saveIntBufferAsBMP(IntBuffer buffer, int width, int height, String filePath) {
        File file = new File(filePath);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // BMP Header
            fos.write(new byte[]{0x42, 0x4D}); // Signature 'BM'
            fos.write(intToBytes(54 + width * height * 3)); // File size in bytes
            fos.write(new byte[]{0, 0, 0, 0}); // Reserved
            fos.write(intToBytes(54)); // Data offset

            // DIB Header
            fos.write(intToBytes(40)); // Header size
            fos.write(intToBytes(width)); // Width
            fos.write(intToBytes(height)); // Height
            fos.write(new byte[]{1, 0}); // Planes
            fos.write(new byte[]{24, 0}); // Bits per pixel
            fos.write(new byte[]{0, 0, 0, 0}); // Compression (none)
            fos.write(intToBytes(width * height * 3)); // Image size (no compression)
            fos.write(new byte[]{0, 0, 0, 0}); // X pixels per meter (not specified)
            fos.write(new byte[]{0, 0, 0, 0}); // Y pixels per meter (not specified)
            fos.write(new byte[]{0, 0, 0, 0}); // Colors in color table (none)
            fos.write(new byte[]{0, 0, 0, 0}); // Important color count (all)

            // Pixel Data
            for (int y = height - 1; y >= 0; y--) { // BMP files are bottom to top
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    int argb = buffer.get(index);
                    int blue = argb & 0xFF;
                    int green = (argb >> 8) & 0xFF;
                    int red = (argb >> 16) & 0xFF;
                    fos.write(new byte[]{(byte) blue, (byte) green, (byte) red});
                }
            }

            System.out.println("Image successfully written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred while writing the image: " + e.getMessage());
        }
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }
    
    public static void saveIntBufferAsCSV(IntBuffer buffer, int width, int height, String filePath) {
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    int argb = buffer.get(index);

                    // Extract the RGB components
                    int blue = argb & 0xFF;
                    int green = (argb >> 8) & 0xFF;
                    int red = (argb >> 16) & 0xFF;

                    // Write the R-G-B value as "red-green-blue"
                    writer.write(String.format("%03d-%03d-%03d", red, green, blue));

                    // Add a comma between columns, but not after the last column in a row
                    if (x < width - 1) {
                        writer.write(",");
                    }
                }

                // Add a new line after each row
                writer.write("\n");
            }

            System.out.println("CSV file successfully written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred while writing the CSV file: " + e.getMessage());
        }
    }
    
}
