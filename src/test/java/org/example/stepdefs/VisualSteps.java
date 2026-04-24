package org.example.stepdefs;

import io.cucumber.java.en.Then;
import org.example.utils.VisualCompareUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class VisualSteps {

    private final SharedContext ctx;

    public VisualSteps(SharedContext ctx) {
        this.ctx = ctx;
    }

    @Then("the page should match the visual baseline {string}")
    public void thePageShouldMatchTheVisualBaseline(String baselineName) {
        assertThat(VisualCompareUtil.compareScreenshot(ctx.page, baselineName))
                .as("Visual diff for " + baselineName + " exceeded threshold")
                .isTrue();
    }
}
