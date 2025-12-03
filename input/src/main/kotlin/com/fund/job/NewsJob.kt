package com.fund.job

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.fund.investing.InvestingClient
import com.fund.modules.news.model.StockNews
import com.fund.modules.news.service.StockNewsService
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NewsJob (
    private var investingClient: InvestingClient,
    private var stockNewsService: StockNewsService
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 新闻数据类
     */
    data class NewsItem(
        val title: String,
        val provider: String,
        val link: String,
        val content: String? = null  // 新闻详情内容
    )

    @Scheduled(initialDelay = 100, fixedDelay = 1000 * 60)
    fun loadNews() {
        try {
            logger.info { "Loading news..." }
            val html = investingClient.loadData("https://www.investing.com/news/stock-market-news")

            // 使用 jsoup 解析 HTML
            val doc: Document = Jsoup.parse(html)

            // 查找所有包含 data-test="article-title-link" 的链接元素
            val newsItems = mutableListOf<NewsItem>()

            // 查找所有文章项（通过 article 标签或包含标题链接的容器）
            val articleElements = doc.select("article[data-test=article-item], article.news-analysis-v2_article__wW0pT")

            articleElements.forEach { article ->
                try {
                    // 查找标题链接（优先使用 data-test="article-title-link"，如果没有则查找包含特定 class 的链接）
                    val titleLink = article.select("a[data-test=article-title-link]").first()
                        ?: article.select("a.whitespace-normal.text-sm.font-bold").first()
                        ?: article.select("a.text-primary, a[class*=text-primary]").first()

                    if (titleLink != null) {
                        val title = titleLink.text().trim()
                        val link = titleLink.attr("href")

                        // 处理相对链接，转换为绝对链接
                        val fullLink = if (link.startsWith("http")) {
                            link
                        } else {
                            "https://www.investing.com$link"
                        }

                        // 查找新闻来源
                        val providerElement = article.select("span[data-test=news-provider-name]").first()
                        val provider = providerElement?.text()?.trim() ?: "Unknown"

                        if (title.isNotBlank() && fullLink.isNotBlank()) {
                            newsItems.add(NewsItem(title, provider, fullLink))
                        }
                    }
                } catch (e: Exception) {
                    logger.warn(e) { "Error parsing article element" }
                }
            }

            logger.info { "Successfully parsed ${newsItems.size} news items" }

            // 遍历每个新闻，获取详情内容
            val newsWithContent = newsItems.mapIndexed { index, news ->
                try {
                    logger.info { "Loading detail for news ${index + 1}/${newsItems.size}: ${news.title}" }
                    val content = loadNewsDetail(news.link)
                    news.copy(content = content)
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to load detail for news: ${news.title}" }
                    news.copy(content = null)
                }
            }

            // 打印前几条新闻用于验证
            newsWithContent.take(5).forEach { news ->
                logger.info { "News: ${news.title} | Provider: ${news.provider} | Link: ${news.link}" }
                if (news.content != null) {

                } else {
                    logger.warn { "Content is null for news: ${news.title}" }
                }
            }

            // 保存新闻数据到数据库（使用已获取详情内容的 newsWithContent）
            for (newsItem in newsWithContent) {
                val count = stockNewsService.count(
                    KtQueryWrapper(StockNews())
                        .eq(StockNews::link, newsItem.link)
                )
                if (count == 0L) {
                    val stockNews = StockNews()
                    stockNews.title = newsItem.title
                    stockNews.provider = newsItem.provider
                    stockNews.link = newsItem.link
                    stockNews.content = newsItem.content
                    stockNews.createTime = LocalDateTime.now()
                    stockNews.updateTime = LocalDateTime.now()

                    stockNewsService.save(stockNews)
                   // logger.info { "Saved news: ${newsItem.title}, content length: ${newsItem.content?.length ?: 0}" }
                } else {
                    logger.debug { "News already exists: ${newsItem.link}" }
                }
            }

        } catch (e: Exception) {
            logger.error(e) { "Error loading news" }
        }
    }

    /**
     * 加载新闻详情内容
     * @param newsLink 新闻链接
     * @return 新闻详情内容（保留 p 标签的 HTML 结构）
     */
    private fun loadNewsDetail(newsLink: String): String? {
        return try {
            // 请求新闻详情页面
            val detailHtml = investingClient.loadData(newsLink)

            // 使用 jsoup 解析详情页面
            val detailDoc: Document = Jsoup.parse(detailHtml)

            // 尝试多种选择器策略查找包含文章内容的 div
            val articleDiv = detailDoc.select("div.article_WYSIWYG__O0uhw.article_articlePage__UMz3q").first()
                ?: detailDoc.select("div[class*=article_WYSIWYG__O0uhw][class*=article_articlePage__UMz3q]").first()
                ?: detailDoc.select("div.article_WYSIWYG__O0uhw").first()
                ?: detailDoc.select("div[class*=article_WYSIWYG]").first()
                ?: detailDoc.select("div[class*=articlePage]").first()
                ?: detailDoc.select("article div").first()

            if (articleDiv != null) {
                // 提取所有 p 标签，保留 HTML 结构
                val paragraphs = articleDiv.select("p")
                val content = paragraphs.map { it.outerHtml().trim() }
                    .filter { it.isNotBlank() }
                    .joinToString("<br/>")

                if (content.isNotBlank()) {
                    logger.debug { "Successfully extracted content with p tags from: $newsLink, paragraphs: ${paragraphs.size}, content length: ${content.length}" }
                    content
                } else {
                    // 如果 p 标签为空，尝试提取 div 内的所有 HTML（保留标签）
                    val allHtml = articleDiv.html().trim()
                    if (allHtml.isNotBlank()) {
                        logger.debug { "No p tags found, extracted all HTML from div: $newsLink, length: ${allHtml.length}" }
                        allHtml
                    } else {
                        logger.warn { "No content found in article div for: $newsLink" }
                        // 调试：打印找到的 div 的 class 名称
                        logger.debug { "Found div classes: ${articleDiv.classNames()}" }
                        null
                    }
                }
            } else {
                logger.warn { "Article content div not found for: $newsLink" }
                // 调试：打印页面中所有包含 article 的 div 的 class
                val allArticleDivs = detailDoc.select("div[class*=article], article")
                logger.debug { "Found ${allArticleDivs.size} article-related divs. Sample classes: ${allArticleDivs.take(5).map { it.classNames() }}" }
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error loading news detail from: $newsLink" }
            null
        }
    }

}