package port;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private String login;
    private String password;
    private UUID userId;


    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.userId = UUID.randomUUID();
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public UUID getUserId() {
        return this.userId;
    }

    @Override
    public String toString() {
        return "Логин: " + this.login + "\tUUID: " + this.userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // одна ссылка
        if (obj == null || getClass() != obj.getClass()) return false; // разные классы или null
        User user = (User)obj; // приведение
        return this.login.equals(user.getLogin()) && this.userId.equals(user.getUserId()); // сравнение полей
    }

    public void toPrint() {
        System.out.println(this.toString());
    }

    public static void saveJSON (List<User> users, String path) {

        // Используем try-with-resources, чтобы автоматически закрывать writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            for (User user : users) {
                String jsonString = objectMapper.writeValueAsString(user);

                writer.write(jsonString);
                writer.newLine();
            }
            System.out.println("Данные успешно сохранены в файл users.json");
        } catch (IOException e) {
            System.out.println("Проблемы с формированием users json: " + e.getMessage());
        }
    }

    public static List<User> fromJSON(String path) {
        List<User> users = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {

                User user = mapper.readValue(line, User.class);
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Проблемы с чтением json: " + e.getMessage());
        }
        return users;
    }

}