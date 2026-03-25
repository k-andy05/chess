package client;

public interface ClientState {
    String eval(String input) throws Exception;
    String help();
}
