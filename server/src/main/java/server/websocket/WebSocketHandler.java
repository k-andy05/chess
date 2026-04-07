package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import websocket.commands.UserGameCommand;

import io.javalin.websocket.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> { connect(); }
                case MAKE_MOVE -> { makeMove(); }
                case LEAVE -> { leave(); }
                case RESIGN -> { resign(); }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect() throws IOException {}

    private void makeMove() throws IOException {}

    private void leave() throws IOException {}

    private void resign() throws IOException {}
}
