package com.example.agrisense.service;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;
import com.example.agrisense.entity.dto.ThresholdDTO;
import com.example.agrisense.util.ThresholdHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeepSeekAiService implements AiService {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekAiService.class);

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Value("${deepseek.api-key:}")
    private String apiKey;

    @Value("${deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    public DeepSeekAiService() {
        this.objectMapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(java.util.concurrent.TimeUnit.SECONDS.toMillis(30),
                        java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(java.util.concurrent.TimeUnit.SECONDS.toMillis(30),
                        java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(java.util.concurrent.TimeUnit.SECONDS.toMillis(30),
                        java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public String getAdvice(SensorData sensorData, Crop crop, String growthStage) {
        try {
            String prompt = buildPrompt(sensorData, crop, growthStage);
            return callApi(prompt);
        } catch (Exception e) {
            logger.error("调用DeepSeek AI失败", e);
            return "AI服务暂时不可用，请稍后重试。";
        }
    }

    @Override
    public String getAdviceWithDevice(SensorData sensorData, Crop crop, Device device) {
        return getAdvice(sensorData, crop, device != null ? device.getGrowthStage() : null);
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    private String buildPrompt(SensorData sensorData, Crop crop, String growthStage) {
        ThresholdDTO thresholds = ThresholdHelper.getThresholds(crop, growthStage);

        Map<String, String> variables = new HashMap<>();
        variables.put("cropName", crop != null ? crop.getName() : "通用作物");
        variables.put("growthStage", growthStage != null ? growthStage : "通用");
        variables.put("currentTemp", String.valueOf(sensorData.getTemperature()));
        variables.put("currentHumidity", String.valueOf(sensorData.getHumidity()));

        variables.put("minTemp", String.valueOf(thresholds.getMinTemp()));
        variables.put("maxTemp", String.valueOf(thresholds.getMaxTemp()));
        variables.put("optimalTempMin", String.valueOf(thresholds.getOptimalTempMin()));
        variables.put("optimalTempMax", String.valueOf(thresholds.getOptimalTempMax()));
        variables.put("minHumidity", String.valueOf(thresholds.getMinHumidity()));
        variables.put("maxHumidity", String.valueOf(thresholds.getMaxHumidity()));
        variables.put("optimalHumidityMin", String.valueOf(thresholds.getOptimalHumidityMin()));
        variables.put("optimalHumidityMax", String.valueOf(thresholds.getOptimalHumidityMax()));
        variables.put("specialNotes", crop != null && crop.getSpecialNotes() != null ? crop.getSpecialNotes() : "");

        String template = getDefaultTemplate();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }

    private String getDefaultTemplate() {
        return """
                你是一位专业的农业专家。请根据以下信息给出简要的大棚管理建议：

                作物：{cropName}
                生长阶段：{growthStage}
                当前温度：{currentTemp}°C
                当前湿度：{currentHumidity}%

                最适温度范围：{optimalTempMin}-{optimalTempMax}°C
                可接受温度范围：{minTemp}-{maxTemp}°C
                最适湿度范围：{optimalHumidityMin}-{optimalHumidityMax}%
                可接受湿度范围：{minHumidity}-{maxHumidity}%

                特殊注意事项：{specialNotes}

                请：
                1. 对比当前值与目标值
                2. 给出具体操作建议（如"温度偏高2°C，建议开启侧窗通风"）
                3. 如有超出范围的情况，重点提醒
                4. 保持回答简洁，不超过300字
                """;
    }

    private String callApi(String prompt) throws IOException {
        String jsonBody = String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":%s}],\"temperature\":0.7,\"max_tokens\":500}",
                model,
                escapeJson(prompt));

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API调用失败: " + response);
            }

            String responseBody = response.body().string();
            return extractContent(responseBody);
        }
    }

    private String extractContent(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                String content = messageNode.path("content").asText();
                if (content != null && !content.isEmpty()) {
                    return content;
                }
            }
            return jsonResponse;
        } catch (Exception e) {
            logger.error("解析AI响应失败", e);
            return jsonResponse;
        }
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}
