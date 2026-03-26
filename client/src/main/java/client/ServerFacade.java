package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import model.GameData;
import request.*;
import result.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    public String authToken;
    public int gameID;
    public String playerColor;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("POST", "/session", request);
        HttpResponse<String> httpResponse = sendResponse(httpRequest);
//        System.out.println("RAW LOGIN RESPONSE: " + httpResponse.body());
//        return handleResponse(httpResponse, LoginResult.class);
        LoginResult loginResult = handleResponse(httpResponse, LoginResult.class);
        if (loginResult != null) {
//            authToken = new Gson().toJson(loginResult);
            authToken = loginResult.authToken;
//            System.out.println("AuthToken from login response is \"" + loginResult.authToken + "\"");
        }
        return loginResult;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("POST", "/user", request);
        HttpResponse<String> httpResponse = sendResponse(httpRequest);
        RegisterResult result = handleResponse(httpResponse, RegisterResult.class);
        if (result != null && result.authToken != null) {
            this.authToken = result.authToken;
        }
        return result;
    }

    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("DELETE", "/session", request);
        authToken = null;
        return handleResponse(sendResponse(httpRequest), LogoutResult.class);
    }

    public CreateResult create(CreateRequest request) throws ResponseException {
//        System.out.println("Making sure request passed into serverfacade.create is right..." + request.gameName + " " + request.authToken);
//        System.out.println("Make sure authtoken from request == authtoken from serverfacade local var" + authToken + "==" + request.authToken);
        HttpRequest httpRequest = buildRequest("POST", "/game", request);
        CreateResult createResult = handleResponse(sendResponse(httpRequest), CreateResult.class);
        return createResult;
    }

    public ListResult list(ListRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("GET", "/game", request);
        return handleResponse(sendResponse(httpRequest), ListResult.class);
    }

    public JoinResult join(JoinRequest request) throws ResponseException {
        System.out.println("Join Request params: " + request.authToken + " " + request.playerColor + " " + request.gameID);
        HttpRequest httpRequest = buildRequest("PUT", "/game", request);
        JoinResult joinResult = handleResponse(sendResponse(httpRequest), JoinResult.class);
        this.gameID = request.gameID;
        this.playerColor = request.playerColor;
        System.out.println("Current game ID: " + gameID);
        return joinResult;
    }

    public void clear() throws ResponseException {
        HttpRequest httpRequest = buildRequest("DELETE", "/db", null);
        handleResponse(sendResponse(httpRequest), null);
    }

    public void observe(JoinRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("PUT", "/game", request);
        handleResponse(sendResponse(httpRequest), null);
        this.gameID = request.gameID;
        this.playerColor = request.playerColor;
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
//        System.out.println("AUTH TOKEN BEING SENT: " + authToken);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendResponse(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }
        }
        if (responseClass != null && response.body() != null && !response.body().isEmpty()) {
            String jsonBody = response.body();
            if (responseClass == ListResult.class) {
                jsonBody = jsonBody.replace("\"games\":", "\"gameList\":");
            }
//            GsonBuilder builder = new GsonBuilder();
//            Gson gson = builder.create();
            return new Gson().fromJson(jsonBody, responseClass);
        }
        return null;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
