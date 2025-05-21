// Created: 17 Mai 2025
package de.freese.base.persistence.formatter;

import java.util.regex.Pattern;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public final class SqlFormatter {
    private static final Pattern PATTERN_LINE_BREAKS = Pattern.compile("(\\r\\n|\\r|\\n)");
    private static final Pattern PATTERN_SPACES = Pattern.compile("\\s{2,}");

    public static String format(final CharSequence sql) {
        String value = sql.toString();
        value = PATTERN_LINE_BREAKS.matcher(value).replaceAll(" ");
        value = PATTERN_SPACES.matcher(value).replaceAll(" ");
        value = value
                .replace("( ", "(")
                .replace(" )", ")");

        final String valueLowerCase = value.toLowerCase();

        if (valueLowerCase.startsWith("create") || valueLowerCase.startsWith("drop") || valueLowerCase.startsWith("alter")) {
            return FormatStyle.DDL.getFormatter().format(value);
        }

        return FormatStyle.BASIC.getFormatter().format(value);
    }

    public static void log(final CharSequence sql, final Logger logger) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        final String formatted = format(sql);

        logger.debug("{}", formatted);
    }

    private SqlFormatter() {
        super();
    }
}
