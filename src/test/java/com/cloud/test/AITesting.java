//package com.example.practice;
//
//import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
//import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
//import com.alibaba.cloud.ai.graph.OverAllState;
//import com.alibaba.cloud.ai.graph.agent.ReactAgent;
//import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
//import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
//import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
//import com.alibaba.cloud.ai.graph.exception.GraphStateException;
//import com.example.practice.utils.ai.WeatherTool;
//import com.example.practice.utils.ai.WebPageFetcherTool;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.messages.AssistantMessage;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.tool.ToolCallback;
//import org.springframework.ai.tool.function.FunctionToolCallback;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.Optional;
//
//
//@SpringBootTest
//public class AITesting {
//
//    @Value("${spring.ai.dashscope.api-key}")
//    private String apiKey;
//
//    @Test
//    public void testAI() throws GraphRunnerException {
//        // 初始化ChatModel
//        DashScopeApi dashScopeApi = DashScopeApi.builder()
//                .apiKey(apiKey)
//                .build();
//        ChatModel chatModel = DashScopeChatModel.builder()
//                .dashScopeApi(dashScopeApi)
//                .build();
//
//        // 定义天气查询工具
//        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
//                .description("Get weather for a given city")
//                .inputType(String.class)
//                .build();
//
//        // 定义查询页面源码的工具
//        ToolCallback getWebPageFetcher = FunctionToolCallback.builder("get_web_page_source", new WebPageFetcherTool())
//                .description("一个可以帮助你获取网页源码的工具")
//                .inputType(String.class)
//                .build();
//
//        // 创建agent
//        String instruction = """
//          你是一个经验丰富的自动化测试工程师， 非常擅长Xpath定位， 并且分析网页源码和DOM节点，以及JSON数据格式。
//          在后面的请求中， 你只需要返回我JSON格式的数据， 例如：
//            { name: "登陆名输入框", xpath: "//input[@id=username]" }
//
//          限制： 不要废话，以及其他内容， 只回复相应的JSON数据。
//          """;
//        String customSchema = """
//          请严格按照以下JSON格式返回结果：
//          {
//              "name": "元素的名称",
//              "xpath": "返回元素的Xpath定位方式",
//          }
//          """;
//        ReactAgent agent = ReactAgent.builder()
//                .name("Chat")
//                .model(chatModel)
//                .tools(weatherTool, getWebPageFetcher)
////                .instruction(instruction)
//                .outputSchema(customSchema)
//                .saver(new MemorySaver())
//                .build();
//
//        // 运行agent
//        String message = "请帮我获取网页中与登陆表单相关的元素： http://www.csh.moe.edu.cn/MOETC/index.jsp";
//        AssistantMessage response = agent.call(message);
//        System.out.println("=============================== AI Response ==================================");
//        System.out.println(response.getText());
//    }
//
//    @Test
//    public void testMultiAgent() throws GraphRunnerException, GraphStateException {
//        // 初始化ChatModel
//        DashScopeApi dashScopeApi = DashScopeApi.builder()
//                .apiKey(apiKey)
//                .build();
//        ChatModel chatModel = DashScopeChatModel.builder()
//                .dashScopeApi(dashScopeApi)
//                .build();
//
//        // 创建专业化的子Agent
//        ReactAgent writerAgent = ReactAgent.builder()
//                .name("writer_agent")
//                .model(chatModel)
//                .description("专业写作Agent")
//                .instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答。")
//                .outputKey("article") // [!code highlight]
//                .build();
//
//        ReactAgent reviewerAgent = ReactAgent.builder()
//                .name("reviewer_agent")
//                .model(chatModel)
//                .description("专业评审Agent")
//                .instruction("你是一个知名的评论家，擅长对文章进行评论和修改。" +
//                        "对于散文类文章，请确保文章中必须包含对于西湖风景的描述。" +
//                        "最终只返回修改后的文章，不要包含任何评论信息。")
//                .outputKey("reviewed_article") // [!code highlight]
//                .build();
//
//        // 创建顺序Agent
//        SequentialAgent blogAgent = SequentialAgent.builder() // [!code highlight]
//                .name("blog_agent")
//                .description("根据用户给定的主题写一篇文章，然后将文章交给评论员进行评论")
//                .subAgents(List.of(writerAgent, reviewerAgent)) // [!code highlight]
//                .build();
//
//        // 使用
//        Optional<OverAllState> result = blogAgent.invoke("帮我写一个100字左右的散文");
//
//        if (result.isPresent()) {
//            OverAllState state = result.get();
//
//            // 访问第一个Agent的输出
//            AssistantMessage article = (AssistantMessage) state.value("article").get();
//            System.out.println("原始文章: " + article.getText());
//
//            // 访问第二个Agent的输出
//            AssistantMessage reviewedArticle = (AssistantMessage) state.value("reviewed_article").get();
//            System.out.println("评审后文章: " + reviewedArticle.getText());
//        }
//    }
//
//    @Test
//    public void testStream()  throws GraphRunnerException, GraphStateException {
//        // 初始化ChatModel
//        DashScopeApi dashScopeApi = DashScopeApi.builder()
//                .apiKey(apiKey)
//                .build();
//        ChatModel chatModel = DashScopeChatModel.builder()
//                .dashScopeApi(dashScopeApi)
//                .build();
//
//        // 使用流式 API
//        Flux<ChatResponse> responseStream = chatModel.stream(
//                new Prompt("详细解释Spring Boot的自动配置原理")
//        );
//        // 订阅并处理流式响应
//        responseStream.subscribe(
//                chatResponse -> {
//                    String content = chatResponse.getResult()
//                            .getOutput()
//                            .getText();
//                    System.out.print(content);
//                },
//                error -> System.err.println("错误: " + error.getMessage()),
//                () -> System.out.println("流式响应完成")
//                );
//    }
//}
//
