plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    testImplementation("org.testng:testng:7.7.1")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    implementation("com.google.code.gson:gson:2.10.1")

}
tasks.test {
    useTestNG()
}