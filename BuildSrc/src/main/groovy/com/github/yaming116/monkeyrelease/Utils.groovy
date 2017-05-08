package com.github.yaming116.monkeyrelease

import groovy.json.JsonSlurper
import org.apache.commons.lang.NullArgumentException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Sun on 2017/5/4.
 */
public class Utils {
    static final String TAG = '[MonkeyRelease]'

    /**
     * Modifies output apk file name for all application variants
     */
    static def setApkName(project) {
        project.android.applicationVariants.all { variant ->

            // first check version code and name from Gradle build script, then from AndroidManifest.xml
            def versionCode = variant.versionCode ? variant.versionCode : getVersionCode(project)
            def versionName = variant.versionName ? variant.versionName : getVersionName(project)
            def appName = appName(project)
            def apkDir = new File(project.project.rootDir, 'build' + File.separator + 'apk')
            def fileName = "$appName-$variant.name-$versionName-${versionCode}.apk"
            variant.outputs.each { output ->
                output.outputFile = new File(apkDir, fileName)
                def apkPath = output.outputFile.getAbsolutePath()
                println "$TAG Setting $output.name variant output name to $fileName"
                if (output.name == 'release') {
                    println "appPath: $apkPath"
                }
            }
        }
    }

    /**
     * Parsing AndroidManifest.xml to return versionName
     */
    static def getVersionName(project) {
        def androidManifestPath = project.android.sourceSets.main.manifest.srcFile
        def manifestText = project.file(androidManifestPath).getText()
        def patternVersionNumber = java.util.regex.Pattern.compile("versionName=\"(\\d+)\\.(\\d+)\\.(\\d+)\"")
        def matcherVersionNumber = patternVersionNumber.matcher(manifestText)
        matcherVersionNumber.find()
        def majorVersion = Integer.parseInt(matcherVersionNumber.group(1))
        def minorVersion = Integer.parseInt(matcherVersionNumber.group(2))
        def pointVersion = Integer.parseInt(matcherVersionNumber.group(3))
        def versionName = majorVersion + "." + minorVersion + "." + pointVersion
        return versionName
    }

    /**
     * Parsing AndroidManifest.xml to return versionCode
     */
    static def getVersionCode(project) {
        def androidManifestPath = project.android.sourceSets.main.manifest.srcFile
        def manifestText = project.file(androidManifestPath).getText()
        def patternVersionNumber = java.util.regex.Pattern.compile("versionCode=\"(\\d+)\"")
        def matcherVersionNumber = patternVersionNumber.matcher(manifestText)
        matcherVersionNumber.find()
        def version = Integer.parseInt(matcherVersionNumber.group(1))
        return version
    }

    /**
     * Loads signing properties from file and sets them in release signingConfig
     */
    static def loadProperties(project) {
        def projectDir = project.projectDir
        def propFileName = 'monkey_release.properties'
        def propFile = new File("$projectDir/$propFileName")
        println "$TAG Reading $projectDir/$propFileName"
        if (propFile.canRead()) {
            def props = new Properties()
            props.load(new FileInputStream(propFile))

            def buildType = project.android.signingConfigs.release
            buildType.storeFile = project.file(props["KEYSTORE_FILE"])
            buildType.storePassword = props["KEYSTORE_PASSWORD"]
            buildType.keyAlias = props["KEY_ALIAS"]
            buildType.keyPassword = props["KEY_PASSWORD"]

        } else {
            throw new FileNotFoundException("can't found monkey_release.properties file in $projectDir")
        }
    }

    static def loadJsonConfig(project, rootName){
        def keyStorePath = System.getenv('STORE_ROOT')
        def jsonConfig = new File("$keyStorePath/$rootName/config.json")
        if (!jsonConfig.exists()){
            throw new FileNotFoundException("can't found json config at $jsonConfig")
        }
        def jsonSlurper = new JsonSlurper()
        def map = jsonSlurper.parse(jsonConfig)
        def buildType = project.android.signingConfigs.release
        buildType.storeFile = project.file(map.get("KEY_PATH"))
        buildType.storePassword = map.get("STORE_PASSWORD")
        buildType.keyAlias = map.get("KEY_ALIAS")
        buildType.keyPassword = map.get("KEY_PASSWORD")
    }


    static def loadReleaseSigningConfig(project){
        def isJenkins = "true".equals(System.getenv("IS_JENKINS"))

        if (!isJenkins) {
            loadProperties(project)
        }else {
            def appName = appName(project)
            loadJsonConfig(project, appName)
        }
    }

    static def appName(project){
        def rootName = project.rootProject.name
        println("=======root project name is $rootName")
        def appNamePrefix = System.getenv("APP_NAME_PREFIX");
        if (appNamePrefix != null) {
            rootName = rootName.replace(appNamePrefix, "")
        }
        println("=======after replace appNamePrefix is $rootName")
        if (project.hasProperty('projectName')) {
            rootName = project.property('projectName')
        }
        println("=====current project name is $rootName=====")

        return rootName
    }
}
