package org.example.stepdefs;

import io.cucumber.java.After;

public class Hooks {

    private final SharedContext ctx;

    public Hooks(SharedContext ctx) {
        this.ctx = ctx;
    }

    @After
    public void tearDown() {
        ctx.tearDown();
    }
}
