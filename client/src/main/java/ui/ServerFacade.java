package ui;

import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        var result = handleResponse(response,null);
    }

    public AuthData register(UserData user) throws Exception {
        var request = buildRequest("POST", "/user", user,null);
        var response = sendRequest(request);
        return handleResponse(response,AuthData.class);
    }

    public AuthData login(UserData user) throws Exception {
        var request = buildRequest("POST", "/session", user,null);
        var response = sendRequest(request);
        return handleResponse(response,AuthData.class);
    }

    public void logout(AuthData auth) throws Exception {
        var request = buildRequest("DELETE", "/session", null, auth.authToken());
        var response = sendRequest(request);
        var result = handleResponse(response,null);
    }

    public GameData createGame(AuthData auth, GameData game) throws Exception {
        var request = buildRequest("POST", "/game", game, auth.authToken());
        var response = sendRequest(request);
        return handleResponse(response,GameData.class);
    }

    public void joinGame(AuthData auth, JoinGameRequest joinReq) throws Exception {
        var request = buildRequest("PUT", "/game", joinReq, auth.authToken());
        var response = sendRequest(request);
        var result = handleResponse(response,null);
    }

    public ListGamesResult listGames(AuthData auth) throws Exception {
        var request = buildRequest("GET", "/game", null, auth.authToken());
        var response = sendRequest(request);
        return handleResponse(response,ListGamesResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if(authToken!=null){
            request.header("authorization",authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception("failure: " +  body);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
