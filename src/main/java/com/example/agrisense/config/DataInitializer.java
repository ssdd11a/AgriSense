package com.example.agrisense.config;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.GrowthStage;
import com.example.agrisense.entity.PromptTemplate;
import com.example.agrisense.repository.CropRepository;
import com.example.agrisense.repository.DeviceRepository;
import com.example.agrisense.repository.PromptTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CropRepository cropRepository;
    private final DeviceRepository deviceRepository;
    private final PromptTemplateRepository promptTemplateRepository;

    public DataInitializer(CropRepository cropRepository, DeviceRepository deviceRepository,
            PromptTemplateRepository promptTemplateRepository) {
        this.cropRepository = cropRepository;
        this.deviceRepository = deviceRepository;
        this.promptTemplateRepository = promptTemplateRepository;
    }

    @Override
    public void run(String... args) {
        if (cropRepository.count() == 0) {
            initCrops();
        }

        if (deviceRepository.count() == 0) {
            initDevices();
        }

        if (promptTemplateRepository.count() == 0) {
            initPromptTemplates();
        }
    }

    private void initCrops() {
        logger.info("初始化作物数据...");

        Crop tomato = new Crop();
        tomato.setName("番茄");
        tomato.setIcon("🍅");
        tomato.setDescription("茄科番茄属，喜温蔬菜");
        tomato.setSpecialNotes("高温高湿易发病害，注意通风排湿。灰霉病、晚疫病需重点防控。");
        tomato.setIsDefault(false);

        addGrowthStage(tomato, "苗期", "定植后至开花前", 10.0, 18.0, 25.0, 30.0, 50.0, 60.0, 70.0, 85.0, 0);
        addGrowthStage(tomato, "花期", "第一穗花开放至坐果", 12.0, 20.0, 28.0, 32.0, 45.0, 55.0, 70.0, 85.0, 1);
        addGrowthStage(tomato, "结果期", "坐果至果实成熟", 10.0, 20.0, 26.0, 30.0, 50.0, 60.0, 75.0, 85.0, 2);
        cropRepository.save(tomato);

        Crop cucumber = new Crop();
        cucumber.setName("黄瓜");
        cucumber.setIcon("🥒");
        cucumber.setDescription("葫芦科黄瓜属，喜温喜湿");
        cucumber.setSpecialNotes("需水量大，保持土壤湿润但不积水。注意霜霉病和白粉病防控。");
        cucumber.setIsDefault(false);

        addGrowthStage(cucumber, "苗期", "出苗至4-5片真叶", 15.0, 22.0, 28.0, 32.0, 60.0, 70.0, 80.0, 90.0, 0);
        addGrowthStage(cucumber, "伸蔓期", "甩蔓至初花", 15.0, 20.0, 26.0, 32.0, 60.0, 70.0, 85.0, 90.0, 1);
        addGrowthStage(cucumber, "结瓜期", "根瓜坐住至拉秧", 12.0, 22.0, 30.0, 35.0, 70.0, 75.0, 85.0, 95.0, 2);
        cropRepository.save(cucumber);

        Crop strawberry = new Crop();
        strawberry.setName("草莓");
        strawberry.setIcon("🍓");
        strawberry.setDescription("蔷薇科草莓属，多年生草本");
        strawberry.setSpecialNotes("花期注意温度控制，果实期注意通风排湿预防灰霉病。");
        strawberry.setIsDefault(false);

        addGrowthStage(strawberry, "缓苗期", "定植后2周内", 5.0, 15.0, 20.0, 25.0, 60.0, 70.0, 80.0, 90.0, 0);
        addGrowthStage(strawberry, "生长期", "缓苗后至开花前", 5.0, 15.0, 22.0, 28.0, 60.0, 65.0, 75.0, 85.0, 1);
        addGrowthStage(strawberry, "开花结果期", "开花至果实成熟", 5.0, 15.0, 20.0, 25.0, 50.0, 60.0, 70.0, 80.0, 2);
        cropRepository.save(strawberry);

        Crop rice = new Crop();
        rice.setName("水稻");
        rice.setIcon("🌾");
        rice.setDescription("禾本科稻属，一年生草本，水田种植");
        rice.setSpecialNotes("需保持适当水层，注意纹枯病、稻飞虱防控。");
        rice.setIsDefault(false);

        addGrowthStage(rice, "苗期", "育秧期", 15.0, 20.0, 28.0, 35.0, 70.0, 75.0, 85.0, 95.0, 0);
        addGrowthStage(rice, "分蘖期", "返青至分蘖盛期", 18.0, 22.0, 30.0, 35.0, 70.0, 75.0, 85.0, 95.0, 1);
        addGrowthStage(rice, "孕穗期", "分蘖末期至抽穗前", 20.0, 25.0, 32.0, 35.0, 75.0, 80.0, 90.0, 98.0, 2);
        addGrowthStage(rice, "灌浆期", "抽穗至蜡熟", 20.0, 25.0, 30.0, 35.0, 70.0, 75.0, 85.0, 95.0, 3);
        cropRepository.save(rice);

        Crop general = new Crop();
        general.setName("通用作物");
        general.setIcon("🌱");
        general.setDescription("适用于大多数常见作物");
        general.setSpecialNotes("根据实际种植作物调整阈值");
        general.setIsDefault(true);

        addGrowthStage(general, "通用", "适用于所有生长阶段", 10.0, 18.0, 28.0, 35.0, 40.0, 60.0, 80.0, 90.0, 0);
        cropRepository.save(general);

        logger.info("作物数据初始化完成");
    }

    private void addGrowthStage(Crop crop, String name, String desc,
            double minTemp, double optTempMin, double optTempMax, double maxTemp,
            double minHum, double optHumMin, double optHumMax, double maxHum,
            int order) {
        GrowthStage stage = new GrowthStage();
        stage.setName(name);
        stage.setDescription(desc);
        stage.setMinTemp(minTemp);
        stage.setOptimalTempMin(optTempMin);
        stage.setOptimalTempMax(optTempMax);
        stage.setMaxTemp(maxTemp);
        stage.setMinHumidity(minHum);
        stage.setOptimalHumidityMin(optHumMin);
        stage.setOptimalHumidityMax(optHumMax);
        stage.setMaxHumidity(maxHum);
        stage.setOrder(order);
        crop.addStage(stage);
    }

    private void initDevices() {
        logger.info("初始化设备数据...");

        Device device1 = new Device();
        device1.setId("1");
        device1.setName("大棚A-主设备");
        device1.setApiKey("DEVICE_KEY_001");
        device1.setCropId(1L);
        device1.setGrowthStage("花期");
        device1.setLocation("南区1号大棚");
        device1.setCreatedAt(LocalDateTime.now());
        device1.setLastActive(LocalDateTime.now());
        device1.setIsActive(true);
        deviceRepository.save(device1);

        logger.info("设备数据初始化完成");
    }

    private void initPromptTemplates() {
        logger.info("初始化提示词模板...");

        PromptTemplate template = new PromptTemplate();
        template.setName("默认农业建议模板");
        template.setTemplate("""
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
            """);
        template.setLang("zh-CN");
        template.setIsDefault(true);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        promptTemplateRepository.save(template);

        logger.info("提示词模板初始化完成");
    }
}
