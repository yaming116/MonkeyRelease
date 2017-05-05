MonkeyRelease
===

*Gradle* Android打包插件

功能如下：

1. 获取 *versionName* 和 *versionCode*用于重命名*APK*的名字,格式如下: `project_name-${variant}-${versionName}-${versionCode}.apk`
2. 配置签名信息,根据环境变量 **IS_JENKINS**,加载方式有下面两种:
    * 加载当前项目下面的 *monkey_release.properties*
    * 加载 `环境变量(STORE_ROOT)/$project_name/config.json`
    

Usage
===

添加如下信息到: `app/build.gradle`

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        compile 'com.github.yaming116:monkey-release:1.0.0'
    }
}
apply plugin: 'monkey-release'
```

创建 `app/monkey_release.properties`

```properties
KEYSTORE_FILE=path/to/your.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```



感谢
===

[easyrelease](https://github.com/inloop/easyrelease)