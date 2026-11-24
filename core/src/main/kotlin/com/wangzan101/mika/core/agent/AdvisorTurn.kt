package com.wangzan101.mika.core.agent

/**
 * 随办顾问单次路径 (M1-P1)。
 * 双击胶囊发来的屏幕 + 回复顾问类问句:第一轮不给工具,模型只能直接回答,
 * 走 AgentLoop 现有的「无调用收尾」,避免观察→决策→动作的完整循环。
 * M0 实测:顾问问题进完整循环约 15 轮/次、输入约 195k token;本路径目标 1–3 轮。
 * 关键词保守匹配:只命中明确的「求回复建议」句式;执行类请求(提取/巡检/委托)不受影响。
 */
object AdvisorTurn {
    private val KEYWORDS = listOf(
        "怎么回", "怎样回", "如何回", "帮我回", "帮忙回", "回复建议", "回什么", "回啥",
        "怎么接话", "这条怎么", "该怎么回", "怎么回复", "如何回复",
        "how to reply", "how should i reply", "suggest a reply", "reply suggestion",
    )

    /** 用户附言是否是明确的回复顾问请求。空话(只发屏幕)不算:那按屏幕猜意图的规矩交给主模型。 */
    fun isAdvisorWords(words: String): Boolean {
        val w = words.trim().lowercase()
        if (w.isEmpty() || w.length > 80) return false
        return KEYWORDS.any { w.contains(it) }
    }
}
