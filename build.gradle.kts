plugins {
	id("io.quarkus") apply false
}

subprojects {
	repositories {
		mavenCentral()
		mavenLocal()
	}

	pluginManager.withPlugin("java") {
		extensions.configure<JavaPluginExtension> {
			toolchain {
				languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_21.majorVersion)
				nativeImageCapable = false
			}
		}
		tasks.withType<JavaCompile> {
			options.encoding = "UTF-8"
			options.compilerArgs.add("-parameters")
		}
	}

	pluginManager.withPlugin("io.quarkus") {
		tasks.withType<Test> {
			systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
		}
	}
}
