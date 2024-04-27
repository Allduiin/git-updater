package service

import org.w3c.dom.Document

class BitbucketService {
    private val documentMapperService = DocumentMapperService()

    internal fun updateVersion(
        pomFile: String,
        dependencyGroupId: String,
        dependencyArtifactId: String,
        newVersion: String
    ): String {
        val pomDocument = documentMapperService.parseXml(pomFile)
        updateDependencyVersion(pomDocument, dependencyGroupId, dependencyArtifactId, newVersion)
        return documentMapperService.serializeXml(pomDocument)
    }

    private fun updateDependencyVersion(doc: Document, groupId: String, artifactId: String, newVersion: String) {
        val dependencies = doc.getElementsByTagName("dependency")
        for (i in 0 until dependencies.length) {
            val dependency = dependencies.item(i) as org.w3c.dom.Element
            val gId = dependency.getElementsByTagName("groupId").item(0).textContent
            val aId = dependency.getElementsByTagName("artifactId").item(0).textContent

            if (gId == groupId && aId == artifactId) {
                val versionNode = dependency.getElementsByTagName("version").item(0)
                versionNode.textContent = newVersion
                break
            }
        }
    }
}