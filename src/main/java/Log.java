import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static File logFile = new File("src\\main\\resources\\File.log");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static StringBuilder log = new StringBuilder();
    private static final String inputMassagePattern = "От пользователя %s пришло сообщение: \"%s\"";
    private static final String outputMassagePattern = "От пользователя %s отправлено сообщение: \"%s\"";

    private static void writeLog(String name, String logText) {
        log.append(String.format("%s %s: %s \n", currentTime(), name, logText));
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write(log.toString());
            writer.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String currentTime() {
        return formatter.format(new Date());
    }

    public static void writeStart(String name) {
        writeLog(name, "запуск");
    }

    public static void writeFinish(String name) {
        writeLog(name, "отключение");
    }

    public static void writeInputMassage(String name, String text) {
        writeLog(name, String.format(inputMassagePattern, name, text));
    }

    public static void writeOutputMassage(String name, String text) {
        writeLog(name, String.format(outputMassagePattern, name, text));
    }

    public static void writeError(String name, String error) {
        writeLog(name, error);
    }
}
