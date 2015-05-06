import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreatePOM {

	public static void createArtifactElements(Document document,
		Element projectElement, String _artifactId, String _groupId,
		String _name, String _packaging, String _version)
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
	}
	public static Element createProjectElement(Document document,
		String _artifactId, String _groupId, String _name, String _packaging,
		String _version) throws Exception {
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

		createArtifactElements(document,projectElement,_artifactId,
			_groupId,_name,_packaging,_version);

		return projectElement;
	}
}