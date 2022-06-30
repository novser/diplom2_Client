import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class Client extends Thread{

    private final int port;
    private final String ip;
    private int buffer;
    private  String name = "";
    private final String massageToConsolePattern = "%s : %s\n";

    public Client() {
        Map<String, String> settings = Main.getSettings(name);
        this.ip = settings.get("ip");
        this.port = Integer.parseInt(settings.get("port"));
        this.buffer = Integer.parseInt(settings.get("buffer"));
    }

    @Override
    public void run() {
        InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
        try (final SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(socketAddress);
            final ByteBuffer inputBuffer = ByteBuffer.allocate(buffer);
            System.out.println("Введите никнейм:");
            name = scanner.nextLine();
            Log.writeStart(name);
            String text;
            while (true) {
                System.out.println("Введите сообщение или \"exit\" для выхода");
                text = scanner.nextLine();
                if ("exit".equals(text)) break;
                Massage outputMassage = new Massage(name, text);
                String jsonString = JsonHelper.getJsonTextFromMassage(outputMassage);
                socketChannel.write(ByteBuffer.wrap(jsonString.getBytes(StandardCharsets.UTF_8)));
                Log.writeOutputMassage(name, outputMassage);

                int bytesCount = socketChannel.read(inputBuffer);
                Massage inputMassage = JsonHelper.getMassageFromJson(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
                System.out.println(String.format(massageToConsolePattern, inputMassage.getSender(), inputMassage.getText()));
                Log.writeInputMassage(name, inputMassage);
                inputBuffer.clear();
            }
        } catch (Exception e) {
            Log.writeError(name, e.getMessage());
        }
        Log.writeFinish(name);
    }
}
