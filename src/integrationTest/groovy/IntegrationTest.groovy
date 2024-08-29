import spock.lang.Specification
import spock.lang.Stepwise


@Stepwise

// Before running the tests, you need to START the application SERVER
// (this can be done manually. For example, using a Gradle task: ./gradlew bootRun)

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

    def "Get all locations available for user"() {
        when: "Get all locations available for a user"
        def locationsUrl = new URL("${BASE_URL}/api/users/1/availableLocations")
        def connection = (HttpURLConnection) locationsUrl.openConnection()
        connection.requestMethod = 'GET'
        connection.connect()

        then: "Should return the list of locations"
        connection.responseCode == HttpURLConnection.HTTP_OK
    }
}