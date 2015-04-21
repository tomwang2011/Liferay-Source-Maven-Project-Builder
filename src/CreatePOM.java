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

public class CreatePOM {

	public static void createArtifactElements(Document document, Element projectElement, String[] args) throws Exception {
		Element modelVersionElement = document.createElement("modelVersion");

		modelVersionElement.appendChild(document.createTextNode("4.0.0"));

		projectElement.appendChild(modelVersionElement);

		Element portalSourceDirElement
			= document.createElement("sourceDirectory");

		if (_artifactId.equals("portal")) {
			Element groupId = document.createElement("groupId");

			groupId.appendChild(document.createTextNode(_groupId));

			projectElement.appendChild(groupId);

			portalSourceDirElement.appendChild(document.createTextNode(_fullPath));
		}
		else {
			createParentElement(document, projectElement, portalSourceDirElement);
		}

		Element artifactIdElement = document.createElement("artifactId");

		artifactIdElement.appendChild(document.createTextNode(_artifactId));

		projectElement.appendChild(artifactIdElement);

		Element versionElement = document.createElement("version");

		versionElement.appendChild(document.createTextNode(_version));

		projectElement.appendChild(versionElement);

		Element packagingElement = document.createElement("packaging");

		packagingElement.appendChild(document.createTextNode(_packaging));

		projectElement.appendChild(packagingElement);

		Element nameElement = document.createElement("name");

		nameElement.appendChild(document.createTextNode(_name));

		projectElement.appendChild(nameElement);

		if (_artifactId.equals("portal")) {
			createPortalPOM(args, projectElement, portalSourceDirElement, document);
		}
		else {
			createModulePOM(args, projectElement, portalSourceDirElement, document);
		}
	}

	public static void createBuildElement(Document document, Element portalSourceDirElement, Element projectElement) {
		Element buildElement = document.createElement("build");

		projectElement.appendChild(buildElement);

		buildElement.appendChild(portalSourceDirElement);

		if(_artifactId.endsWith("-test")) {
			Element testSourceDirElement = document.createElement("testSourceDirectory");

			String path = _fullPath.substring(_portalDir.length());

			path = path.substring(0, path.length()-3)+"test";

			testSourceDirElement.appendChild(document.createTextNode("${sourceDirectory}" + path));

			buildElement.appendChild(testSourceDirElement);
		}
	}

	public static void createDependenciesElement(Document document, Element projectElement, int i, String[] args) {
		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		for (int j = i; j < args.length; j++) {
			createDependencyElement(document, dependenciesElement, j, args);
		}
	}

	public static void createDependencyElement(Document document, Element dependenciesElement, int j, String[] args) {
		Element dependencyElement = document.createElement("dependency");
		Element dependencyGroupIdElement = document.createElement("groupId");

		dependencyGroupIdElement.appendChild(document.createTextNode(_groupId));

		dependencyElement.appendChild(dependencyGroupIdElement);

		Element dependencyArtifactIdElement = document.createElement("artifactId");

		String[] artifactIdToken = args[j].split("/");

		dependencyArtifactIdElement.appendChild(document.createTextNode(
			artifactIdToken[artifactIdToken.length - 1]));

		dependencyElement.appendChild(dependencyArtifactIdElement);

		Element dependencyVersionElement = document.createElement("version");

		dependencyVersionElement.appendChild(document.createTextNode(_version));

		dependencyElement.appendChild(dependencyVersionElement);

		if(artifactIdToken[artifactIdToken.length - 1].endsWith("jar")) {
			Element dependencyScopeElement = document.createElement("scope");

			dependencyScopeElement.appendChild(document.createTextNode("system"));

			dependencyElement.appendChild(dependencyScopeElement);

			Element dependencySystemPathElement = document.createElement("systemPath");

			dependencySystemPathElement.appendChild(document.createTextNode(args[j]));

			dependencyElement.appendChild(dependencySystemPathElement);
		}

		dependenciesElement.appendChild(dependencyElement);
	}

	public static void createModulePOM(
		String[] args, Element projectElement, Element portalSourceDirElement, Document document) {

		createBuildElement(document, portalSourceDirElement, projectElement);

		createDependenciesElement(document, projectElement, 7, args);
	}

	public static int createModulesElement(Document document, Element projectElement, int i, String[] args) {
		Element modulesElement = document.createElement("modules");

		projectElement.appendChild(modulesElement);

		while (!args[i].substring(0, 1).equals("/")) {
			Element moduleElement = document.createElement("module");

			moduleElement.appendChild(document.createTextNode(args[i]));

			modulesElement.appendChild(moduleElement);

			i++;
		}

		return (i);
	}

