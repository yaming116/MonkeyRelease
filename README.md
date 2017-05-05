MonkeyRelease
===

*Gradle* Android打包插件

功能如下：

1. 获取 *versionName* 和 *versionCode*用于重命名*APK*的名字,格式如下: `project_name-${variant}-${versionName}-${versionCode}.apk`
2. 配置签名信息,根据环境变量 **IS_JENKINS**,加载方式有下面两种:
    * 加载当前项目下面的 *monkey_release.properties*
    * 加载 `环境变量(STORE_ROOT)/$project_name/config.json`



感谢
===

[easyrelease](https://github.com/inloop/easyrelease)