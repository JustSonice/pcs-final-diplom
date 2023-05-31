import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            System.out.println("Сервер успешно запущен!");

            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
            System.out.println(engine.search("люди"));
            engine.readUnusefulWords("stop-ru.txt");

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    String word = in.readLine();
                    List<PageEntry> resp = engine.search(word);
                    out.println(gson.toJson(resp));
                }
            }
        } catch (IOException e) {
            System.out.println("Невозможно запустить сервер!");
            e.printStackTrace();
        }
    }
}