	public static void createParentElement(Document document, Element projectElement, Element portalSourceDirElement) {
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

		String path = _fullPath.substring(_portalDir.length());

		portalSourceDirElement.appendChild(
			document.createTextNode("${sourceDirectory}" + path));
	}

	public static void createPortalPOM(
		String[] args, Element projectElement, Element portalSourceDirElement, Document document)
		throws Exception {

		createPropertiesElement(document, portalSourceDirElement, projectElement);

		int i = createModulesElement(document, projectElement, 6, args);

		createDependenciesElement(document, projectElement, i, args);

		createRepositoriesElement(document, projectElement);
	}

	public static void createProjectElement(Document document, String[] args) throws Exception {
		Element projectElement = document.createElement("project");

		document.appendChild(projectElement);

		projectElement.setAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
		projectElement.setAttribute(
			"xmlns:xsi",
			"http://www.w3.org/2001/XMLSchema-instance");
		projectElement.setAttribute(
			"xsi:schemaLocation",
			"http://maven.apache.org/POM/4.0.0 "
			+ "http://maven.apache.org/maven-v4_0_0.xsd");

		createArtifactElements(document, projectElement, args);
	}

	public static void createPropertiesElement(Document document, Element portalSourceDirElement, Element projectElement) {
		Element propertiesElement = document.createElement("properties");

		projectElement.appendChild(propertiesElement);

		propertiesElement.appendChild(portalSourceDirElement);

		Element compilerSourceElement = document.createElement("maven.compiler.source");

		compilerSourceElement.appendChild(document.createTextNode("1.7"));

		propertiesElement.appendChild(compilerSourceElement);

		Element compilerTargetElement = document.createElement("maven.compiler.target");

		compilerTargetElement.appendChild(document.createTextNode("1.7"));

		propertiesElement.appendChild(compilerTargetElement);
	}

	public static void createRepositoriesElement(Document document, Element projectElement) throws Exception {
		Element repositoriesElement = document.createElement("repositories");

		projectElement.appendChild(repositoriesElement);

		createRepositoryElement(repositoriesElement, document, "com.liferay.liferay-ce",
			"https://repository.liferay.com/nexus/content/groups/liferay-ce/");

		createRepositoryElement(repositoriesElement, document, "public",
			"https://repository.liferay.com/nexus/content/groups/public/");
	}

	public static void createRepositoryElement(
		Element repositoriesElement, Document document, String repoId, String repoUrl)
		throws Exception {

		Element repositoryElement = document.createElement("repository");

		repositoriesElement.appendChild(repositoryElement);

		Element repositoryIdElement = document.createElement("id");

		repositoryElement.appendChild(repositoryIdElement);

		repositoryIdElement.appendChild(
			document.createTextNode(repoId));

		Element repositoryURLElement = document.createElement("url");

		repositoryElement.appendChild(repositoryURLElement);

		repositoryURLElement.appendChild(document.createTextNode(
			repoUrl));
	}

	public static void main(String[] args) throws Exception {
		parseArgument(args);

		DocumentBuilderFactory documentBuilderFactory
			= DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder
			= documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();

		createProjectElement(document, args);

		TransformerFactory transformerFactory
			= TransformerFactory.newInstance();

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
			setGroupId(args[0]);

			setArtifactId(args[1]);

			setVersion(args[2]);

			setPackaging(args[3]);

			setName(args[4]);

			setFullPath(args[5]);

			setPortalDir(args[6]);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
				"Insufficient number of inputs, please use the following order "
				+ "of inputs: GroupId, ArtifactId, Version, Packaging, Name, "
				+ "FullPath-to-module, Portal-path, Modules, Dependencies");

			System.exit(1);
		}
	}

	public static void setArtifactId(String arg) {
		_artifactId = arg;
	}

	public static void setFullPath(String arg) {
		_fullPath = arg;
	}

	public static void setGroupId(String arg) {
		_groupId = arg;
	}

	public static void setName(String arg) {
		_name = arg;
	}

	public static void setPackaging(String arg) {
		_packaging = arg;
	}

	public static void setPortalDir(String arg) {
		_portalDir = arg;
	}

	public static void setVersion(String arg) {
		_version = arg;
	}

	private static String _groupId;
	private static String _artifactId;
	private static String _version;
	private static String _packaging;
	private static String _name;
	private static String _fullPath;
	private static String _portalDir;

}
