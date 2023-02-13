package de.freese.base.core.regex;

import java.util.ArrayList;
import java.util.List;

import de.freese.base.core.regex.transformer.EndsWithTransformer;
import de.freese.base.core.regex.transformer.EscapeBracketTransformer;
import de.freese.base.core.regex.transformer.EscapeSlashesTransformer;
import de.freese.base.core.regex.transformer.ManyPointsTransformer;
import de.freese.base.core.regex.transformer.ManySignsTransformer;
import de.freese.base.core.regex.transformer.RegExTransformer;
import de.freese.base.core.regex.transformer.SingleSignTransformer;
import de.freese.base.core.regex.transformer.StartsWithTransformer;

/**
 * @author Thomas Freese
 */
public final class RegExToWildcard implements RegExTransformer {
    private static final RegExTransformer INSTANCE = new RegExToWildcard();

    public static RegExTransformer getInstance() {
        return INSTANCE;
    }

    private final List<RegExTransformer> transformers;

    private RegExToWildcard() {
        super();

        this.transformers = new ArrayList<>();

        this.transformers.add(new EscapeBracketTransformer());
        this.transformers.add(new EscapeSlashesTransformer());
        this.transformers.add(new SingleSignTransformer());
        this.transformers.add(new ManySignsTransformer());
        this.transformers.add(new ManyPointsTransformer());
        this.transformers.add(new StartsWithTransformer());
        this.transformers.add(new EndsWithTransformer());
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex) {
        String expression = regex;

        if (expression == null) {
            return null;
        }

        if ("".equals(expression.strip())) {
            return "";
        }

        for (RegExTransformer transformer : this.transformers) {
            expression = transformer.regExToWildcard(expression);
        }

        return expression;
    }

    /**
     * Wenn ein '@' am Anfang enthalten ist, wird der Ausdruck unverändert zurückgeliefert und das '@' entfernt.
     *
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard) {
        String expression = wildcard;

        // .* als default IST NICHT ZULÄSSIG !!!
        if (expression == null) {
            return null;
        }

        // .* als default IST NICHT ZULÄSSIG !!!
        if ("".equals(expression.strip())) {
            return "";
        }

        if (expression.startsWith("@")) {
            return expression.substring(1);
        }

        for (RegExTransformer transformer : this.transformers) {
            expression = transformer.wildcardToRegEx(expression);
        }

        return expression;
    }
}
