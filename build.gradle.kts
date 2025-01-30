//import com.google.protobuf.gradle.id

plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
  //  id("com.google.protobuf") version "0.9.4"
}

group = "ru.zinoviev.questbot"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val grpcStarterVersion = "2.15.0.RELEASE"
val protobufVersion = "3.25.1"
val grpcVersion = "1.58.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // https://mvnrepository.com/artifact/org.hashids/hashids
    implementation("org.hashids:hashids:1.0.3")


    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.springframework:spring-aspects:6.0.11")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

//    implementation("io.grpc:grpc-netty:${grpcVersion}")
//    implementation("net.devh:grpc-spring-boot-starter:${grpcStarterVersion}") {
//        exclude(group = "io.grpc", module = "grpc-netty-shaded")
//    }

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:${protobufVersion}"
//    }
//    plugins {
//        id ("grpc") {
//            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
//        }
//    }
//    generateProtoTasks {
//        ofSourceSet("main").forEach {
//            it.plugins {
//                id("grpc") { }
//            }
//        }
//    }
//}

tasks.withType<Test> {
    useJUnitPlatform()
}
