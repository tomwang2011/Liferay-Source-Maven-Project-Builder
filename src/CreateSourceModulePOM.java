
import java.io.File;

import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CreateSourceModulePOM {

	public static void createDependenciesElement(Element projectElement) {
		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		for (String _token : _tokens) {
			createDependencyElement(dependenciesElement, _token);
		}
	}

	public static void createDependencyElement(
		Element dependenciesElement, String dependencyToken) {

		Element dependencyElement = document.createElement("dependency");

		dependenciesElement.appendChild(dependencyElement);

		Element groupIdElement = document.createElement("groupId");

		groupIdElement.appendChild(document.createTextNode(_groupId));

		dependencyElement.appendChild(groupIdElement);

		Element artifactIdElement = document.createElement("artifactId");

		artifactIdElement.appendChild(document.createTextNode(dependencyToken));

		dependencyElement.appendChild(artifactIdElement);

		Element versionElement = document.createElement("version");

		versionElement.appendChild(document.createTextNode(_version));

		dependencyElement.appendChild(versionElement);
	}

	public static void createModulePOM(
		Element projectElement, Element portalSourceDirElement) {

		createDependenciesElement(projectElement);
	}

	public static void createParentElement(
		Element projectElement, Element portalSourceDirElement) {

		Element parent = document.createElement("parent");

		projectElement.appendChild(parent);

		Element groupIdElement = document.createElement("groupId");

		groupIdElement.appendChild(document.createTextNode(_groupId));

		parent.appendChild(groupIdElement);

		Element parentArtifactIdElement = document.createElement("artifactId");

		parentArtifactIdElement.appendChild(document.createTextNode("portal"));

		parent.appendChild(parentArtifactIdElement);

		Element parentVersionElement = document.createElement("version");

		parentVersionElement.appendChild(document.createTextNode(_version));

		parent.appendChild(parentVersionElement);
	}

	public static void createProjectElement() throws Exception {
		Element projectElement = CreatePOM.createProjectElement(
			document, _artifactId, _groupId, _name, _packaging, _version);

		Element portalSourceDirElement = document.createElement(
			"sourceDirectory");

		createParentElement(projectElement, portalSourceDirElement);

		createModulePOM(projectElement, portalSourceDirElement);
	}

	public static void main(String[] args) throws Exception {
		parseArgument(args);

		documentBuilderFactory = DocumentBuilderFactory.newInstance();

		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		document = documentBuilder.newDocument();

		createProjectElement();

		TransformerFactory transformerFactory =
			TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		DOMSource source = new DOMSource(document);

		StreamResult streamResult;

		if (_artifactId.equals("portal")) {
			streamResult = new StreamResult(new File(_artifactId + "/pom.xml"));
		}
		else {
			streamResult = new StreamResult(
				new File("portal/" + _artifactId + "/pom.xml"));
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
			"{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, streamResult);
	}

	public static void parseArgument(String[] args) {
		try {
			_groupId = args[0];

			_artifactId = args[1];

			_version = args[2];

			_packaging = args[3];

			_name = args[4];

			_tokens = Arrays.copyOfRange(args, 5, args.length);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
				"Insufficient number of inputs, please use the following" +
				" order of inputs: GroupId, ArtifactId, Version, Packaging, " +
				"Name, Modules");

			System.exit(1);
		}
	}

	private static String _artifactId;
	private static String _groupId;
	private static String _name;
	private static String _packaging;
	private static String[] _tokens;
	private static String _version;
	private static Document document;
	private static DocumentBuilder documentBuilder;
	private static DocumentBuilderFactory documentBuilderFactory;
}