import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static final String settingsPath = "src\\main\\resources\\settings.txt";

    private static String name = "";
    private static final String massageToConsolePattern = "%s : %s\n";
    private static final int timeout = 500;

    public static void main(String[] args) {
        Map<String, String> settings = getSettings();
        String ip = settings.get("ip");
        int port = Integer.parseInt(settings.get("port"));

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите никнейм:");
            name = scanner.nextLine();
            Log.writeStart(name);
            String msg;
            System.out.println("Введите сообщение или \"exit\" для выхода");
            while (true) {
                Thread.sleep(timeout);
                try (Socket socket = new Socket(ip, port);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true))
                {

                    if (scanner.hasNext()) {
                        msg = scanner.nextLine();
                        if ("exit".equals(msg)) {
                            Log.writeFinish(name);
                            break;
                        }
                        System.out.println(String.format(massageToConsolePattern, "Вы: ", msg));
                        out.println(name + ":" + msg);
                        Log.writeOutputMassage(name, msg);
                    }

                    in.lines().forEach(element -> {System.out.println(element); Log.writeInputMassage(name, element);});
                }
            }
        } catch (Exception e) {
            Log.writeError(name, e.getMessage());
        }
    }

    public static Map<String, String> getSettings() {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream fin = new FileInputStream(settingsPath)) {
            int i;
            while ((i = fin.read()) != -1) {
                builder.append((char) i);
            }
        } catch (IOException ex) {
            Log.writeError("MAIN", ex.getMessage());
        }

        Map<String, String> settings = Arrays.stream(builder.toString().split(";"))
                .map(element -> element.split(":"))
                .collect(Collectors.toMap(element -> clearWord(element[0]), element -> clearWord(element[1])));

        return settings;
    }

    private static String clearWord(String word) {
        return word.replaceAll("(?U)[\\pP\\s]", "");
    }
}
