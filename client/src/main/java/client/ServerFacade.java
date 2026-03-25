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

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("POST", "/session", request, null);
        HttpResponse<String> httpResponse = sendResponse(httpRequest);
        return handleResponse(httpResponse, LoginResult.class);
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        HttpRequest httpRequest = buildRequest("POST", "/user", request, null);
        HttpResponse<String> httpResponse = sendResponse(httpRequest);
        return handleResponse(httpResponse, RegisterResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
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
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
