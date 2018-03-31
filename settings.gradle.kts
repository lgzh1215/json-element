rootProject.name = "json-element"

include("moshi")
findProject(":moshi")!!.buildFileName = "moshi.gradle.kts"