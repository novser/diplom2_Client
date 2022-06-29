import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Client extends Thread{

    private final int port;
    private final String ip;
    private  String name = "";
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
             Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Введите никнейм:");
            name = scanner.nextLine();
            while (true){
                System.out.println("Введите сообщение или exit для выхода:");
                String text = scanner.nextLine();

                Massage massage = new Massage(name, text);
                out.println(JsonHelper.getJsonTextFromMassage(massage));
                if (text.equals("exit")) break;

                Massage inputMassage = JsonHelper.getMassageFromJson(in.readLine());
                System.out.println(String.format(massageToConsolePattern, inputMassage.getSender(), inputMassage.getText()));
            }
//            out.println(scanner.nextLine());
//            System.out.println("Ответ: " + in.readLine());
        } catch (Exception e) {
            Log.writeError(name, e.getMessage());
        }
    }
}
