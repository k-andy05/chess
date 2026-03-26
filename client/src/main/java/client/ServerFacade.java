package client;

import com.google.gson.Gson;
import exception.ResponseException;
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
        LoginResult loginResult = handleResponse(httpResponse, LoginResult.class);
        if (loginResult != null) {
            authToken = loginResult.authToken;
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
        HttpRequest httpRequest = buildRequest("POST", "/game", request);
        return handleResponse(sendResponse(httpRequest), CreateResult.class);
    }

    public ListResult list(ListRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("GET", "/game", request);
        return handleResponse(sendResponse(httpRequest), ListResult.class);
    }

    public JoinResult join(JoinRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("PUT", "/game", request);
        JoinResult joinResult = handleResponse(sendResponse(httpRequest), JoinResult.class);
        this.gameID = request.gameID;
        this.playerColor = request.playerColor;
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
            return new Gson().fromJson(jsonBody, responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
