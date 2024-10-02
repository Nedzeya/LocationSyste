import spock.lang.Specification
import spock.lang.Stepwise
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPatch
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
/**
 * Before running the integration tests, you need to START the application SERVER
 * (this can be done manually. For example, using a Gradle task: ./gradlew bootRun)
 **/
@Stepwise
class IntegrationTest extends Specification {

    static final String BASE_URL = "http://localhost:8081"

    def "Register new user account by email"() {
        when: "Register a new user"
        def userUrl = new URL("${BASE_URL}/api/users")
        def connection = (HttpURLConnection) userUrl.openConnection()
        connection.requestMethod = 'POST'
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        def userPayload = '{"name":"John","email":"john@example.com"}'
        connection.outputStream.withWriter { writer -> writer.write(userPayload) }
        connection.connect()

        then: "User should be created successfully"
        connection.responseCode == HttpURLConnection.HTTP_CREATED
    }

    def "Create location"() {
        when: "Create a new location"
        def locationUrl = new URL("${BASE_URL}/api/locations")
        def connection = (HttpURLConnection) locationUrl.openConnection()
        connection.requestMethod = 'POST'
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        def locationPayload = '{"name":"Home","address":"123 Main St, Springfield, IL, 62704","owner":{"name":"John","email":"john@example.com"}}'
        connection.outputStream.withWriter { writer -> writer.write(locationPayload) }
        connection.connect()

        then: "Location should be created successfully"
        connection.responseCode == HttpURLConnection.HTTP_CREATED
    }

    def "Register friend user account by email"() {
        when: "Register a new user"
        def userUrl = new URL("${BASE_URL}/api/users")
        def connection = (HttpURLConnection) userUrl.openConnection()
        connection.requestMethod = 'POST'
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        def userPayload = '{"name":"Friend","email":"friend@example.com"}'
        connection.outputStream.withWriter { writer -> writer.write(userPayload) }
        connection.connect()

        then: "User should be created successfully"
        connection.responseCode == HttpURLConnection.HTTP_CREATED
    }

    def "Share location with another user"() {
        when: "Share the location with another user"
        def shareUrl = new URL("${BASE_URL}/api/locations/1/share?userEmail=friend@example.com&accessLevel=READ_ONLY")
        def connection = (HttpURLConnection) shareUrl.openConnection()
        connection.requestMethod = 'POST'
        connection.connect()

        then: "Location should be shared successfully"
        connection.responseCode == HttpURLConnection.HTTP_OK
    }

    def "Get all friend users on the location"() {
        when: "Get all friend users on the location"
        def friendsUrl = new URL("${BASE_URL}/api/locations/1/friends")
        def connection = (HttpURLConnection) friendsUrl.openConnection()
        connection.requestMethod = 'GET'
        connection.connect()

        then: "Should return the list of friends"
        connection.responseCode == HttpURLConnection.HTTP_OK
    }

    def "Manage access for friend user on the location"() {
        when: "Manage access for a friend user"
        CloseableHttpClient client = HttpClients.createDefault()
        HttpPatch httpPatch = new HttpPatch("${BASE_URL}/api/locations/1/access?userEmail=friend@example.com&accessLevel=ADMIN")
        httpPatch.setHeader("Content-Type", "application/json")

        HttpResponse response = client.execute(httpPatch)

        then: "Access should be updated successfully"
        response.statusLine.statusCode == 200

        cleanup:
        client.close()
    }

    def "Get all locations available for first user"() {
        when: "Get all locations available for a user"
        def locationsUrl = new URL("${BASE_URL}/api/users/1/availableLocations")
        def connection = (HttpURLConnection) locationsUrl.openConnection()
        connection.requestMethod = 'GET'
        connection.connect()

        then: "Should return the list of locations"
        connection.responseCode == HttpURLConnection.HTTP_OK
    }

    def "Create multiple locations for second user"() {
        when: "Create 9 locations for second user"
        for (int i = 1; i <= 9; i++) {
            def locationUrl = new URL("${BASE_URL}/api/locations")
            def connection = (HttpURLConnection) locationUrl.openConnection()
            connection.requestMethod = 'POST'
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            def locationPayload = """
        {
            "name": "Home${i}",
            "address": "${i + 123} Main St, Springfield, IL, 6270${i}",
            "owner": {
                "name": "Friend",
                "email": "friend@example.com"
            }
        }
        """
            connection.outputStream.withWriter { writer -> writer.write(locationPayload) }
            connection.connect()

            assert connection.responseCode == HttpURLConnection.HTTP_CREATED
        }

        then: "All locations should be created successfully"
        true
    }

    def "Get all locations available for second user and test caching"() {
        when: "Get all locations available for a user the first time"
        long startTime1 = System.currentTimeMillis()
        def locationsUrl = new URL("${BASE_URL}/api/users/2/availableLocations")
        def connection1 = (HttpURLConnection) locationsUrl.openConnection()
        connection1.requestMethod = 'GET'
        connection1.connect()

        long endTime1 = System.currentTimeMillis()
        long duration1 = endTime1 - startTime1

        then: "Should return the list of locations with a cache miss"
        connection1.responseCode == HttpURLConnection.HTTP_OK

        when: "Get all locations available for a user the second time"
        long startTime2 = System.currentTimeMillis()
        def connection2 = (HttpURLConnection) locationsUrl.openConnection()
        connection2.requestMethod = 'GET'
        connection2.connect()

        long endTime2 = System.currentTimeMillis()
        long duration2 = endTime2 - startTime2

        then: "Should return the list of locations from cache"
        connection2.responseCode == HttpURLConnection.HTTP_OK

        and: "The second response should be faster due to cache hit"
        "Second request (cache hit) should be faster than the first request (cache miss) or the same "
        assert duration2 <= duration1

        println "First request duration (cache miss): ${duration1} ms"
        println "Second request duration (cache hit): ${duration2} ms"
    }
}