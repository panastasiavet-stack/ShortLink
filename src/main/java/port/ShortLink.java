package alexuuport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShortLink {
    private String originalUrl;
    private String shortCode;
    private User user;
    private int maxRedirects;
    private int currentRedirects;
    private LocalDateTime creationTime;
    private LocalDateTime expiryTime;

    public ShortLink(String originalUrl, String shortCode, User user, int maxRedirects, int lifespanHours) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.user = user;
        this.maxRedirects = maxRedirects;
        this.currentRedirects = 0;
        this.creationTime = LocalDateTime.now();
        this.expiryTime = creationTime.plusHours(lifespanHours);
    }

    public ShortLink() {}

    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    @JsonIgnore
    public boolean canRedirect() {
        return !isExpired() && (currentRedirects < maxRedirects);
    }

    public void incrementRedirects() {
        currentRedirects++;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public String getShortCode() {
        return this.shortCode;
    }

    public User getUser() {
        return this.user;
    }

    public int getMaxRedirects() {
        return this.maxRedirects;
    }

    public int getCurrentRedirects() {
        return this.currentRedirects;
    }

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    public LocalDateTime getExpiryTime() {
        return this.expiryTime;
    }

    public void setMaxRedirects(int value) {
        if (value > 0) {
            this.maxRedirects = value;
        } else {System.out.println("Ошибка, значение должно быть больше \"0\" ");}
    }

    public void setExpiryTime(LocalDateTime dateTime) {
        this.expiryTime = dateTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return "Оригинальный URL: " + this.originalUrl + "\nКороткая ссылка: clck.ru/" + this.shortCode + "\nПользователь: " + this.user.getLogin() +
                "\nМаксимальное разрешенное количество переходов: " +  this.maxRedirects + "\nСовершенных переходов: " + this.currentRedirects +
                "\nДата создания: " + this.creationTime.format(formatter) + "\nПродолжительность существования до: " + this.expiryTime.format(formatter) +
                "\n";
    }

    public void toPrint() {
        System.out.println(toString());
    }

}