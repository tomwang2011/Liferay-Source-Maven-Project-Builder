
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

	public static void createBuildElement(
		Element portalSourceDirElement, Element projectElement) {

		Element buildElement = document.createElement("build");

		projectElement.appendChild(buildElement);

		buildElement.appendChild(portalSourceDirElement);

		if (new File(
				_fullPath.substring(
					0, _fullPath.length() -3) + "test").exists()) {

			Element testSourceDirElement = document.createElement(
				"testSourceDirectory");

			String path = _fullPath.substring(_portalPath.length());

			path = path.substring(0, path.length() - 3) + "test";

			testSourceDirElement.appendChild(
				document.createTextNode("${sourceDirectory}" + path));

			buildElement.appendChild(testSourceDirElement);
		}
	}

	public static void createDependenciesElement(Element projectElement) {
		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		parseModuleBuildFile(dependenciesElement);

		parseIvyDependencies(dependenciesElement);

		for (String _token : _tokens) {
			createDependencyElement(dependenciesElement, _token);
		}
	}

	public static void createDependencyElement(
		Element dependenciesElement, String dependencyToken) {

		String[] dependencyTokens = dependencyToken.split(":");
		String[] artifactIdToken =
			dependencyTokens[dependencyTokens.length - 1].split("/");

		Element dependencyElement = document.createElement("dependency");
		Element dependencyGroupIdElement = document.createElement("groupId");

		if (artifactIdToken[0].equals("")) {
			dependencyGroupIdElement.appendChild(
				document.createTextNode(_groupId));
		}
		else {
			dependencyGroupIdElement.appendChild(
				document.createTextNode(dependencyTokens[0]));
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
			dependencyVersionElement.appendChild(
				document.createTextNode(_version));
		}
		else {
			dependencyVersionElement.appendChild(
				document.createTextNode(dependencyTokens[1]));
		}

		dependencyElement.appendChild(dependencyVersionElement);

		if (dependencyTokens.length > 3) {
			Element dependencyScopeElement = document.createElement("scope");

			if (dependencyTokens[2].equals("master")) {
				dependencyScopeElement.appendChild(
					document.createTextNode("compile"));

				dependencyElement.appendChild(dependencyScopeElement);

				Element exclusionsElement = document.createElement(
					"exclusions");

				dependencyElement.appendChild(exclusionsElement);

				Element exclusionElement = document.createElement("exclusion");

				exclusionsElement.appendChild(exclusionElement);

				Element exclusionGroupIdElement = document.createElement(
					"groupId");

				exclusionElement.appendChild(exclusionGroupIdElement);

				exclusionGroupIdElement.appendChild(
					document.createTextNode("*"));

				Element exclusionArtifactIdElement = document.createElement(
					"artifactId");

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

			dependencyScopeElement.appendChild(
				document.createTextNode("system"));

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

			_fullPath = args[5];

			_portalPath = args[6];

			_ivyDependency = args[7];

			_moduleBuildFile = args[8];

			_tokens = Arrays.copyOfRange(args, 9, args.length);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
				"Insufficient number of inputs, please use the following" +
				" order of inputs: GroupId, ArtifactId, Version, Packaging, " +
				"Name, FullPath-to-module, Portal-path, Ivy-filepath, " +
				"Build-filepath, Module-specific-dependencies");

			System.exit(1);
		}
	}

	public static void parseIvyDependencies(Element dependenciesElement) {
		if (!_ivyDependency.startsWith("$")) {
			try {
				File ivyFile = new File(_ivyDependency);

				Document ivyDocument = documentBuilder.parse(ivyFile);

				ivyDocument.getDocumentElement().normalize();

				NodeList ivyDependencyList =
					ivyDocument.getElementsByTagName("dependency");

				for (int i = 0; i < ivyDependencyList.getLength(); i++) {
					Node ivyDependencyNode = ivyDependencyList.item(i);

					Element ivyDependencyElement = (Element)ivyDependencyNode;

					String ivyDependency;

					if (ivyDependencyElement.getAttribute("conf").isEmpty()) {
						ivyDependency =
							ivyDependencyElement.getAttribute("org") + ":" +
							ivyDependencyElement.getAttribute("rev") + ":" +
							ivyDependencyElement.getAttribute("name");
					}
					else {
						String ivyConf = ivyDependencyElement.getAttribute(
							"conf");

						if (ivyConf.endsWith("master") &&
							!ivyConf.startsWith("internal")) {

							ivyConf = "master";
						}
						else {
							ivyConf = "compile";
						}

						ivyDependency =
							ivyDependencyElement.getAttribute("org") +
							":" + ivyDependencyElement.getAttribute("rev") +
							":" + ivyConf + ":" +
							ivyDependencyElement.getAttribute("name");
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
		if (!_moduleBuildFile.startsWith("$")) {
			try {
				File moduleBuildFile = new File(_moduleBuildFile);

				Document moduleBuildFileDocument = documentBuilder.parse(
					moduleBuildFile);

				Element moduleBuildFileElement =
					moduleBuildFileDocument.getDocumentElement();

				moduleBuildFileElement.normalize();

				NodeList modulePropertyList =
					moduleBuildFileDocument.getElementsByTagName("property");

				for (int i = 0; i < modulePropertyList.getLength(); i++) {
					Element modulePropertyElement =
						(Element)modulePropertyList.item(i);

					String modulePropertyElementName =
						modulePropertyElement.getAttribute("name");

					if (modulePropertyElementName.equals("import.shared")) {
						String moduleDependencyString =
							modulePropertyElement.getAttribute("value");

						String[] moduleDependencyList =
							moduleDependencyString.split(",");

						for (String moduleDependency : moduleDependencyList) {
							String[] moduleDependencySplit =
								moduleDependency.split("/");

							createDependencyElement(
								dependenciesElement, _groupId + ":" + _version +
								":" + moduleDependencySplit[
									moduleDependencySplit.length - 1]);
						}
					}
				}

				NodeList webLibPathNodes =
					moduleBuildFileDocument.getElementsByTagName("path");

				for (int i = 0; i < webLibPathNodes.getLength(); i++) {
					Element webLibPathElement =
						(Element)webLibPathNodes.item(i);

					String webLibPathElementId =
						webLibPathElement.getAttribute("id");

					if (webLibPathElementId.equals("web-lib.classpath")) {
						NodeList filesetNodeList =
							webLibPathElement.getElementsByTagName("fileset");

						Element filesetElement =
							(Element)filesetNodeList.item(0);

						String libDependencyString =
							filesetElement.getAttribute("includes");

						String[] libDependencyList =
							libDependencyString.split(",");

						String libDependencyPath = filesetElement.getAttribute(
							"dir");

						String parsedPath = libDependencyPath.replace(
							"${project.dir}", _portalPath);

						for (String libDependency : libDependencyList) {
							libDependencyPath =
								parsedPath + "/" + libDependency;

							File dependencyFile = new File(libDependencyPath);

							if (!dependencyFile.exists()) {
								System.out.println(
									"Lib path error at: " + parsedPath +
									" for module: " + _artifactId);
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
	}

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
	private static Document document;
	private static DocumentBuilder documentBuilder;
	private static DocumentBuilderFactory documentBuilderFactory;

}