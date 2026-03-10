# Gallery 代码库速查图（新人向）

> 一张图 + 最短路径，快速理解：从主界面到数据层，再到多协议媒体实现。

## 1) 整体架构速查（模块 & 分层）

```mermaid
flowchart TB
    subgraph Gradle工程
      A[app 模块\nAndroid 应用]
      B[photoview 模块\n图片缩放/手势库]
    end

    subgraph app核心分层
      UI[Activity / Fragment / Adapter\nMainActivity / ViewerActivity / ImageFragment]
      MVP[Contract + Presenter\nMainContact / ViewerContact\nMainPresenter / ViewerPresenter]
      DATA[Data\nMediaDataSource / MediaRepository\nMediaCache]
      MODEL[Model\nMedia 抽象 + Local/Samba/WebDav/AutoIndex]
      DB[Database\nRequery + Server 实体]
      DI[Dagger DI\nAppComponent + AppModule]
      IMG[Glide 自定义加载\nMediaLoader + MediaDataFetcher]
    end

    A --> UI
    A --> MVP
    A --> DI
    UI <--> MVP
    MVP --> DATA
    DATA --> MODEL
    MVP --> DB
    UI --> IMG
    IMG --> MODEL
    DI --> MVP
    DI --> DATA
    DI --> DB
```

---

## 2) 主流程速查（从启动到看图）

```mermaid
sequenceDiagram
    participant App as Application
    participant Main as MainActivity
    participant MP as MainPresenter
    participant Repo as MediaRepository
    participant DB as Database
    participant Viewer as ViewerActivity
    participant VP as ViewerPresenter

    App->>App: 创建 AppComponent 并注入全局依赖
    Main->>MP: start()
    MP->>Repo: register(Local/Samba/WebDav/AutoIndex)
    MP->>Repo: addRoot(本地目录)
    MP->>DB: getServers()
    DB-->>MP: 已保存的服务器
    MP->>Repo: addRoot(远程源)
    MP->>Repo: roots()/loadDir()
    MP-->>Main: replaceData(...)

    Main->>MP: clickItem(...)
    alt 点击图片
        MP-->>Main: startViewer(...)
        Main->>Viewer: 传递 uri/parent/position
        Viewer->>VP: loadData(...)
        VP->>Repo: loadDir(parent)
        VP->>Repo: loadMediaList(parent)
        VP-->>Viewer: replaceData(纯图片列表, 当前索引)
    else 点击目录
        MP->>Repo: loadDir(child)
        MP-->>Main: replaceData(...)
    end
```

---

## 3) 多协议扩展速查（新增一种来源怎么做）

```mermaid
flowchart LR
    I[实现 Media 接口\n例如 XxxMedia]
    S[声明协议标识 例如 smb 或 dav]
    U[实现必要方法 fromUri children getInputStream auth]
    R[在 MainPresenter start 中注册新实现]
    D[可选: 在数据库中保存连接信息]

    I --> S --> U --> R --> D
```

---

## 4) 新人建议阅读顺序（30~60 分钟）

1. `app/src/main/AndroidManifest.xml`：先看页面入口与 Application。  
2. `activity/MainActivity.java` + `presenter/MainPresenter.java`：理解主列表和目录导航。  
3. `data/MediaDataSource.java` + `data/MediaRepository.java`：理解统一数据抽象。  
4. `model/Media.java` + `model/media/*Media.java`：理解多协议统一机制。  
5. `activity/ViewerActivity.java` + `presenter/ViewerPresenter.java` + `fragment/ImageFragment.java`：理解全屏查看链路。  
6. `di/*`：看依赖注入怎么把对象串起来。  
7. `glide/*`：理解为什么 Glide 能直接加载 `Media`。  

---

## 5) 常见排查点（实战高频）

- 看不到某个远程源：先查 `register()` 是否注册对应 scheme，再看 `addRoot()` 的 URI 格式。  
- 能进目录但无缩略图：查 `MediaDataFetcher` 是否拿到 `InputStream`，以及该目录是否有可识别图片后缀。  
- 查看器位置错乱：重点看 `ViewerPresenter` 里“文件列表索引”和“纯图片索引”的转换逻辑。  
- 返回键行为不符合预期：看 `MainPresenter.loadParent()` 与 `MainActivity.onBackPressed()` 联动。  

