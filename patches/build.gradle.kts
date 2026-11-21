plugins {
    alias(libs.plugins.animalsniffer)
}

group = "app.template"

patches {
    about {
        name = "VWFNDR Patches"
        description = "Patches for VWFNDR + MBL (com.vwfndr.mbl)"
        source = "https://github.com/yourusername/vwfndr-patches"
        author = "bench"
        contact = "na"
        website = "na"
        license = "GPLv3"
    }
}

val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
    signature(libs.android.api.signature) { artifact { type = "signature" } }
}

animalsniffer {
    sourceSets = listOf(project.sourceSets["main"])
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"
        dependsOn(build)
        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    named("buildAndroid") {
        dependsOn("animalsnifferMain")
    }

    publish {
        dependsOn("generatePatchesList")
    }
}
