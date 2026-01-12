package port;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LinkManagerTest {

    private LinkManager linkManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Создаем LinkManager и тестового пользователя
        linkManager = new LinkManager();

        // Пользователь для тестов
        testUser = new User("testUser", "testUserPass");
    }

    @AfterEach
    void tearDown() {
        // Очищаем все ссылки после каждого теста
        linkManager.getLinks().clear();
    }

    @Test
    @DisplayName("Создание короткой ссылки добавляет запись в map")
    void testCreateShortLinkAddsLink() {
        String originalUrl = "https://example.com";
        String shortCode = linkManager.createShortLink(originalUrl, testUser);

        assertNotNull(shortCode, "Сгенерированный код не должен быть null");
        assertTrue(linkManager.getLinks().containsKey(shortCode), "Map должен содержать созданную ссылку");
        assertEquals(originalUrl, linkManager.getLinks().get(shortCode).getOriginalUrl(),
                "Оригинальный URL должен совпадать");
    }

    @Test
    @DisplayName("Получение оригинального URL увеличивает счетчик переходов")
    void testGetOriginalUrlIncrementsRedirects() {
        String originalUrl = "https://example.com";
        String shortCode = linkManager.createShortLink(originalUrl, testUser);

        // Сначала редиректов должно быть 0
        assertEquals(0, linkManager.getLinks().get(shortCode).getCurrentRedirects());

        String retrievedUrl = linkManager.getOriginalUrl(shortCode);
        assertEquals(originalUrl, retrievedUrl, "Должен вернуть оригинальный URL");

        // После запроса счетчик редиректов увеличивается
        assertEquals(1, linkManager.getLinks().get(shortCode).getCurrentRedirects());
    }

    @Test
    @DisplayName("Проверка прав пользователя возвращает true для владельца")
    void testToRightsCheckOwner() {
        String shortCode = linkManager.createShortLink("https://example.com", testUser);
        assertTrue(linkManager.toRightsCheck(shortCode, testUser), "Владелец должен иметь права");
    }

    @Test
    @DisplayName("Удаление ссылки работает только для владельца")
    void testToRemoveOnlyOwner() {
        String shortCode = linkManager.createShortLink("https://example.com", testUser);

        User otherUser = new User("otherUser", "otherUserPass");

        // Другой пользователь не может удалить
        linkManager.toRemove(shortCode, otherUser);
        assertTrue(linkManager.getLinks().containsKey(shortCode), "Ссылка не должна быть удалена чужим пользователем");

        // Владелец может удалить
        linkManager.toRemove(shortCode, testUser);
        assertFalse(linkManager.getLinks().containsKey(shortCode), "Ссылка должна быть удалена владельцем");
    }

    @Test
    @DisplayName("Создание нескольких коротких ссылок генерирует уникальные коды")
    void testUniqueShortCodes() {
        String shortCode1 = linkManager.createShortLink("https://example1.com", testUser);
        String shortCode2 = linkManager.createShortLink("https://example2.com", testUser);

        assertNotEquals(shortCode1, shortCode2, "Коды должны быть уникальными");
    }
}