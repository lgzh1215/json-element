import groovy.lang.Closure
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
   maven
   kotlin("jvm") version "1.2.31"
}

allprojects {
   group = "moe.aisia.json-element"
   version = "1.0"

   repositories {
      mavenCentral()
      jcenter()
   }

   tasks.withType<KotlinCompile> {
      kotlinOptions.apply {
         suppressWarnings = true
         verbose = true
         freeCompilerArgs = listOf(
               "-Xno-call-assertions",
               "-Xno-param-assertions",
               "-Xno-receiver-assertions"
         )
      }
   }
}

dependencies {
   compile(kotlin("stdlib"))
   testCompile(kotlin("reflect"))
   testCompile("junit:junit:4.12")
   testCompile("io.kotlintest:kotlintest:2.0.7") {
      exclude("org.jetbrains.kotlin", "kotlin-reflect")
   }
}

task<Jar>("sourcesJar") {
   dependsOn("classes")
   classifier = "sources"
   from(java.sourceSets["main"].allSource)
}

artifacts {
   add("archives", tasks["sourcesJar"])
}