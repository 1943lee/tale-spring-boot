package com.lcy.tale;

import com.lcy.tale.utils.DateKit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author lcy
 * @since 2018/12/6
 */
@Slf4j
public class CommonTest {
    @Test
    public void testDateUtil() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 11, 6, 16, 28, 0);

        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        boolean isToday = DateKit.isToday(date);
        log.info("{}", isToday);
        log.info("{}", Instant.now().getEpochSecond());
        log.info("{}", Instant.now().getNano());
    }
}
