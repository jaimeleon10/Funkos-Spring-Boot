plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm")
	id("jacoco")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

dependencies {
	// Web Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	// JPA e Hibernate
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// Base de datos H2
	implementation("com.h2database:h2")
	// Cache
	implementation("org.springframework.boot:spring-boot-starter-cache")
	// Validacion
	implementation("org.springframework.boot:spring-boot-starter-validation")
	// WebSocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.required.set(true)
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestReport)
}
