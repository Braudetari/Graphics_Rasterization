package app_interface;

import java.nio.IntBuffer;
import java.nio.file.Paths;

import org.joml.Vector3f;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import your_code.WorldModel;
import your_code.YourSelectionEnum;

public class Main extends Application {
	//Constants
	public final int IMAGE_WIDTH                                = InterfaceDefaultParams.IMAGE_WIDTH;
	public final int IMAGE_HEIGHT                               = InterfaceDefaultParams.IMAGE_HEIGHT;
	public final int INITIAL_CAMERA_DISTANCE_FROM_AXIS_CENTER = InterfaceDefaultParams.INITIAL_CAMERA_DISTANCE_FROM_AXIS_CENTER;
	public final float CAMERA_MAX_VERTICAL_ANGLE             = InterfaceDefaultParams.CAMERA_MAX_VERTICAL_ANGLE;

    // Unsaved parameters - can change during execution but are not saved between runs.
	//                      loaded with their default values
	private float cameraRadius           = InterfaceDefaultParams.cameraRadius;
	private Vector3f cameraPos            = InterfaceDefaultParams.cameraPos;
	private Vector3f cameraLookAtCenter   = InterfaceDefaultParams.cameraLookAtCenter; 
	private Vector3f cameraUp             = InterfaceDefaultParams.cameraUp;
	private float cameraAngleHorizontal  = InterfaceDefaultParams.cameraAngleHorizontal;
	private float cameraAngleVertical    = InterfaceDefaultParams.cameraAngleVertical;

	private float horizontalFOV          = InterfaceDefaultParams.horizontalFOV;

	private float modelScale             = InterfaceDefaultParams.modelScale;

	private float lighting_Diffuse       = InterfaceDefaultParams.lighting_Diffuse;
	private float lighting_Specular      = InterfaceDefaultParams.lighting_Specular;
	private float lighting_Ambient       = InterfaceDefaultParams.lighting_Ambient;
	private float lighting_sHininess     = InterfaceDefaultParams.lighting_sHininess;
	private Vector3f lightPosition        = InterfaceDefaultParams.lightPosition;

    // Saved parameters - these are managed by this class and persist between runs.
	private InterfaceSavedParams savedParams = new InterfaceSavedParams();

	// The 3D model class that handles rendering
	WorldModel worldModel = new WorldModel(IMAGE_WIDTH,IMAGE_HEIGHT);

    //User interface variables
    
    //Image variables
	private IntBuffer[] intBuffers = new IntBuffer[2];
	private IntBufferWrapper[] intBufferWrappers = new IntBufferWrapper[2]; 
	@SuppressWarnings("unchecked")
	private PixelBuffer<IntBuffer>[] pixelBuffers = (PixelBuffer<IntBuffer>[]) new PixelBuffer[2];
	
	private int frontBufferIndex = 0;
	private int backBufferIndex = 1;
    private ImageView imageView;
    private long imageLastTimesUpdated = 0;
    private boolean imageLoaded;
    
    //cursor
    private double lastCursorX, lastCursorY;
    
    //labels
    private Label labelInfo1;
    private Label labelInfo2;
    private Label labelInfo3;
    private Label labelInfo4;
    private StringProperty labelInfo1StringProperty = new SimpleStringProperty("starting...");
    private StringProperty labelInfo2StringProperty = new SimpleStringProperty("starting...");
    private StringProperty labelInfo3StringProperty = new SimpleStringProperty("starting...");
    private StringProperty labelInfo4StringProperty = new SimpleStringProperty("starting...");
    private long labelLastUpdateTime = 0;
    
    //comboboxes
    ComboBox<ProjectionTypeEnum> comboProjType;
    ComboBox<DisplayTypeEnum> comboDispType;
    ComboBox<String> comboDispNormals;
    ComboBox<String> comboAnimation;
    ComboBox<String> comboExercise;
    ComboBox<YourSelectionEnum> comboYourSelection;

    //animation timer
    AnimationTimer timer; 
    
    //stage
    Stage primaryStage;

    //measuring time
    TimeMeasurement timeMeasurementRendering = new TimeMeasurement(10);
    TimeMeasurement timeMeasurementDisplay = new TimeMeasurement(10);
    
//    private int crossX = IMAGE_WIDTH/2, crossY = IMAGE_HEIGHT/2;
    
