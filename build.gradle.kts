plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.7"
}

repositories {
    jcenter()
}

application {
    mainClassName = "com.github.mesayah.cardgame.CardGame"
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}
