package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>());
        HashSet<Session> relevantSessionSet = connections.get(gameID);
        relevantSessionSet.add(session);
    }

    public void remove(Session session, Integer gameID) throws ResponseException {
        HashSet<Session> relevantSessionSet = connections.get(gameID);
        if (relevantSessionSet == null) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: Game does not exist, so neither does the session");
        }
        relevantSessionSet.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException, ResponseException {
        String msg = new Gson().toJson(notification);
        HashSet<Session> relevantSessionSet = connections.get(gameID);
        if (relevantSessionSet == null) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: Game does not exist, so neither do any sessions");
        }
        for (Session c : relevantSessionSet) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
