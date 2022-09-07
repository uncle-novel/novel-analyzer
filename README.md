<p align="center">
    <a href="https://github.com/unclezs/novel-analyzer/actions/workflows/maven.yml">
    <img src="https://img.shields.io/github/workflow/status/unclezs/novel-analyzer/Java%20CI%20with%20Maven" alt="maven build"/>
    </a>
	<img src="https://img.shields.io/github/v/release/unclezs/novel-analyzer" alt="release"/>
	<img src="https://img.shields.io/badge/jdk-8.221-green" alt="jdk"/>
	<img src="https://img.shields.io/badge/platform-win linux mac-green" alt="platform"/>
</p>

### 🌡️小说解析SDK

对小说解析下载功能的封装。

[规则编写教程](https://github.com/unclezs/uncle-novel-official-site/tree/main/docs/booksource)

```xml
<dependency>
  <groupId>com.unclezs</groupId>
  <artifactId>novel-analyzer</artifactId>
  <version>1.0.26</version>
</dependency>
```

### 主要功能

- 小说零规则目录解析及正文解析
- 书源模式（css选择器、xpath、json-path、regex）
- HTTP代理支持
- SPI自定义HTTP客户端（动 / 静态）
- 动态脚本支持
- 小说下载基础能力
