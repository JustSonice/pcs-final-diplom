import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        engine.readUnusefulWords("stop-ru.txt");

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            System.out.println("Сервер успешно запущен!");
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = new Socket("localhost", 8989);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    Scanner wordEnter = new Scanner(System.in);
                    System.out.println("Введите слово для поиска:");
                    String word = wordEnter.nextLine();
                    if (word != null) {
                        System.out.println("Поисковый запрос: " + word);

                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();

                        List<PageEntry> resp = engine.search(word);
                        System.out.println("Результаты поиска: \n " + resp);
                        out.println(gson.toJson(resp));
                        break;
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    System.out.println("Невозможно запустить сервер!");
                    e.printStackTrace();
                }
            }
        }
    }
}