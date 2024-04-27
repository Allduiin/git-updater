package controller

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets
import java.rmi.UnexpectedException

class BitbucketController {
    internal fun getPomFile(workspace: String, repoSlug: String, sourceBranch: String, bearerToken: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.bitbucket.org/2.0/repositories/$workspace/$repoSlug/src/$sourceBranch/pom.xml"))
            .header("Authorization", "Bearer $bearerToken")
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            return response.body()
        } else {
            throw Exception("Failed to fetch pom.xml: ${response.statusCode()} ${response.body()}")
        }
    }

    internal fun updateVersion(
        workspace: String,
        repoSlug: String,
        sourceBranch: String,
        updatedPomString: String,
        bearerToken: String,
        version: String
    ): Boolean {
        val newBranchName = "update-versions-branch-$version"
        val createBranchResult = createBranch(
            token = bearerToken,
            workspace = workspace,
            repoSlug = repoSlug,
            newBranchName = newBranchName,
            sourceBranch = sourceBranch
        )
        if (!createBranchResult) {
            throw UnexpectedException("Failed to create branch")
        }

        val updateFileResult = updateFile(bearerToken, workspace, repoSlug, newBranchName, "pom.xml", updatedPomString)
        if (!updateFileResult) {
            throw UnexpectedException("Failed to update File")
        }
        val prTitle = "Changed $repoSlug"
        return createPullRequest(bearerToken, workspace, repoSlug, newBranchName, sourceBranch, prTitle, "")
    }

    private fun createBranch(token: String, workspace: String, repoSlug: String, newBranchName: String, sourceBranch: String): Boolean {
        val uri = URI("https://api.bitbucket.org/2.0/repositories/$workspace/$repoSlug/refs/branches")
        val jsonBody = """
                {
                    "name": "$newBranchName",
                    "target": {"hash": "$sourceBranch"}
                }
            """.trimIndent()

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()

        val client = HttpClient.newHttpClient()
        val response = client.send(request, BodyHandlers.ofString())

        println("Branch created with status code: ${response.statusCode()}")
        return response.statusCode() == 201 // 201 Created
    }

    fun updateFile(token: String, workspace: String, repoSlug: String, branch: String, filePath: String, fileContent: String): Boolean {
        val uri = URI("https://api.bitbucket.org/2.0/repositories/$workspace/$repoSlug/src")
        val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"

        val formData = StringBuilder()
        formData.append("--$boundary\r\n")
        formData.append("Content-Disposition: form-data; name=\"branch\"\r\n\r\n")
        formData.append("$branch\r\n")
        formData.append("--$boundary\r\n")
        formData.append("Content-Disposition: form-data; name=\"files/$filePath\"; filename=\"$filePath\"\r\n")
        formData.append("Content-Type: text/xml\r\n\r\n") // Set Content-Type according to your file type
        formData.append(fileContent)
        formData.append("\r\n--$boundary--\r\n")

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .POST(HttpRequest.BodyPublishers.ofString(formData.toString(), StandardCharsets.UTF_8))
            .build()

        val client = HttpClient.newHttpClient()
        val response = client.send(request, BodyHandlers.ofString())

        println("Branch updated pom with status code: ${response.statusCode()}")
        return response.statusCode() == 201
    }

    private fun createPullRequest(
        token: String,
        workspace: String,
        repoSlug: String,
        sourceBranch: String,
        destinationBranch: String,
        title: String,
        description: String
    ): Boolean {
        val uri = URI("https://api.bitbucket.org/2.0/repositories/$workspace/$repoSlug/pullrequests")
        val jsonBody = """
            {
                "title": "$title",
                "source": {
                    "branch": {"name": "$sourceBranch"}
                },
                "destination": {
                    "branch": {"name": "$destinationBranch"}
                },
                "description": "$description"
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()

        val client = HttpClient.newHttpClient()
        val response = client.send(request, BodyHandlers.ofString())

        println("Pull request created with status code: ${response.statusCode()}")
        return response.statusCode() == 201 // 201 Created
    }
}