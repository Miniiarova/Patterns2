package delivery.test;

import delivery.data.ApiFactory;
import delivery.data.DataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;

public class LoginTest {
    @BeforeEach
    void setUpEach() {
        open("http://localhost:9999");
    }

    @AfterEach
    void cleanUpEach() {
        closeWebDriver();
    }

    @Test
    void shouldLoginWithValidCredentials() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, true);

        $("[data-test-id=login] input").setValue(userInfo.getLogin());
        $("[data-test-id=password] input").setValue(userInfo.getPassword());
        $("[data-test-id=action-login]").click();

        assertLoggedInSuccessfully();
    }

    @Test
    void shouldFailWithWrongLogin() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, true);

        $("[data-test-id=login] input").setValue("not-exists-123");
        $("[data-test-id=password] input").setValue(userInfo.getPassword());
        $("[data-test-id=action-login]").click();

        assertLoginFailed("Неверно указан логин или пароль");
    }

    @Test
    void shouldFailWithWrongPassword() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, true);

        $("[data-test-id=login] input").setValue(userInfo.getLogin());
        $("[data-test-id=password] input").setValue("invalid-password-123");
        $("[data-test-id=action-login]").click();

        assertLoginFailed("Неверно указан логин или пароль");
    }

    @Test
    void shouldFailForInactiveUser() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, false);

        $("[data-test-id=login] input").setValue(userInfo.getLogin());
        $("[data-test-id=password] input").setValue(userInfo.getPassword());
        $("[data-test-id=action-login]").click();

        assertLoginFailed("Пользователь заблокирован");
    }

    @Test
    void shouldFailForInactiveUserAndWrongLogin() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, false);

        $("[data-test-id=login] input").setValue("not-exists");
        $("[data-test-id=password] input").setValue(userInfo.getPassword());
        $("[data-test-id=action-login]").click();

        assertLoginFailed("Неверно указан логин или пароль");
    }

    @Test
    void shouldFailForInactiveUserAndWrongPassword() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, false);

        $("[data-test-id=login] input").setValue(userInfo.getLogin());
        $("[data-test-id=password] input").setValue("invalid-password-123");
        $("[data-test-id=action-login]").click();

        assertLoginFailed("Неверно указан логин или пароль");
    }

    @Test
    void shouldShowValidationErrorWithoutCredentials() {
        $("[data-test-id=action-login]").click();

        $("[data-test-id=login]")
                .shouldHave(cssClass("input_invalid"))
                .find(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text("Поле обязательно для заполнения"));

        $("[data-test-id=password]")
                .shouldHave(cssClass("input_invalid"))
                .find(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldShowValidationErrorWithNoPassword() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, false);

        $("[data-test-id=login] input").setValue(userInfo.getLogin());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=password]")
                .shouldHave(cssClass("input_invalid"))
            .find(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldShowValidationErrorWithNoLogin() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser();
        ApiFactory.setupApi(userInfo, false);

        $("[data-test-id=password] input").setValue(userInfo.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=login]")
                .shouldHave(cssClass("input_invalid"))
                .find(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    private void assertLoginFailed(String errorText) {
        webdriver().shouldHave(url("http://localhost:9999/"));

        $("[data-test-id=error-notification]")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text(errorText));

        $("h2.heading")
                .shouldNotHave(text("Личный кабинет"));
    }

    private void assertLoggedInSuccessfully() {
        webdriver().shouldHave(url("http://localhost:9999/dashboard"));

        $("h2.heading")
                .shouldBe(visible, Duration.ofSeconds(1))
                .shouldHave(text("Личный кабинет"));
    }
}
