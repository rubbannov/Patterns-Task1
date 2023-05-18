package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $x("//*[@data-test-id='city']//input[@placeholder='Город']").setValue(validUser.getCity());
        $x("//*[@data-test-id='date']//input[@placeholder='Дата встречи']").sendKeys(Keys
                .chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//*[@data-test-id='date']//input[@placeholder='Дата встречи']").setValue(firstMeetingDate);
        $x("//*[@data-test-id='name']//input[@name='name']").setValue(validUser.getName());
        $x("//*[@data-test-id='phone']//input[@type='tel']").setValue(validUser.getPhone());
        $x("//*[@data-test-id='agreement']").click();
        $$("[type='button']").filter(visible).last().click();
        $x("//*[contains(text(), 'Успешно')]").shouldBe(visible, Duration.ofSeconds(11));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));

        // Replanning

        $x("//button[@class = 'icon-button icon-button_size_m icon-button_theme_alfa-on-white notification__closer']")
                .click();
        $x("//*[@data-test-id='date']//input[@placeholder='Дата встречи']").sendKeys(Keys
                .chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//*[@data-test-id='date']//input[@placeholder='Дата встречи']").setValue(secondMeetingDate);
        $$("[type='button']").filter(visible).last().click();
        $("[data-test-id=replan-notification]")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $x("//span[text()='Перепланировать']").click();
        $("[data-test-id=success-notification]").shouldBe(Condition.visible, Duration.ofSeconds(11));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}