
# AgriSense 智慧农业温湿监测系统

企业级智慧农业监测系统，支持多设备管理、作物管理、AI 智能分析、告警系统等功能。

## 功能特性

### 核心功能
- 🔌 **多设备支持**：支持多个传感器设备独立上报数据
- 🌾 **作物管理**：预定义常见作物（番茄、黄瓜、草莓、水稻），支持自定义作物和生长阶段
- 🤖 **AI 智能分析**：集成 DeepSeek AI，基于作物和温湿度给出个性化管理建议
- 🚨 **智能告警**：自动检测温湿度异常，记录告警历史
- 📈 **历史数据**：支持历史数据查看、图表分析和 CSV 导出

### 企业级特性
- 🔒 **安全性**：预留认证接口（可轻松扩展）
- 📝 **数据验证**：输入验证确保数据完整性
- 📄 **API 文档**：集成 Swagger/OpenAPI
- 🐳 **Docker 支持**：一键部署
- 📦 **日志管理**：完善的日志记录

## 快速开始

### 环境要求
- Java 17+
- Maven 3.8+
- MySQL 8.0+（可选，默认使用 H2 内存数据库）

### 运行项目

#### 1. 使用 Maven 直接运行
```bash
mvn spring-boot:run
```

#### 2. 使用 Docker
```bash
# 构建并启动
docker-compose up -d
```

### 访问系统
- **前端仪表盘**: http://localhost:8080/index.html
- **数据模拟器**: http://localhost:8080/simulator.html
- **API 文档**: http://localhost:8080/swagger-ui.html
- **H2 控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - 用户名: `sa`
  - 密码: (留空)

## 硬件接入

### ESP8266 + DHT11/DHT22

项目提供完整的 Arduino 示例代码，见 `hardware/esp8266_dht11.ino`。

**硬件连接**：
- DHT11 VCC → ESP8266 3.3V
- DHT11 GND → ESP8266 GND  
- DHT11 DATA → ESP8266 D4 (GPIO2)

**代码配置**：
```c
const char* WIFI_SSID = "你的WiFi名称";
const char* WIFI_PASSWORD = "你的WiFi密码";
const char* SERVER_HOST = "192.168.1.100"; // 后端服务器IP
const int SERVER_PORT = 8080;
const String DEVICE_ID = "esp8266_001";
```

## API 文档

### 传感器数据接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/sensor` | 上报传感器数据 |
| GET | `/api/sensor/latest` | 获取最新数据 |
| GET | `/api/sensor/advice` | 获取 AI 建议 |
| GET | `/api/sensor/history` | 获取历史数据（分页） |

### 设备管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/devices` | 获取所有设备 |
| GET | `/api/devices/{id}` | 获取单个设备 |
| POST | `/api/devices` | 创建设备 |
| PUT | `/api/devices/{id}` | 更新设备 |
| DELETE | `/api/devices/{id}` | 删除设备 |

### 作物管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/crops` | 获取所有作物 |
| GET | `/api/crops/{id}` | 获取单个作物 |
| POST | `/api/crops` | 创建作物 |
| PUT | `/api/crops/{id}` | 更新作物 |
| DELETE | `/api/crops/{id}` | 删除作物 |
| GET | `/api/crops/{id}/stages` | 获取作物生长阶段 |

## 配置说明

### application.yml 主要配置
```yaml
# DeepSeek AI 配置
deepseek:
  api-key: "sk-你的真实密钥"
  model: "deepseek-chat"

# 数据库配置（可选 MySQL）
spring:
  datasource:
    url: jdbc:h2:mem:testdb  # H2 内存数据库（默认）
    # url: jdbc:mysql://localhost:3306/agrisense  # MySQL 配置
    # username: root
    # password: 123456
    # driver-class-name: com.mysql.cj.jdbc.Driver
```

## 项目结构

```
AgriSense/
├── src/main/java/com/example/agrisense/
│   ├── entity/              # 实体类
│   │   ├── Crop.java       # 作物
│   │   ├── Device.java     # 设备
│   │   ├── SensorData.java # 传感器数据
│   │   ├── Alert.java      # 告警记录
│   │   └── GrowthStage.java # 生长阶段
│   ├── repository/          # 数据访问层
│   ├── service/            # 业务逻辑层
│   ├── controller/         # 控制器
│   └── config/             # 配置类
├── src/main/resources/
│   ├── static/             # 前端文件
│   │   ├── index.html      # 主仪表盘
│   │   └── simulator.html  # 数据模拟器
│   └── application.yml     # 配置文件
├── hardware/               # 硬件示例代码
│   └── esp8266_dht11.ino  # ESP8266 示例
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Data JPA
- SpringDoc OpenAPI (Swagger)
- H2/MySQL 数据库
- OkHttp (AI 接口调用)

### 前端
- 原生 HTML/CSS/JavaScript
- Chart.js (图表)
- Font Awesome (图标)

## 预置作物数据

系统初始化时自动创建以下作物：

| 作物 | 图标 | 生长阶段 |
|------|------|----------|
| 番茄 | 🍅 | 苗期、花期、果期 |
| 黄瓜 | 🥒 | 苗期、伸蔓期、结瓜期 |
| 草莓 | 🍓 | 缓苗期、生长期、开花结果期 |
| 水稻 | 🌾 | 苗期、分蘖期、孕穗期、灌浆期 |
| 通用 | 🌱 | 通用 |

## 开发说明

### 添加新的 AI 服务
实现 `AiService` 接口即可添加新的 AI 提供商：

```java
@Service
public class MyAiService implements AiService {
    // 实现接口方法
}
```

### 修改告警规则
在 `AlertService` 中自定义告警逻辑。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

