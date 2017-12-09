package com.keyboard3;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String source = "地方 LableView 地 ";
        List<String> words = WordUtil.getWords(source);
        for (String item :
                words) {
            System.out.println(item);
        }
    }
}