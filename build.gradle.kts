plugins {
    id("java")
    id("war")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0") // Provides Servlet API
    compileOnly("javax.servlet:javax.servlet-api:4.0.1") // For older servlet versions, if needed
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("deployToTomcat") {
    dependsOn("war") // Ensure WAR is built first
    from("build/libs") // Location of WAR file
    into("C:/Apache/Tomcat9/webapps") // Deploy directly to Tomcat
}