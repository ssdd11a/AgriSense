
/*
 * AgriSense ESP8266 + DHT11 传感器数据上报示例
 * 
 * 功能：
 * 1. 读取 DHT11 温湿度数据
 * 2. 连接 WiFi
 * 3. 每10秒通过 HTTP POST 上报数据到后端
 * 4. 首次上报时自动在后端注册设备
 * 
 * 硬件连接：
 * - DHT11 VCC -> 3.3V
 * - DHT11 GND -> GND
 * - DHT11 DATA -> D4 (GPIO2)
 * 
 * 依赖库：
 * - ESP8266WiFi.h (ESP8266 核心库)
 * - DHT.h (DHT 传感器库，安装：库管理器搜索 "DHT sensor library")
 */

#include &lt;ESP8266WiFi.h&gt;
#include &lt;DHT.h&gt;

// ---------------- 配置信息 ----------------
const char* WIFI_SSID = "你的WiFi名称";
const char* WIFI_PASSWORD = "你的WiFi密码";

const char* SERVER_HOST = "192.168.1.100"; // 后端服务器IP
const int SERVER_PORT = 8080;
const String DEVICE_ID = "esp8266_001"; // 设备ID，可自定义
const int REPORT_INTERVAL = 10000; // 上报间隔，毫秒

#define DHTPIN D4      // DHT11 数据引脚
#define DHTTYPE DHT11  // DHT11 传感器类型

// ---------------- 全局变量 ----------------
DHT dht(DHTPIN, DHTTYPE);
unsigned long lastReportTime = 0;
WiFiClient client;

// ---------------- 函数声明 ----------------
void connectWiFi();
void sendSensorData(float temperature, float humidity);

void setup() {
  Serial.begin(115200);
  delay(100);
  
  Serial.println("\n====================================");
  Serial.println("AgriSense ESP8266 传感器节点");
  Serial.println("====================================");
  
  // 初始化 DHT11
  dht.begin();
  delay(2000);
  
  // 连接 WiFi
  connectWiFi();
}

void loop() {
  // 检查 WiFi 连接状态
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi 连接断开，正在重连...");
    connectWiFi();
    delay(5000);
    return;
  }
  
  // 定时上报数据
  if (millis() - lastReportTime &gt;= REPORT_INTERVAL) {
    lastReportTime = millis();
    
    // 读取传感器数据
    float humidity = dht.readHumidity();
    float temperature = dht.readTemperature();
    
    // 检查读取是否成功
    if (isnan(humidity) || isnan(temperature)) {
      Serial.println("读取传感器失败！");
      return;
    }
    
    Serial.printf("温湿度读取成功: 温度=%.1f°C, 湿度=%.1f%%\n", temperature, humidity);
    
    // 发送数据到后端
    sendSensorData(temperature, humidity);
  }
  
  delay(1000);
}

// WiFi 连接函数
void connectWiFi() {
  Serial.printf("正在连接 WiFi: %s ...\n", WIFI_SSID);
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED &amp;&amp; attempts &lt; 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi 连接成功！");
    Serial.printf("IP 地址: %s\n", WiFi.localIP().toString().c_str());
  } else {
    Serial.println("\nWiFi 连接失败！");
  }
}

// 发送传感器数据到后端
void sendSensorData(float temperature, float humidity) {
  if (!client.connect(SERVER_HOST, SERVER_PORT)) {
    Serial.println("连接服务器失败！");
    return;
  }
  
  // 构建 JSON 请求体
  String jsonBody = "{\"deviceId\":\"";
  jsonBody += DEVICE_ID;
  jsonBody += "\",\"temperature\":";
  jsonBody += String(temperature, 1);
  jsonBody += ",\"humidity\":";
  jsonBody += String(humidity, 1);
  jsonBody += "}";
  
  // 发送 HTTP POST 请求
  client.println("POST /api/sensor HTTP/1.1");
  client.printf("Host: %s:%d\n", SERVER_HOST, SERVER_PORT);
  client.println("Content-Type: application/json");
  client.printf("Content-Length: %d\n", jsonBody.length());
  client.println("Connection: close");
  client.println();
  client.println(jsonBody);
  
  // 等待并打印响应
  Serial.println("\n发送请求到服务器...");
  unsigned long timeout = millis();
  while (client.available() == 0) {
    if (millis() - timeout &gt; 5000) {
      Serial.println("服务器响应超时！");
      client.stop();
      return;
    }
  }
  
  String response = "";
  while (client.available()) {
    response += (char)client.read();
  }
  
  Serial.println("服务器响应:");
  Serial.println(response.substring(0, 200)); // 只打印前200字符
  
  client.stop();
  Serial.println("------------------------------------");
}

