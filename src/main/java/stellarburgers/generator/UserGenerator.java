package stellarburgers.generator;

import stellarburgers.model.User;

import java.util.UUID;

public class UserGenerator {

    public static User getRandomUser() {
        String random = UUID.randomUUID()
                .toString()
                .replace("-", "");

        return new User(
                "user_" + random + "@yandex.ru",
                "password123",
                "Anna"
        );
    }
}