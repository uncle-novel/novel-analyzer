package com.unclezs.novel.core.request;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import org.junit.Test;

/**
 * @author blog.unclezs.com
 * @since 2020/12/24 10:42
 */
public class DynamicSiteTest {
    @Test
    public void test() {
        WebEngine engine = new WebEngine();
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println(engine.getDocument().toString());
            }
        });
        engine.load("https://blog.unclezs.com");
    }
}
