package com.github.yaming116.monkeyrelease

import com.android.build.gradle.AppPlugin
import org.gradle.api.Project
import org.gradle.api.Plugin

class MonkeyReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!(project.plugins.hasPlugin(AppPlugin) )) {
            throw new IllegalArgumentException(
                    'Monkey Release gradle plugin can only be applied to android App projects.')
        }

        // Setup properties for our customization, otherwise zipalign will not start
        project.android {
            signingConfigs {
                release {
                    storeFile = project.file('.')
                    keyAlias = ''
                    storePassword = ''
                    keyPassword = ''
                }
            }
        }

        project.android {
            buildTypes {
                release {
                    debuggable false
                    zipAlignEnabled true
                    signingConfig project.android.signingConfigs.release
                }
            }
        }


        project.afterEvaluate {
            Utils.setApkName(project)
            Utils.loadReleaseSigningConfig(project)
        }
    }
}