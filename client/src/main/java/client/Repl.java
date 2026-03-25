package client;

import java.util.Scanner;

public class Repl {
    private ClientState state;
    private String currentStatus;
    private final ServerFacade server;

    public Repl(String serverUrl) {
        this.currentStatus = "LOGGED_OUT";
        this.server = new ServerFacade(serverUrl);
        this.state = new PreLoginClient(server);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";

        System.out.println("Welcome to 240 chess! Type help to get started.");

        while (!result.equals("quit")) {
            System.out.print("\n" + currentStatus + ">>> ");
            String input = scanner.nextLine();

            try {
                result = state.eval(input);
                System.out.println(result);

                switch (result) {
                    case "LOGIN_SUCCESS" -> {
                        currentStatus = "LOGGED_IN";
                        state = new PostLoginClient(server);
                    }
                    case "ENTER_GAME" -> {
                        return;
                    }
//                    state = new GameplayClient();
                    case "LOGOUT" -> {
                        currentStatus = "LOGGED_OUT";
                        state = new PreLoginClient(server);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