    //Loading the model and creating the window method
	//////////////////////////////////////////////////
    
    public void start(Stage primaryStage) throws Exception {

    	// Loading the model and initialize display parameters
    	imageLoaded = worldModel.load(savedParams.getModelFileName());
    	setCameraLocation(cameraPos, cameraLookAtCenter, cameraUp, horizontalFOV);
    	setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
    	setRenderingType(savedParams.getProjectionType(), savedParams.getDisplayType(), savedParams.isDisplayNormals());
    	setTransformation(modelScale);
    	setExercise(savedParams.getExercise());

        
        // creating the application window layout
        //////////////////////////////////////////////////////
    	
        // Buttons Dropdown menus row
        Label labelOpen = new Label(" "); 
        Button buttonOpen = new Button("Open...");
        VBox vboxButtonOpen = new VBox(labelOpen, buttonOpen);

        Label labelSave = new Label(" "); 
        Button buttonSave = new Button("Save...");
        VBox vboxSave = new VBox(labelSave, buttonSave);

    	Label labelProjType = new Label("Projection Type:"); 
        comboProjType = new ComboBox<>(); 
        comboProjType.getItems().addAll(ProjectionTypeEnum.values()); 
        comboProjType.setValue(savedParams.getProjectionType()); 
        VBox vboxProj = new VBox(labelProjType, comboProjType);

        Label labelDispType = new Label("Display Type:"); 
        comboDispType = new ComboBox<>(); 
        comboDispType.getItems().addAll(DisplayTypeEnum.values()); 
        comboDispType.setValue(savedParams.getDisplayType()); 
        VBox vboxDisp = new VBox(labelDispType, comboDispType);

        Label labelDispNormals = new Label("Normals:"); 
        comboDispNormals = new ComboBox<>(); 
        comboDispNormals.getItems().addAll("True", "False"); 
        comboDispNormals.setValue(savedParams.isDisplayNormals() ? "True" : "False"); 
        VBox vboxNormals = new VBox(labelDispNormals, comboDispNormals);

        Label labelAnimationon = new Label("Animate:"); 
        comboAnimation = new ComboBox<>(); 
        comboAnimation.getItems().addAll("True", "False"); 
        comboAnimation.setValue("False"); 
        VBox vboxAnimate = new VBox(labelAnimationon, comboAnimation);
        
        HBox dropdownRow = new HBox(vboxButtonOpen, vboxSave, vboxProj, vboxDisp, vboxNormals, vboxAnimate);
       
        //Image
        intBuffers[0] = IntBuffer.allocate(IMAGE_WIDTH * IMAGE_HEIGHT);
        intBuffers[1] = IntBuffer.allocate(IMAGE_WIDTH * IMAGE_HEIGHT);
        intBufferWrappers[0] = new IntBufferWrapper(intBuffers[0], IMAGE_WIDTH, IMAGE_HEIGHT);
        intBufferWrappers[1] = new IntBufferWrapper(intBuffers[1], IMAGE_WIDTH, IMAGE_HEIGHT);
        pixelBuffers[0] = new PixelBuffer<>(IMAGE_WIDTH, IMAGE_HEIGHT, intBuffers[0], PixelFormat.getIntArgbPreInstance());
        pixelBuffers[1] = new PixelBuffer<>(IMAGE_WIDTH, IMAGE_HEIGHT, intBuffers[1], PixelFormat.getIntArgbPreInstance());
        imageView = new ImageView(new WritableImage(pixelBuffers[backBufferIndex])); 

    	//labels 
        labelInfo1 = new Label("starting...");
        labelInfo2 = new Label("starting...");
        labelInfo3 = new Label("starting...");
        labelInfo4 = new Label("starting...");
        // Bind labels to the properties
        labelInfo1.textProperty().bind(labelInfo1StringProperty);
        labelInfo2.textProperty().bind(labelInfo2StringProperty);
        labelInfo3.textProperty().bind(labelInfo3StringProperty);
        labelInfo4.textProperty().bind(labelInfo4StringProperty);

        //Exercise label and combobox
        Label labelExercise = new Label("Exercise: "); 
        comboExercise = new ComboBox<>();
        for(ExerciseEnum ex: ExerciseEnum.values())
        	comboExercise.getItems().add(ex.getDescription());
        //comboExercise.getItems().addAll(ExerciseEnum.values()); 
        comboExercise.setValue(savedParams.getExercise().getDescription()); 

        //Your selection label and combobox
        Label labelYourSelection = new Label("  Your selection: "); 
        comboYourSelection = new ComboBox<>();
        comboYourSelection.getItems().addAll(YourSelectionEnum.values());
//        for(YourSelectionEnum sel: YourSelectionEnum.values())
//        	comboYourSelection.getItems().add(sel);
        comboYourSelection.setValue(YourSelectionEnum.values()[0]); 
          
        HBox hboxExercise = new HBox(labelExercise, comboExercise, labelYourSelection, comboYourSelection);
        
        
        
        //combining button and combobox row with image and lables
        VBox vbox = new VBox(dropdownRow, imageView, labelInfo1, labelInfo2, labelInfo3, labelInfo4, hboxExercise);

        //creating scene setting the stage
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Rasterization App");


        // Add event listeners
        //////////////////////////////////////////////////////

        //buttons
        buttonOpen.setOnAction(this::handleOpenFile);
        buttonSave.setOnAction(this::handleSaveFile);

        //dropdowns
        comboProjType.setOnAction(this::handleProjectionTypeChange);
        comboDispType.setOnAction(this::handleDisplayTypeChange);
        comboDispNormals.setOnAction(this::handleDisplayNormalsChange);
        comboAnimation.setOnAction(this::handleAnimationChange);
        comboExercise.setOnAction(this::handleExerciseChange);
        comboYourSelection.setOnAction(this::handleYourSelectionChange);
        
        //keyboard
        scene.setOnKeyPressed(this::handleKeyPressed);
        
        //mouse
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseReleased(this::handleMouseReleases);
        scene.setOnMouseDragged(this::handleMouseDragged);
        scene.addEventFilter(ScrollEvent.SCROLL, this::handleMouseWheelScrolling);
        
        // Force rendering and label update after the stage is visible
        Platform.runLater(() -> {
            updateWindow();  
        });

		// Create animation timer
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				updateWindow();
			}
		};
        
        // Show the stage
        //scene.getRoot().requestFocus();
        primaryStage.setResizable(false);
    	primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
	    System.exit(0); // Ensure the JVM terminates
    }
   
    
    //Updating window method - 
    //   image and labels are updated when parameters change
	//////////////////////////////////////////////////
    
    private void updateWindow() {

    	//Update image
        if (System.nanoTime() - imageLastTimesUpdated > InterfaceDefaultParams.IMAGE_UPDATE_INTERVAL_IN_MS * 1_000_000) {

        	//Rendering
        	timeMeasurementRendering.start();

        	boolean displayingTextureForModelWithoutTexture = !worldModel.modelHasTexture() && (savedParams.getDisplayType()==DisplayTypeEnum.TEXTURE || savedParams.getDisplayType()==DisplayTypeEnum.TEXTURE_LIGHTING);
        		
    		if (imageLoaded && !displayingTextureForModelWithoutTexture) {
            	worldModel.render(intBufferWrappers[backBufferIndex]);
//    	        drawCross(intBufferWrappers[backBufferIndex]);
    		} else {
    			if(!imageLoaded) {
    				intBufferWrappers[backBufferIndex].fillImageWithColor(50f/255, 50f/255, 50f/255);
    				intBufferWrappers[backBufferIndex].writeText("Fail to load model file !", IMAGE_WIDTH/2-200, IMAGE_HEIGHT/2-20, 40, 1f, 0, 0);
    			} else if(displayingTextureForModelWithoutTexture) {
    				intBufferWrappers[backBufferIndex].fillImageWithColor(50f/255, 50f/255, 50f/255);
    				intBufferWrappers[backBufferIndex].writeText("Displaying texture for Model without texture !", IMAGE_WIDTH/2-280, IMAGE_HEIGHT/2-20, 28, 1f, 0, 0);
    			}
    		}        	
	        timeMeasurementRendering.stop();
	

	        //Swapping buffers and updating display
	        timeMeasurementDisplay.start();
	        // Swap buffers
	        int temp = frontBufferIndex;
	        frontBufferIndex = backBufferIndex;
	        backBufferIndex = temp;
	        //updating display
	    	pixelBuffers[frontBufferIndex].updateBuffer(b -> null);
	    	imageView.setImage(new WritableImage(pixelBuffers[frontBufferIndex]));
	    	timeMeasurementDisplay.stop();
	    	
	        imageLastTimesUpdated = System.nanoTime();
        }

        //Update labels
        if(System.nanoTime() - labelLastUpdateTime > InterfaceDefaultParams.LABELS_UPDATE_INTERVAL_IN_MS * 1_000_000) {
	    	labelInfo1StringProperty.set(desciptionString1());  // Updates automatically on JavaFX thread
	    	labelInfo2StringProperty.set(desciptionString2());
	    	labelInfo3StringProperty.set(desciptionString3());
	    	labelInfo4StringProperty.set(desciptionString4());
	    	labelLastUpdateTime = System.nanoTime(); 
		}
        
        if (comboAnimation.getValue().equals("False"))
        		imageView.requestFocus();
        
		if (!imageLoaded) 
			handleOpenFile();
    }    

	private String desciptionString1() {
		return String.format(
				"Camera angles:(%.1f,%.1f) position:(%.1f,%.1f,%.1f) cameraLookAtCenter:(%.1f,%.1f,%.1f) Scale: %.1f",
				cameraAngleHorizontal, cameraAngleVertical,
				cameraPos.x, cameraPos.y, cameraPos.z,
				cameraLookAtCenter.x, cameraLookAtCenter.y, cameraLookAtCenter.z,
				modelScale);
	}

	private String desciptionString2() {
		return String.format("Lighting reflection - Diffuse:%1.2f, Specular:%1.2f, Ambient:%1.2f, sHininess:%1.2f",
				lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess);
	}

	private String desciptionString3() {
		return String.format("Time of last %d in mili (last,mean,STD,max) Rendering - %1.2f, %1.2f, %1.2f, %1.2f, Display - %1.2f, %1.2f, %1.2f, %1.2f",
				timeMeasurementRendering.getN(),
				timeMeasurementRendering.getLastMeasurement(), timeMeasurementRendering.getMeanOfLastN(),
				timeMeasurementRendering.getStdOfLastN(), timeMeasurementRendering.getMaxOfLastN(),
				timeMeasurementDisplay.getLastMeasurement(), timeMeasurementDisplay.getMeanOfLastN(),
				timeMeasurementDisplay.getStdOfLastN(), timeMeasurementDisplay.getMaxOfLastN());
	}
    
	private String desciptionString4() {
		String str = savedParams.getModelFileName();
		str =  (str.length()>60) ? Paths.get(savedParams.getModelFileName()).getRoot() + " ... " + str.substring( str.length()-60) : str;
		if(!imageLoaded)
			str += " - loading failed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"; 
		return String.format("3D model: %s", str);
		
	}
    
