package com.atlassian;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamResolver
{
    // pattern for ${variable}.
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$[{][a-zA-Z][a-zA-Z_0-9]*[}]");

    /***
     * Resolve parameters embedded in the input string in form of ${variable} into the actual values.
     *
     * @param input the input string.
     * @param params the map of parameter=>value.
     *
     * @return the resolved string.
     */
    public String resolve(final String input, final Map<String, String> params)
    {
        // early exit if no resolution is needed.
        if (params == null || params.size()==0)
        {
            return input;
        }

        // our scratch space for matching
        final StringBuilder sb = new StringBuilder(input);

        // match the variable patterns in the input string.
        final Matcher matcher = VARIABLE_PATTERN.matcher(sb);

        // keep matching
        int current = 0;
        while(matcher.find(current))
        {
            // parse the match
            String match = matcher.group();
            String variable = match.substring(2, match.length() - 1);

            // if the variable resolves to value
            if (params.containsKey(variable))
            {
                sb.replace(matcher.start(), matcher.end(), params.get(variable));
                current = matcher.start() + params.get(variable).length();
            }
            // if there is no matching variable name
            else
            {
                current = matcher.end();
            }
        }

        return sb.toString();
    }
}
