package com.wangzan101.mika.core.agent

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdvisorTurnTest {
    @Test fun advisorPhrases() {
        assertTrue(AdvisorTurn.isAdvisorWords("这条怎么回"))
        assertTrue(AdvisorTurn.isAdvisorWords("帮我回复一下"))
        assertTrue(AdvisorTurn.isAdvisorWords("给个回复建议"))
        assertTrue(AdvisorTurn.isAdvisorWords("How should I reply?"))
    }
    @Test fun nonAdvisor() {
        assertFalse(AdvisorTurn.isAdvisorWords(""))
        assertFalse(AdvisorTurn.isAdvisorWords("把这单提取成表格"))
        assertFalse(AdvisorTurn.isAdvisorWords("每天早上盯一下价格"))
        assertFalse(AdvisorTurn.isAdvisorWords("帮我发个消息给张三"))
        assertFalse(AdvisorTurn.isAdvisorWords("总结一下这个页面"))
    }
}
