package delivery.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import static io.restassured.RestAssured.given;

public class ApiFactory {
    private final static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private ApiFactory() {
    }

    public static void setupApi(DataGenerator.UserInfo userInfo, boolean active) {
        RegistrationDto registrationDto = new RegistrationDto(
                userInfo.getLogin(),
                userInfo.getPassword(),
                active ? "active" : "blocked"
        );

        given() // "дано"
            .spec(requestSpec) // указываем, какую спецификацию используем
            .body(registrationDto) // передаём в теле объект, который будет преобразован в JSON
        .when() // "когда"
            .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
            .then() // "тогда ожидаем"
            .statusCode(200); // код 200 OK
    }

    @Value
    public static class RegistrationDto{
        String login;
        String password;
        String status;
    }
}
