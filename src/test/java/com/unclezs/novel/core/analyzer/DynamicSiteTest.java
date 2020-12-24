package com.unclezs.novel.core.analyzer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable,
                Worker.State oldValue, Worker.State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println(engine.getDocument().toString());
                }
            }
        });
        engine.load("https://blog.unclezs.com");
    }
}
