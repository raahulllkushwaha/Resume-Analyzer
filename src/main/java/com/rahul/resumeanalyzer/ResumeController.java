package com.rahul.resumeanalyzer;

import org.apache.tika.Tika;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin("*")
public class ResumeController {

    private final ChatClient chatClient;

    private final Tika tika = new Tika();

    public ResumeController(OpenAiChatModel openAiChatModel){
        this.chatClient=ChatClient.create(openAiChatModel);
    }

    public Map<String, Object> analyzer(@RequestParam("file") MultipartFile file) throws Exception{
        String content = tika.parseToString(file.getInputStream());
        String prompt = """
                Analyze this resume text:
                %s
                1. Extract key skills
                2. Rate overall resume quality (1-10)
                3. Suggest 5 improvments
                Reply in structured JSON format.
                """.formatted(content);

        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return Map.of("analysis", aiResponse);
    }
}
