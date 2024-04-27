package service

private const val WELCOME_MESSAGE = "Welcome to the Dependency Version Updater!"
private const val ENTER_WORKSPACE_MESSAGE = "Enter the bitbucket workspace: "
private const val ENTER_REPOSLUG_MESSAGE = "Enter the bitbucket repository name: "
private const val ENTER_BRANCH_MESSAGE = "Enter the source branch to change versions(Default main): "
private const val ENTER_BEARER_MESSAGE = "Enter your Bitbucket Bearer token: "
private const val ENTER_GROUP_ID_MESSAGE = "Enter the dependency group id: "
private const val ENTER_ARTIFACT_ID_MESSAGE = "Enter the dependency artifact id: "
private const val ENTER_VERSION_MESSAGE = "Enter the new version for the dependency: "
private const val DEFAULT_BRANCH = "main"

private const val NULL_PATH = "Path to pom.xml cannot be null"
private const val NULL_BEARER = "Bearer token cannot be null"
private const val NULL_ARTIFACT_ID = "Artifact id cannot be null"
private const val NULL_GROUP_ID = "Group id cannot be null"
private const val NULL_VESRION = "New version cannot be null"


class ConsoleCommunicationService {
    internal fun welcome() {
        println(WELCOME_MESSAGE)
    }

    internal fun askWorkspacePath(): String {
        print(ENTER_WORKSPACE_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_PATH)
    }

    internal fun askRepoSlug(): String {
        print(ENTER_REPOSLUG_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_PATH)
    }

    internal fun askSourceBranch(): String {
        print(ENTER_BRANCH_MESSAGE)
        return readLine() ?: DEFAULT_BRANCH
    }

    internal fun askBearerToken(): String {
        print(ENTER_BEARER_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_BEARER)
    }

    internal fun askGroupId(): String {
        print(ENTER_GROUP_ID_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_GROUP_ID)
    }


    internal fun askArtifactId(): String {
        print(ENTER_ARTIFACT_ID_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_ARTIFACT_ID)
    }

    internal fun askNewVersion(): String {
        print(ENTER_VERSION_MESSAGE)
        return readLine() ?: throw IllegalArgumentException(NULL_VESRION)
    }
}