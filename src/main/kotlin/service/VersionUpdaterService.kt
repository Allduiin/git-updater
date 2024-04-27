package service

import controller.BitbucketController

private val DEFAULT_WORKSPACE = "alageizia"
private val DEFAULT_REPOSLUG = "git-updater"
private val DEFAULT_GROUP_ID = "org.mockito"
private val DEFAULT_ARTIFACT_ID = "mockito-inline"
private val DEFAULT_VERSION = "1.2.5"
private val DEFAULT_SOURCE_BRANCH = "update-versions-branch"

class VersionUpdaterService {
    private val consoleCommunicationService = ConsoleCommunicationService()
    private val bitbucketController = BitbucketController()
    private val bitbucketService = BitbucketService()

    fun updateDependencyVersion(): Boolean {
        val (workspace, repoSlug, bearerToken) = getLocation()

        val branch = consoleCommunicationService.askSourceBranch()
        val groupId = consoleCommunicationService.askGroupId()
        val artifactId = consoleCommunicationService.askArtifactId()
        val newVersion = consoleCommunicationService.askNewVersion()

        val pomFile = bitbucketController.getPomFile(
            workspace = workspace,
            repoSlug = repoSlug,
            sourceBranch = branch,
            bearerToken = bearerToken
        )
        val updatedPom = bitbucketService.updateVersion(
            pomFile = pomFile,
            dependencyGroupId = groupId,
            dependencyArtifactId = artifactId,
            newVersion = newVersion
        )
        return bitbucketController.updateVersion(
            workspace = workspace,
            repoSlug = repoSlug,
            sourceBranch = branch,
            updatedPomString = updatedPom,
            bearerToken = bearerToken,
            version = newVersion
        )
    }

    private fun getLocation(): Triple<String, String, String> {
        consoleCommunicationService.welcome()
        val workspace = consoleCommunicationService.askWorkspacePath()
        val repoSlug = consoleCommunicationService.askRepoSlug()
        val bearerToken = consoleCommunicationService.askBearerToken()
        return Triple(workspace, repoSlug, bearerToken)
    }
}