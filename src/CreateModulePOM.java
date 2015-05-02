
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

public class CreateModulePOM {

	public static void createArtifactElements(
		Element projectElement) throws Exception {

		Element modelVersionElement = document.createElement("modelVersion");

		modelVersionElement.appendChild(document.createTextNode("4.0.0"));

		projectElement.appendChild(modelVersionElement);

		Element portalSourceDirElement = document.createElement(
			"sourceDirectory");

		createParentElement(projectElement, portalSourceDirElement);

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

		createModulePOM(projectElement, portalSourceDirElement);
	}

	public static void createBuildElement(
		Element portalSourceDirElement, Element projectElement) {

		Element buildElement = document.createElement("build");

		projectElement.appendChild(buildElement);

		buildElement.appendChild(portalSourceDirElement);

		if (_artifactId.endsWith("-test")) {
			Element testSourceDirElement = document.createElement(
				"testSourceDirectory");

			String path = _fullPath.substring(_portalPath.length());

			path = path.substring(0, path.length() - 3) + "test";

			testSourceDirElement.appendChild(document.createTextNode(
				"${sourceDirectory}" + path));

			buildElement.appendChild(testSourceDirElement);
		}
	}

	public static void createDependenciesElement(
		Element projectElement) {

		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		parseModuleBuildFile(dependenciesElement);

		parseIvyDependencies(dependenciesElement);

		for (int i = 0; i < _tokens.length; i++) {
			createDependencyElement(dependenciesElement, _tokens[i]);
		}
	}

	public static void createDependencyElement(
		Element dependenciesElement, String dependencyToken) {

		String[] dependencyTokens = dependencyToken.split(":");
		String[] artifactIdToken
			= dependencyTokens[dependencyTokens.length - 1].split("/");

		Element dependencyElement = document.createElement("dependency");
		Element dependencyGroupIdElement = document.createElement("groupId");

		if (artifactIdToken[0].equals("")) {
			dependencyGroupIdElement.appendChild(document.createTextNode(
				_groupId));
		}
		else {
			dependencyGroupIdElement.appendChild(document.createTextNode(
				dependencyTokens[0]));
		}

		dependencyElement.appendChild(dependencyGroupIdElement);

		Element dependencyArtifactIdElement = document.createElement(
			"artifactId");

		dependencyArtifactIdElement.appendChild(
			document.createTextNode(
				artifactIdToken[artifactIdToken.length - 1]));

		dependencyElement.appendChild(dependencyArtifactIdElement);

		Element dependencyVersionElement = document.createElement("version");

		if (artifactIdToken[0].equals("")) {
			dependencyVersionElement.appendChild(document.createTextNode(
				_version));
		}
		else {
			dependencyVersionElement.appendChild(document.createTextNode(
				dependencyTokens[1]));
		}

		dependencyElement.appendChild(dependencyVersionElement);

		if (dependencyTokens.length > 3) {
			Element dependencyScopeElement = document.createElement("scope");

			if (dependencyTokens[2].equals("master")) {
				dependencyScopeElement.appendChild(
					document.createTextNode("compile"));

				dependencyElement.appendChild(dependencyScopeElement);

				Element exclusionsElement =
					document.createElement("exclusions");

				dependencyElement.appendChild(exclusionsElement);

				Element exclusionElement = document.createElement("exclusion");

				exclusionsElement.appendChild(exclusionElement);

				Element exclusionGroupIdElement =
					document.createElement("groupId");

				exclusionElement.appendChild(exclusionGroupIdElement);

				exclusionGroupIdElement.appendChild(
					document.createTextNode("*"));

				Element exclusionArtifactIdElement =
					document.createElement("artifactId");

				exclusionElement.appendChild(exclusionArtifactIdElement);

				exclusionArtifactIdElement.appendChild(
					document.createTextNode("*"));
			}
			else {
				dependencyScopeElement.appendChild(
					document.createTextNode(dependencyTokens[2]));

				dependencyElement.appendChild(dependencyScopeElement);
			}

		}

		if (artifactIdToken[artifactIdToken.length - 1].endsWith(".jar")) {
			Element dependencyScopeElement = document.createElement("scope");

			dependencyScopeElement.appendChild(document.createTextNode(
				"system"));

			dependencyElement.appendChild(dependencyScopeElement);

			Element dependencySystemPathElement = document.createElement(
				"systemPath");

			dependencySystemPathElement.appendChild(
				document.createTextNode(
					dependencyTokens[dependencyTokens.length - 1]));

			dependencyElement.appendChild(dependencySystemPathElement);
		}

		dependenciesElement.appendChild(dependencyElement);
	}

	public static void createModulePOM(
		Element projectElement, Element portalSourceDirElement) {

		createBuildElement(portalSourceDirElement, projectElement);

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

		String path = _fullPath.substring(_portalPath.length());

		portalSourceDirElement.appendChild(
			document.createTextNode("${sourceDirectory}" + path));
	}

	public static void createProjectElement()
		throws Exception {

		Element projectElement = document.createElement("project");

		document.appendChild(projectElement);

		projectElement.setAttribute(
			"xmlns", "http://maven.apache.org/POM/4.0.0");
		projectElement.setAttribute(
			"xmlns:xsi",
			"http://www.w3.org/2001/XMLSchema-instance");
		projectElement.setAttribute(
			"xsi:schemaLocation",
			"http://maven.apache.org/POM/4.0.0 "
			+ "http://maven.apache.org/maven-v4_0_0.xsd");

		createArtifactElements(projectElement);
	}

