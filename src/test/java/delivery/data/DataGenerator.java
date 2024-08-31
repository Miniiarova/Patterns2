package delivery.data;

import com.github.javafaker.Faker;
import lombok.Value;

public class DataGenerator {

    private DataGenerator(){
    }

    public static String generateLogin() {
        Faker faker = new Faker();
        return faker.name().username();
    }

    public static String generatePassword() {
        Faker faker = new Faker();
        return faker.internet().password();
    }

    public static class Registration {
        private Registration() {
        }
        public static UserInfo generateUser() {
            return new UserInfo(generateLogin(), generatePassword());
        }
    }

    @Value
    public static class UserInfo{
        String login;
        String password;
    }
}
