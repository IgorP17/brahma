package com.example.brahmawebui.test.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object для регистрации терминалов.
 */
public class TerminalRegistrationPage {

    private SelenideElement httpIdInput = $("#id");
    private SelenideElement httpModelInput = $("#model");
    private SelenideElement httpLocationInput = $("#location");
    private SelenideElement httpRegisterButton = $("#register-form input[type='submit']");
    private SelenideElement httpResult = $("#register-result");

    private SelenideElement grpcIdInput = $("#grpc-id");
    private SelenideElement grpcModelInput = $("#grpc-model");
    private SelenideElement grpcLocationInput = $("#grpc-location");
    private SelenideElement grpcRegisterButton = $("#register-grpc-form input[type='submit']");
    private SelenideElement grpcResult = $("#grpc-result");

    public void registerTerminalViaHttp(String terminalId, String model, String location) {
        httpIdInput.clear();
        httpIdInput.setValue(terminalId);
        httpModelInput.clear();
        httpModelInput.setValue(model);
        httpLocationInput.clear();
        httpLocationInput.setValue(location);
        httpRegisterButton.click();
    }

    public void registerTerminalViaGrpc(String terminalId, String model, String location) {
        grpcIdInput.clear();
        grpcIdInput.setValue(terminalId);
        grpcModelInput.clear();
        grpcModelInput.setValue(model);
        grpcLocationInput.clear();
        grpcLocationInput.setValue(location);
        grpcRegisterButton.click();
    }

    public void assertHttpRegistrationSuccess() {
        httpResult.should(appear).shouldHave(com.codeborne.selenide.Condition.text("✅"));
    }

    public void assertGrpcRegistrationSuccess() {
        grpcResult.should(appear).shouldHave(com.codeborne.selenide.Condition.text("✅"));
    }

    public void assertRegistrationError() {
        httpResult.should(appear).shouldHave(com.codeborne.selenide.Condition.text("❌"));
    }

    public String getHttpResultText() {
        return httpResult.getText();
    }

    public String getGrpcResultText() {
        return grpcResult.getText();
    }
}