	public static void createPropertiesElement(
		Element portalSourceDirElement, Element projectElement) {

		Element propertiesElement = document.createElement("properties");

		projectElement.appendChild(propertiesElement);

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

	public static void createRepositoriesElement(
		Element projectElement)
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
		Element repositoriesElement, String repoId,
		String repoUrl)
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

		documentBuilderFactory = DocumentBuilderFactory.newInstance();

		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		document = documentBuilder.newDocument();

		createProjectElement();

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
			_groupId = args[0];

			_artifactId = args[1];

			_version = args[2];

			_packaging = args[3];

			_name = args[4];

			_fullPath = args[5];

			_portalPath = args[6];

			_ivyDependency = args[7];

			_moduleBuildFile = args[8];

			_tokens = Arrays.copyOfRange(args, 9, args.length);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
				"Insufficient number of inputs, please use the following order "
				+ "of inputs: GroupId, ArtifactId, Version, Packaging, Name, "
				+ "FullPath-to-module, Portal-path, Modules, Dependencies");

			System.exit(1);
		}
	}

	public static void parseIvyDependencies(Element dependenciesElement) {
		if (!_ivyDependency.startsWith("$")) {
			try {
				File ivyFile = new File(_ivyDependency);

				Document ivyDocument = documentBuilder.parse(ivyFile);

				ivyDocument.getDocumentElement().normalize();

				NodeList ivyDependencyList
					= ivyDocument.getElementsByTagName("dependency");

				for (int i = 0; i < ivyDependencyList.getLength(); i++) {
					Node ivyDependencyNode = ivyDependencyList.item(i);

					Element ivyDependencyElement = (Element) ivyDependencyNode;
					String ivyDependency;

					if (ivyDependencyElement.getAttribute("conf").isEmpty()) {
						ivyDependency = ivyDependencyElement.getAttribute("org")
							+ ":" + ivyDependencyElement.getAttribute("rev")
							+ ":" + ivyDependencyElement.getAttribute("name");
					}
					else {
						String ivyConf
							= ivyDependencyElement.getAttribute("conf");

						if (ivyConf.endsWith("master")
							&& !ivyConf.startsWith("internal")) {
							ivyConf = "master";
						}
						else {
							ivyConf = "compile";
						}

						ivyDependency = ivyDependencyElement.getAttribute("org")
							+ ":" + ivyDependencyElement.getAttribute("rev")
							+ ":" + ivyConf + ":"
							+ ivyDependencyElement.getAttribute("name");
					}
					createDependencyElement(dependenciesElement, ivyDependency);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseModuleBuildFile(Element dependenciesElement) {
		try {
			File moduleBuildFile = new File(_moduleBuildFile);

			Document moduleBuildFileDocument
				= documentBuilder.parse(moduleBuildFile);

			moduleBuildFileDocument.getDocumentElement().normalize();

			NodeList modulePropertyList
				= moduleBuildFileDocument.getElementsByTagName("property");

			for (int i = 0; i < modulePropertyList.getLength(); i++) {
				Element modulePropertyElement = (Element) modulePropertyList.item(i);

				if (modulePropertyElement.getAttribute("name").equals("import.shared")) {
					String[] moduleDependencyList
						= modulePropertyElement.getAttribute("value").split(",");

					for (int j = 0; j < moduleDependencyList.length; j++) {
						String[] moduleDependencySplit
							= moduleDependencyList[j].split("/");

						createDependencyElement(dependenciesElement, _groupId
							+ ":" + _version + ":"
							+ moduleDependencySplit[moduleDependencySplit.length - 1]);
					}
				}
			}
			NodeList webLibPathNodeList =
				moduleBuildFileDocument.getElementsByTagName("path");

			for (int i = 0; i < webLibPathNodeList.getLength(); i++) {
				Element webLibPathElement =
					(Element) webLibPathNodeList.item(i);

				if (webLibPathElement.getAttribute("id").equals("web-lib.classpath")) {
					NodeList filesetNodeList =
						webLibPathElement.getElementsByTagName("fileset");

					Element filesetElement = (Element) filesetNodeList.item(0);

					String[] libDependencyList =
						filesetElement.getAttribute("includes").split(",");

					String libDependencyPath =
						filesetElement.getAttribute("dir");

					String parsedPath =
						libDependencyPath.replace("${project.dir}", _portalPath);

					for (int j = 0; j < libDependencyList.length; j++) {
						libDependencyPath =
							parsedPath + "/" + libDependencyList[j];

						File dependencyFile = new File(libDependencyPath);

						if (!dependencyFile.exists()) {
							System.out.println(
								"Path error at: " + parsedPath
									+ " for module: " + _artifactId);
						}
						else {
							createDependencyElement(
								dependenciesElement, libDependencyPath);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Document document;
	private static DocumentBuilder documentBuilder;
	private static DocumentBuilderFactory documentBuilderFactory;
	private static String _artifactId;
	private static String _fullPath;
	private static String _groupId;
	private static String _ivyDependency;
	private static String _moduleBuildFile;
	private static String _name;
	private static String _packaging;
	private static String _portalPath;
	private static String[] _tokens;
	private static String _version;

}
