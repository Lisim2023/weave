package cn.filaura.weave.type;

import cn.filaura.weave.exception.ConvertException;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateConverter implements Converter<Date> {

    private final DateTimeFormatter formatter;
    private final ZoneId zoneId;



    /**
     * 构造一个新的 ZonedDateTimeConverter 实例，默认时区为系统默认时区。
     *
     * @param pattern 日期时间格式模式
     */
    public DateConverter(String pattern) {
        this(pattern, ZoneId.systemDefault());
    }

    /**
     * 构造一个新的 ZonedDateTimeConverter 实例。
     *
     * @param pattern 日期时间格式模式
     * @param zoneId  时区标识符
     */
    public DateConverter(String pattern, ZoneId zoneId) {
        if (pattern == null || zoneId == null) {
            throw new IllegalArgumentException("Pattern and zoneId cannot be null");
        }
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.zoneId = zoneId;
    }



    @Override
    public Date convert(String source) throws ConvertException {
        if (source == null) return null;

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(source, formatter.withZone(zoneId));
            return Date.from(zonedDateTime.toInstant());
        } catch (DateTimeParseException e) {
            throw new ConvertException("Unparseable date: \"" + source + "\"", e);
        }
    }

}
