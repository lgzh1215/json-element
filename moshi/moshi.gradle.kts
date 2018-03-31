import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   maven
   kotlin("jvm")
}

dependencies {
   compile(kotlin("stdlib"))
   compile(rootProject)
   compileOnly("com.squareup.moshi:moshi:1.5.0")
   testCompile(kotlin("reflect"))
   testCompile("junit:junit:4.12")
   testCompile("io.kotlintest:kotlintest:2.0.7")
   testCompile("com.squareup.moshi:moshi:1.5.0")
}

java.sourceSets["test"].resources.setSrcDirs(rootProject.java.sourceSets["test"].resources.srcDirs)

task<Jar>("sourcesJar") {
   dependsOn("classes")
   classifier = "sources"
   from(java.sourceSets["main"].allSource)
}

artifacts {
   add("archives", tasks["sourcesJar"])
}