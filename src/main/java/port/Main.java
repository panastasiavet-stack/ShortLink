package alexuuport;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static private List<User> users = new ArrayList<>();

    static LinkManager manager = new LinkManager();

    static String message() {
        return "Hello!";
    }

    public static void main(String[] args) {

        System.out.println(message());

        users = User.fromJSON("users.json");

        //меню авторизации, регистрации и просмотра пользователей с UUID
        label:
        while(true) {
            System.out.println("Введите \"1\", для авторизации;\n" +
                    "Введите \"2\", для регистрации нового пользователя;\n" +
                    "Введите \"3\", для просмотра ранее зарегистрированных пользователей;\n" +
                    "Введите \"0\", для выхода из программы;\n");
            String cmd = scanner.nextLine();
            switch (cmd) {
                case "1":
                    User activeUser = toAuthorization();
                    if(activeUser != null) {
                        toRun(activeUser);}
                    break;
                case "2":
                    toRegister();
                    break;
                case "3":
                    toPrintUsers(users);
                    break;
                case "0":
                    User.saveJSON(users, "users.json");
                    manager.saveJSON(manager.links, "links.json");
                    //остановка процесса по удалению просроченных ссылок
                    manager.scheduler.shutdown();
                    break label;
                default:
                    System.out.println("Неизвестная команда!");
                    break;
            }
        }
        scanner.close();
    }

    //меню управления ссылками
    public static void toRun(User activeUser) {
        label:
        while(true) {
            System.out.println(activeUser.getLogin() + ": \n" +
                    "Введите \"1\", для просмотра ранее созданных коротких ссылок;\n" +
                    "Введите \"2\", для создания новой короткой ссылки;\n" +
                    "Введите \"3\", для перехода по короткой ссылке;\n" +
                    "Введите \"4\", для редактирования ссылок\n" +
                    "Введите \"0\", для выхода из профиля;\n");
            String cmd = scanner.nextLine();
            switch (cmd) {
                case "1":
                    manager.toPrintLinksKey();
                    break;
                case "2":
                    toCreateShortLink(activeUser);
                    break;
                case "3":
                    toRedirect();
                    break;
                case "4":
                    toEditLink(activeUser);
                    break;
                case "0":
                    break label;
                default:
                    System.out.println("Неизвестная команда!");
                    break;
            }
        }
    }

    //меню редактирования и удаления ссылок
    public static void toEditLink(User activeUser) {
        String shortLink;
        label:
        while(true) {
            System.out.println(activeUser.getLogin() + ": \n" +
                    "Введите \"1\", для изменения количества переходов;\n" +
                    "Введите \"2\", для изменения срока действия ссылки;\n" +
                    "Введите \"3\", для удаления ссылки;\n" +
                    "Введите \"0\", для переходы в предыдущее меню;\n");
            String cmd = scanner.nextLine();

            switch (cmd) {
                case "1":
                    shortLink = toReadShortLink();
                    manager.toEditRedirect(shortLink, activeUser);
                    break;
                case "2":
                    shortLink = toReadShortLink();
                    manager.toEditExpiryTime(shortLink, activeUser);
                    break;
                case "3":
                    shortLink = toReadShortLink();
                    manager.toRemove(shortLink, activeUser);
                    break;
                case "0":
                    break label;
                default:
                    System.out.println("Неизвестная команда!");
                    break;
            }
        }
    }

    public static void toRegister() {
        System.out.println("\nВведите логин: ");
        String log = scanner.nextLine();

        System.out.println("\nВведите пароль: ");
        String pass = scanner.nextLine();

        //проверка на наличие пользователя с таким же именем
        boolean isBe = false;
        for (User user : users) {
            if (log.equals(user.getLogin())) {
                System.out.println("\nПользователь с таким именем уже существует!\n");
                isBe = true;
                break;
            }
        }
        if (!isBe) {
            User user = new User(log, pass);
            users.add(user);
        }
    }

    public static void toPrintUsers(List<User> users) {
        if (!users.isEmpty()) {
            for (User user : users) {
                user.toPrint();
            }
        } else {
            System.out.println("Зарегистрированных пользователей не найдено!");
        }
        System.out.println("\n");
    }

    public static User toAuthorization() {
        System.out.println("\nВведите логин: ");
        String log = scanner.nextLine();

        System.out.println("\nВведите пароль: ");
        String pass = scanner.nextLine();
        User activUser = null;
        boolean isBe = false;

        for (User user : users) {
            if (log.equals(user.getLogin()) && pass.equals(user.getPassword())) {
                System.out.println("\nПользователь найден!\n");
                activUser = user;
                isBe = true;
                break;
            }
        }
        if(!isBe) {System.out.println("Пользователя с таким именем и паролем не найдено!");}
        return activUser;
    }

    public static void toCreateShortLink(User activeUser) {
        String longUrl = manager.toReadOriginalUrl();



        // Создаем короткую ссылку
        String shortUrl = manager.createShortLink(longUrl, activeUser);
        System.out.println("Создана короткая ссылка: " + shortUrl);
    }

    public static String toReadShortLink() {
        System.out.println("\nВведите короткую ссылку: ");
        String strShortLink = scanner.nextLine();
        return strShortLink.replace("clck.ru/", "");
    }

    public static void toRedirect() {

        String shortUrl = toReadShortLink();
        if (manager.getLinks().containsKey(shortUrl)) {
            ShortLink shortLink = manager.getLinks().get(shortUrl);
            if (!shortLink.isExpired()) {
                if (shortLink.canRedirect()) {
                    System.out.println("Переход по короткой ссылке...");
                    String original = manager.getOriginalUrl(shortUrl);
                    System.out.println("Редирект на: " + original);
                    System.out.println("Переходов по ссылке: ");
                    try {
                        Desktop.getDesktop().browse(new URI(original));
                    } catch (URISyntaxException | IOException e) {
                        System.out.println("Что то пошло не так" + e.getMessage());
                    }
                } else {
                    System.out.println("Достигнут лимит переходов по ссылке, обратитесь к владельцу ссылки для увеличения лимита.");
                }
            } else {
                manager.getLinks().remove(shortUrl);
                System.out.println("Срок действия ссылки истек, ссылка больше не доступна и после получения этого сообщения была удалена!");
            }
        } else {
            System.out.println("Ссылка не найдена!");
        }
    }
}


