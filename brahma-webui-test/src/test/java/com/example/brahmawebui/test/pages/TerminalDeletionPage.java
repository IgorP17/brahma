package com.example.brahmawebui.test.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object для удаления терминалов.
 */
public class TerminalDeletionPage {

    private SelenideElement deleteInput = $("#delete-id");
    private SelenideElement deleteFromBothBtn = $(".delete-btn", 0);
    private SelenideElement deleteFromGatewayBtn = $(".delete-btn", 2);
    private SelenideElement deleteFromProcessorBtn = $(".delete-btn", 1);
    private SelenideElement deleteResult = $("#delete-result");

    public void deleteTerminalFromBoth(String terminalId) {
        deleteInput.clear();
        deleteInput.setValue(terminalId);
        deleteFromBothBtn.click();
    }

    public void deleteTerminalFromGateway(String terminalId) {
        deleteInput.clear();
        deleteInput.setValue(terminalId);
        deleteFromGatewayBtn.click();
    }

    public void deleteTerminalFromProcessor(String terminalId) {
        deleteInput.clear();
        deleteInput.setValue(terminalId);
        deleteFromProcessorBtn.click();
    }

    public void assertDeletionSuccess() {
        deleteResult.should(appear).shouldHave(com.codeborne.selenide.Condition.text("✅"));
    }

    public void assertDeletionError() {
        deleteResult.should(appear).shouldHave(com.codeborne.selenide.Condition.text("❌"));
    }

    public String getDeletionResultText() {
        return deleteResult.getText();
    }
}
