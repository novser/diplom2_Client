import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Client extends Thread {

    private final int port;
    private final String ip;
    private String name = "";
    private final String massageToConsolePattern = "%s : %s\n";

    public Client() {
        Map<String, String> settings = Main.getSettings(name);
        this.ip = settings.get("ip");
        this.port = Integer.parseInt(settings.get("port"));
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(ip, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите никнейм:");
            name = scanner.nextLine();
            Log.writeStart(name);
            String msg;
            System.out.println("Введите сообщение или \"exit\" для выхода");
            while (true) {
                if (scanner.hasNext()) {
                    msg = scanner.nextLine();
                    if ("exit".equals(msg)) {
                        Log.writeFinish(name);
                        break;
                    }
                    Massage outputMassage = JsonHelper.getMassageFromJson(msg);
                    System.out.println(String.format(massageToConsolePattern, "Вы", outputMassage.getText()));
                    out.println(JsonHelper.getJsonTextFromMassage(outputMassage));
                    Log.writeOutputMassage(name, outputMassage);
                }

                Massage inputMassage = JsonHelper.getMassageFromJson(in.readLine());
                System.out.println(String.format(massageToConsolePattern, inputMassage.getSender(), inputMassage.getText()));
                Log.writeInputMassage(name, inputMassage);
            }
        } catch (Exception e) {
            Log.writeError(name, e.getMessage());
        }

    }
}
