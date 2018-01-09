package org.mel;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Test
    public void testEcho() {
        System.out.println("Here U R");
        logger.debug("Here U R");
    }
}
