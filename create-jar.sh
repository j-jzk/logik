#!/usr/bin/bash

# Builds the project into one .jar instead of the ridiculous tarballs Gradle creates.
# ("./gradlew jar" doesn't produce a runnable file)
# If someone knows how to do this using Gradle, please tell me.

kotlinc -include-runtime -d logik.jar src/main/kotlin/github/j_jzk/circuitsim/*
