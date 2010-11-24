package com.atlassian.refapp.sal.pluginsettings;

import junit.framework.TestCase;

public class TestXmlUtils extends TestCase
{
    public void testEscape()
    {
        // no special code to be escaped.
        assertEquals("hello world", XmlUtils.escape("hello world"));

        // null input should return null
        assertEquals(null, XmlUtils.escape(null));

        // special chars should be escaped but \n is legit in XML so it doesn't have to be escaped.
        assertEquals("hello!@#$000Cworld\nyeehaw!@#$0008hoho", XmlUtils.escape("hello\fworld\nyeehaw\bhoho"));
    }

    public void testUnescape()
    {
        // no escape should just return the input.
        assertEquals("hello world", XmlUtils.unescape("hello world"));

        // null input should return null
        assertEquals(null, XmlUtils.unescape(null));

        // this should deescape all the special sequences.
        assertEquals("hello\fworld\nyeehaw\bhoho", XmlUtils.unescape("hello!@#$000Cworld\nyeehaw!@#$0008hoho"));

        // this should not get deescape since the escape sequence is incomplete.
        assertEquals("hello!@#000Cworld", XmlUtils.unescape("hello!@#000Cworld"));

        // this should not get deescape since the escape sequence is incomplete.
        assertEquals("hello!@#$000%world", XmlUtils.unescape("hello!@#$000%world"));

        // this should not get deescape since the escape sequence is incomplete.
        assertEquals("hello!@ !@# !@#$ !! @@ ## $$ !@#$54", XmlUtils.unescape("hello!@ !@# !@#$ !! @@ ## $$ !@#$54"));

        // if the first escaping symbol is the last character in string. it should not try to unescape or crash.
        assertEquals("hello!", XmlUtils.unescape("hello!"));
    }
}
