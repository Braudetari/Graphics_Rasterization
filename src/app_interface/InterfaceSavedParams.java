package app_interface;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


//Saved parameters -   parameters that can be changed, saved, and loaded from a file between runs.
public class InterfaceSavedParams {
	private String modelFileName;
	private String saveImagePath;
	private ProjectionTypeEnum projectionType;
	private DisplayTypeEnum displayType;
	private boolean displayNormals;
	private ExerciseEnum exercise;


	public void setDefaultParams() {
		//2. Saved parameters -   parameters that can be changed, saved, and loaded from a file between runs.
		modelFileName = InterfaceDefaultParams.modelFileName;
		saveImagePath = InterfaceDefaultParams.saveImagePath;
		projectionType = InterfaceDefaultParams.projectionType;
		displayType = InterfaceDefaultParams.displayType;
		displayNormals = InterfaceDefaultParams.displayNormals;
		exercise = InterfaceDefaultParams.exNum;
	}

	public InterfaceSavedParams() {
		setDefaultParams();
		try {
			loadFromFile();
		} catch (Exception e) {
			System.err.println("parameter file is missing creating default parameter file.");
			setDefaultParams();
			try {
				saveToFile();
			} catch (Exception e1) {
				System.err.println("could not save and load parameters from file.");
			}
		}
	}
	
	public String getModelFileName() {
		return modelFileName;
	}

	public void setModelFileName(String modelFileName) {
		this.modelFileName = modelFileName;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}

	public String getSaveImagePath() {
		return saveImagePath;
	}

	public void setSaveImagePath(String saveImagePath) {
		this.saveImagePath = saveImagePath;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}
	
	public ProjectionTypeEnum getProjectionType() {
		return projectionType;
	}

	public void setProjectionType(ProjectionTypeEnum projectionType) {
		this.projectionType = projectionType;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}

	public DisplayTypeEnum getDisplayType() {
		return displayType;
	}

	public void setDisplayType(DisplayTypeEnum displayType) {
		this.displayType = displayType;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}

	public boolean isDisplayNormals() {
		return displayNormals;
	}

	public void setDisplayNormals(boolean displayNormals) {
		this.displayNormals = displayNormals;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}

	public void setExercise(ExerciseEnum exercise) {
		this.exercise = exercise;
		try {
			saveToFile();
		} catch (Exception e) {
			System.out.println("could not save parameters to file.");
		}
	}

	public ExerciseEnum getExercise() {
		return exercise;
	}
	
	private <T> void addElementToFile(Element rootElement, Document doc, String parameterName, T val) {
		Element paramElement = doc.createElement(parameterName);
		paramElement.setTextContent(String.valueOf(val));
		rootElement.appendChild(paramElement);
	}

	// Method to save parameters to XML file
	public void saveToFile() throws Exception {
		String filePath = System.getProperty("user.dir") + File.separator + "parameters.xml";

		// Create a new document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		// Create root element
		Element rootElement = doc.createElement("parameters");
		doc.appendChild(rootElement);

		// Add elements for each parameter
		addElementToFile(rootElement, doc, "modelFileName", modelFileName);
		addElementToFile(rootElement, doc, "saveImagePath", saveImagePath);
		addElementToFile(rootElement, doc, "projectionType", projectionType);
		addElementToFile(rootElement, doc, "displayType", displayType);
		addElementToFile(rootElement, doc, "displayNormals", displayNormals);
		addElementToFile(rootElement, doc, "exercise", exercise);

		// Write document to XML file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filePath));
		transformer.transform(source, result);
	}

	private static int parseParamInt(Element rootElement, String parameterName) {
		return Integer.parseInt(rootElement.getElementsByTagName(parameterName).item(0).getTextContent());
	}

	private static <T extends Enum<T>> T parseParamEnum(Element rootElement, String parameterName, Class<T> enumType) {
		return Enum.valueOf(enumType, rootElement.getElementsByTagName(parameterName).item(0).getTextContent());
	}

	private static boolean parseParamBoolean(Element rootElement, String parameterName) {
		return Boolean.parseBoolean(rootElement.getElementsByTagName(parameterName).item(0).getTextContent());
	}

	private static String parseParamString(Element rootElement, String parameterName) {
		return rootElement.getElementsByTagName(parameterName).item(0).getTextContent();
	}

	private static double parseParamDouble(Element rootElement, String parameterName) {
		return Double.parseDouble(rootElement.getElementsByTagName(parameterName).item(0).getTextContent());
	}

	// Method to load parameters from XML file
	private void loadFromFile() throws Exception {
		String filePath = System.getProperty("user.dir") + File.separator + "parameters.xml";
		File file = new File(filePath);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();

		Element rootElement = doc.getDocumentElement();
		modelFileName = parseParamString(rootElement, "modelFileName");
		saveImagePath = parseParamString(rootElement, "saveImagePath");
		projectionType = parseParamEnum(rootElement, "projectionType", ProjectionTypeEnum.class);
		displayType = parseParamEnum(rootElement, "displayType", DisplayTypeEnum.class);
		displayNormals = parseParamBoolean(rootElement, "displayNormals");
		exercise = parseParamEnum(rootElement, "exercise", ExerciseEnum.class);
	}



	@Override
	public String toString() {
		return "AppParams [modelFileName=" + modelFileName + ", projectionType=" + projectionType + ", displayType="
				+ displayType + ", displayNormals=" + displayNormals + ", exercise = "+ exercise + "]";
	}


	public static void main(String[] args) {
    	InterfaceSavedParams p1 = new InterfaceSavedParams();
    	p1.setModelFileName("./models/pumpkin.obj");
    	p1.setProjectionType(ProjectionTypeEnum.PERSPECTIVE);
    	p1.setDisplayType(DisplayTypeEnum.FACE_COLOR);
    	p1.setDisplayNormals(false);
    	p1.setExercise(ExerciseEnum.EX_0___Starting_point);
    	try {
			p1.saveToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(p1);

		InterfaceSavedParams p2 = new InterfaceSavedParams();
		try {
			p2.loadFromFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(p2);
	}
}

