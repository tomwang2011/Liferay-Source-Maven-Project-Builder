* C source, ASCII text
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

public class CreatePortalPOM {

	public static void createArtifactElements(Element projectElement)
		throws Exception {

		Element modelVersionElement = document.createElement("modelVersion");

		modelVersionElement.appendChild(document.createTextNode("4.0.0"));

		projectElement.appendChild(modelVersionElement);

		Element groupId = document.createElement("groupId");

		groupId.appendChild(document.createTextNode(_groupId));

		projectElement.appendChild(groupId);

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

		createPortalPOM(projectElement);
	}

	public static void createDependenciesElement(
		Element projectElement, int offset) {

		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		for (int i = offset; i < _tokens.length; i++) {
			createDependencyElement(dependenciesElement, _tokens[i]);
		}
	}

	public static void createDependencyElement(
		Element dependenciesElement, String dependencyToken) {

		String[] artifactIdToken = dependencyToken.split("/");

		Element dependencyElement = document.createElement("dependency");

		Element dependencyGroupIdElement = document.createElement("groupId");

		dependencyGroupIdElement.appendChild(document.createTextNode(_groupId));

		dependencyElement.appendChild(dependencyGroupIdElement);

		Element dependencyArtifactIdElement = document.createElement(
			"artifactId");

		dependencyArtifactIdElement.appendChild(
			document.createTextNode(
				artifactIdToken[artifactIdToken.length - 1]));

		dependencyElement.appendChild(dependencyArtifactIdElement);

		Element dependencyVersionElement = document.createElement("version");

		dependencyVersionElement.appendChild(document.createTextNode(_version));

		dependencyElement.appendChild(dependencyVersionElement);

		Element dependencyScopeElement = document.createElement("scope");

		dependencyScopeElement.appendChild(document.createTextNode("system"));

		dependencyElement.appendChild(dependencyScopeElement);

		Element dependencySystemPathElement = document.createElement(
			"systemPath");

		dependencySystemPathElement.appendChild(
			document.createTextNode(dependencyToken));

		dependencyElement.appendChild(dependencySystemPathElement);

		dependenciesElement.appendChild(dependencyElement);
	}

	public static int createModulesElement(Element projectElement) {
		Element modulesElement = document.createElement("modules");

		projectElement.appendChild(modulesElement);

		int i = 0;

		while (!_tokens[i].substring(0, 1).equals("/")) {
			Element moduleElement = document.createElement("module");

			moduleElement.appendChild(document.createTextNode(_tokens[i]));

			modulesElement.appendChild(moduleElement);

			i++;
		}

		return (i);
	}

	public static void createPortalPOM(Element projectElement)
		throws Exception {

		createPropertiesElement(projectElement);

		int offset = createModulesElement(projectElement);

		createDependenciesElement(projectElement, offset);

		createRepositoriesElement(projectElement);
	}

	public static void createProjectElement() throws Exception {
		Element projectElement = document.createElement("project");

		document.appendChild(projectElement);

		projectElement.setAttribute(
			"xmlns", "http://maven.apache.org/POM/4.0.0");
		projectElement.setAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		projectElement.setAttribute(
			"xsi:schemaLocation",
			"http://maven.apache.org/POM/4.0.0 " +
			"http://maven.apache.org/maven-v4_0_0.xsd");

		createArtifactElements(projectElement);
	}

	public static void createPropertiesElement(Element projectElement) {
		Element propertiesElement = document.createElement("properties");

		projectElement.appendChild(propertiesElement);

		Element portalSourceDirElement = document.createElement(
			"sourceDirectory");

		portalSourceDirElement.appendChild(document.createTextNode(_fullPath));

		propertiesElement.appendChild(portalSourceDirElement);

		Element compilerSourceElement = document.createElement(
			"maven.compiler.source");

		compilerSourceElement.appendChild(document.createTextNode("1.7"));

		propertiesElement.appendChild(compilerSourceElement);

		Element compilerTargetElement = document.createElement(
			"maven.compiler.target");

		compilerTargetElement.appendChild(document.createTextNode("1.7"));

		propertiesElement.appendChild(compilerTargetElement);
	}

	public static void createRepositoriesElement(Element projectElement)
		throws Exception {

		Element repositoriesElement = document.createElement("repositories");

		projectElement.appendChild(repositoriesElement);

		createRepositoryElement(
			repositoriesElement, "com.liferay.liferay-ce",
			"https://repository.liferay.com/nexus/content/groups/liferay-ce/");

		createRepositoryElement(
			repositoriesElement, "public",
			"https://repository.liferay.com/nexus/content/groups/public/");

		createRepositoryElement(
			repositoriesElement, "spring-releases",
			"http://repo.spring.io/libs-release-remote/");
	}

	public static void createRepositoryElement(
			Element repositoriesElement, String repoId, String repoUrl)
		throws Exception {

		Element repositoryElement = document.createElement("repository");

		repositoriesElement.appendChild(repositoryElement);

		Element repositoryIdElement = document.createElement("id");

		repositoryElement.appendChild(repositoryIdElement);

		repositoryIdElement.appendChild(document.createTextNode(repoId));

		Element repositoryURLElement = document.createElement("url");

		repositoryElement.appendChild(repositoryURLElement);

		repositoryURLElement.appendChild(document.createTextNode(repoUrl));
	}

	public static void main(String[] args) throws Exception {
		parseArgument(args);

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

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

			_fullPath = args[5];

			_tokens = Arrays.copyOfRange(args, 6, args.length);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
				"Insufficient number of inputs, please use the following " +
				"order of inputs: GroupId, ArtifactId, Version, Packaging, " +
				"Name, FullPath-to-module, Modules, Dependencies");

			System.exit(1);
		}
	}

	private static String _artifactId;
	private static String _fullPath;
	private static String _groupId;
	private static String _name;
	private static String _packaging;
	private static String[] _tokens;
	private static String _version;
	private static Document document;

}