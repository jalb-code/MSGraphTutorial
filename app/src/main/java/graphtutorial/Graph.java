package graphtutorial;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;

import okhttp3.Request;

public class Graph {

    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;

    private static ClientSecretCredential _clientSecretCredential;    
    private static GraphServiceClient<Request> _appClient;

    public static void initializeGraphForAppAuth(Properties properties, Consumer<DeviceCodeInfo> challenge)
            throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        if (_clientSecretCredential == null) {
            final String clientId = _properties.getProperty("app.clientId");
            final String tenantId = _properties.getProperty("app.tenantId");
            final String clientSecret = _properties.getProperty("app.clientSecret");
            
            _clientSecretCredential = new ClientSecretCredentialBuilder()
                    .clientId(clientId)
                    .tenantId(tenantId)                    
                    .clientSecret(clientSecret)
                    .build();
            
        }

        if (_appClient == null) {
            final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                    List.of("https://graph.microsoft.com/.default"), _clientSecretCredential);

            _appClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
    }

    public static String getUserToken() throws Exception {
        // Ensure credential isn't null
        if (_deviceCodeCredential == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        final String[] graphUserScopes = _properties.getProperty("app.graphUserScopes").split(",");

        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphUserScopes);

        final AccessToken token = _deviceCodeCredential.getToken(context).block();
        return token.getToken();
    }

    public static UserCollectionPage getUsers() throws Exception {
        // Ensure client isn't null
        if (_appClient == null) {
            throw new Exception("Graph has not been initialized for app auth");
        }

        return _appClient.users()
                .buildRequest()
                .select("displayName,id,mail")
                .top(25)
                .orderBy("displayName")
                .get();
    }

    public static EventCollectionPage getCalendarEvents(String userId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        // Ensure client isn't null
        if (_appClient == null) {
            throw new Exception("Graph has not been initialized for app auth");
        }

        LinkedList<Option> requestOptions = new LinkedList<Option>();
        requestOptions.add(new QueryOption("startDateTime", DateTimeFormatter.ISO_DATE_TIME.format(startDate)));
        requestOptions.add(new QueryOption("endDateTime", DateTimeFormatter.ISO_DATE_TIME.format(endDate)));

        return _appClient.users(userId)
                .calendar()
                .calendarView()
                .buildRequest(requestOptions)
                .get();                
    }
}