//    private void drawCross(IntBufferWrapper intBufferWrapper) {
//        int width = IMAGE_WIDTH;
//        int height = IMAGE_HEIGHT;
//
//        int crossR = 10;
//        int minCrossX = Math.max(0, crossX - crossR);
//        int maxCrossX = Math.min(crossX + crossR, width);
//        for (int i = minCrossX; i < maxCrossX; i++)
//        	intBufferWrapper.setPixel(i, crossY, 255, 128, 128);
//        int minCrossY = Math.max(0, crossY - crossR);
//        int maxCrossY = Math.min(crossY + crossR, height);
//        for (int i = minCrossY; i < maxCrossY; i++)
//        	intBufferWrapper.setPixel(crossX, i, 255, 128, 128);
//    }

    
	// Mouse and keybouard handlers
	//////////////////////////////////////////////////

    // Keyboord
    private void handleKeyPressed(KeyEvent event) {
    	//For each key changing the state, updating the window acordingly and Consume the event to prevent further propagation 
        KeyCode keyCode = event.getCode();
        if (keyCode == KeyCode.UP) {
        	moveForwardOrBackward(true, true);
            updateWindow();
            event.consume();  
        } else if (keyCode == KeyCode.DOWN) {
        	moveForwardOrBackward(false, true);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.LEFT) {
        	turnRightOrLeft(false);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.RIGHT) {
        	turnRightOrLeft(true);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.ADD) {
            modelScale+=0.1;
            setTransformation(modelScale);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.SUBTRACT) {
            modelScale-=0.1;
            setTransformation(modelScale);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.D) {
            lighting_Diffuse+=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.C) {
            lighting_Diffuse-=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.S) {
            lighting_Specular+=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.X) {
            lighting_Specular-=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.A) {
            lighting_Ambient+=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.Z) {
            lighting_Ambient-=0.05;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.H) {
            lighting_sHininess+=1;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        } else if (keyCode == KeyCode.N) {
            lighting_sHininess-=1;
            setLightingParams(lighting_Diffuse, lighting_Specular, lighting_Ambient, lighting_sHininess, lightPosition);
            updateWindow();
            event.consume(); 
        }
    }

    // Mouse press
    private void handleMousePress(MouseEvent event) {
        lastCursorX = event.getX();
        lastCursorY = event.getY();
    }

    
    // Mouse releases
    private void handleMouseReleases(MouseEvent event) {
        updateWindow();
    }
    
    // Mouse move
    private void handleMouseDragged(MouseEvent event) {
        double curCursorX = event.getX();
        double curCursorY = event.getY();
        double deltaX = curCursorX - lastCursorX; 
        double deltaY = curCursorY - lastCursorY; 

        final float CORSER_MOVEMENT_DIVISION_CONSTANT = 5;
        cameraAngleHorizontal += deltaX/CORSER_MOVEMENT_DIVISION_CONSTANT;
        cameraAngleVertical   -= deltaY/CORSER_MOVEMENT_DIVISION_CONSTANT;
        calcCameraPos();
        setCameraLocation(cameraPos, cameraLookAtCenter, cameraUp, horizontalFOV);
        
//        crossX += deltaX;
//        crossY += deltaY;
//        crossX = Math.max(0,Math.min(crossX, IMAGE_WIDTH-1));
//        crossY = Math.max(0,Math.min(crossY, IMAGE_HEIGHT-1));
        lastCursorX = curCursorX; 
        lastCursorY = curCursorY;
        
        updateWindow();
    }

    // Mouse move
    private void handleMouseWheelScrolling(ScrollEvent event) {
        // Get the scroll amount, this gives the "delta" or how much the wheel moved
        double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            //Mouse wheel scrolled UP
        	moveForwardOrBackward(true, false);
        } else {
            //Mouse wheel scrolled DOWN.
        	moveForwardOrBackward(false, false);
        }

        updateWindow();
        event.consume();
    }
    
    
	private void calcCameraPos() {
		Vector3f cameraDirection = new Vector3f(cameraLookAtCenter).sub(cameraPos);
		cameraRadius = cameraDirection.length(); 
		
		if (cameraAngleVertical > CAMERA_MAX_VERTICAL_ANGLE)
			cameraAngleVertical = CAMERA_MAX_VERTICAL_ANGLE;
		if (cameraAngleVertical < -CAMERA_MAX_VERTICAL_ANGLE)
			cameraAngleVertical = -CAMERA_MAX_VERTICAL_ANGLE;

		cameraAngleHorizontal = (cameraAngleHorizontal + 360) % 360;
		
		cameraPos.x =   (float) ( cameraRadius * Math.cos( (cameraAngleVertical/ 180 * Math.PI) ) * Math.cos(cameraAngleHorizontal/ 180 * Math.PI));
		cameraPos.z = - (float) ( cameraRadius * Math.cos( (cameraAngleVertical/ 180 * Math.PI) ) * Math.sin(cameraAngleHorizontal/ 180 * Math.PI));
		cameraPos.y =   (float) ( cameraRadius * Math.sin( (cameraAngleVertical/ 180 * Math.PI) )  );
	}

	private void moveForwardOrBackward(boolean forward, boolean moveAlsoCameraLookAtCenter) {
		Vector3f cameraDirection = new Vector3f(cameraLookAtCenter).sub(cameraPos);
		cameraDirection.mul(1f/50);
		if(!forward)
			cameraDirection.mul(-1);
		cameraPos.add(cameraDirection);
		if(moveAlsoCameraLookAtCenter)
			cameraLookAtCenter.add(cameraDirection);
	}

	private void turnRightOrLeft(boolean right) {
		Vector3f cameraDirection = new Vector3f(cameraLookAtCenter).sub(cameraPos);
		Vector3f cameraMovingDirection = new Vector3f(cameraDirection).cross(cameraUp);
		cameraMovingDirection.mul(1f/50);
		if(!right)
			cameraMovingDirection.mul(-1);
		cameraLookAtCenter.add(cameraMovingDirection);
	}
	

	
	
	// ComboBox handlers
	//////////////////////////////////////////////////
    
    private void handleProjectionTypeChange(ActionEvent e) {
        savedParams.setProjectionType(comboProjType.getValue());
        setRenderingType(savedParams.getProjectionType(), savedParams.getDisplayType(), savedParams.isDisplayNormals());
        updateWindow();
    }

    private void handleDisplayTypeChange(ActionEvent e) {
        savedParams.setDisplayType(comboDispType.getValue()); 
        setRenderingType(savedParams.getProjectionType(), savedParams.getDisplayType(), savedParams.isDisplayNormals());
        updateWindow();
    }

    private void handleDisplayNormalsChange(ActionEvent e) {
        savedParams.setDisplayNormals(Boolean.parseBoolean(comboDispNormals.getValue())); // Assuming setter exists in params
        setRenderingType(savedParams.getProjectionType(), savedParams.getDisplayType(), savedParams.isDisplayNormals());
        updateWindow();
    }

    private void handleAnimationChange(ActionEvent e) {
        if (comboAnimation.getValue().equals("True")) {
            timer.start();  
        } else {
            timer.stop();   
        }
    }

    private void handleExerciseChange(ActionEvent e) {
    	ExerciseEnum selectedExerciseEnum = ExerciseEnum.values()[comboExercise.getSelectionModel().getSelectedIndex()];
    	savedParams.setExercise(selectedExerciseEnum);
    	setExercise(selectedExerciseEnum);
        updateWindow();
    }
    
    private void handleYourSelectionChange(ActionEvent e) {
    	YourSelectionEnum yourSelection = comboYourSelection.getValue(); 
    	setYourSelection(yourSelection);
        updateWindow();
    }

    // Buttons handlers
	//////////////////////////////////////////////////
    
    // Open file
    private void handleOpenFile(ActionEvent event) {
        handleOpenFile();
    }
    private void handleOpenFile() {
        String filePath = Utilities.openFileChooser(primaryStage, "obj", Paths.get(savedParams.getModelFileName()).getParent().toString());
        if (filePath != null) {
        	imageLoaded = worldModel.load(filePath);
            savedParams.setModelFileName(filePath);
        }
        updateWindow();
   }

    // Save file
    private void handleSaveFile(ActionEvent event) {
        String filePath = Utilities.saveFileChooser(primaryStage, "bmp", savedParams.getSaveImagePath());
        if (filePath != null) {
        	intBufferWrappers[frontBufferIndex].saveToBMP(filePath);
            savedParams.setSaveImagePath(Paths.get(filePath).getParent().toString());
        }
    }
    

    //methods for setting parameters of model rendering
	//////////////////////////////////////////////////

    public void setRenderingType(ProjectionTypeEnum projectionType, DisplayTypeEnum displayType,
			boolean displayNormals) {
		worldModel.projectionType = projectionType;
		worldModel.displayType = displayType;
		worldModel.displayNormals = displayNormals;
	}

	public void setCameraLocation(Vector3f cameraPos, Vector3f cameraLookAtCenter, Vector3f cameraUp,
			float horizontalFOV) {
		worldModel.cameraPos = cameraPos;
		worldModel.cameraLookAtCenter = cameraLookAtCenter;
		worldModel.cameraUp = cameraUp;
		worldModel.horizontalFOV = horizontalFOV;
	}

	public void setTransformation(float modelScale) {
		worldModel.modelScale = modelScale;
	}

	public void setLightingParams(float lighting_Diffuse, float lighting_Specular, float lighting_Ambient,
			float lighting_sHininess, Vector3f lightPosition) {
		worldModel.lighting_Diffuse = lighting_Diffuse;
		worldModel.lighting_Specular = lighting_Specular;
		worldModel.lighting_Ambient = lighting_Ambient;
		worldModel.lighting_sHininess = lighting_sHininess;
		worldModel.lightPositionWorldCoordinates = lightPosition;
	}
	
	public void setExercise(ExerciseEnum exercise) {
		worldModel.exercise = exercise;
	}
	
	public void setYourSelection(YourSelectionEnum sel) {
		worldModel.yourSelection = sel;
	}
}
