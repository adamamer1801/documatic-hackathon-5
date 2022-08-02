plugins {
    id("java")
}

group = "dev.danky.cmm"
version = "1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")
    annotationProcessor("info.picocli:picocli-codegen:4.6.3")
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "dev.danky.cmm.CMM"
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map {if (it.isDirectory) it else zipTree(it)})
    }

}

