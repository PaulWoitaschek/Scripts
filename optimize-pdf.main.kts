#!/usr/bin/env kotlin
@file:DependsOn("com.github.ajalt.clikt:clikt-jvm:3.1.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.3")

import java.io.File
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class App : CliktCommand() {

    private val inputs by argument().file(mustExist = true).multiple()

    override fun run() {
        inputs.forEach {
            require(it.extension == "pdf") {
                "$it must be a pdf file."
            }
        }

        runBlocking {
            inputs.forEach { input ->
                launch(Dispatchers.IO) {
                    optimize(input)
                }
            }
        }
    }

    private fun optimize(input: File) {
        val output = File(input.parentFile, input.nameWithoutExtension + "_ocr." + input.extension)
        println("optimize ${input.absolutePath} to ${output.absolutePath}")
        ProcessBuilder(
                "ocrmypdf", input.absolutePath,
                output.absolutePath,
                "--clean",
                "--clean-final",
                "--remove-background",
                "--deskew",
                "-l", "deu+eng",
                "--jobs", "8"
        )
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
    }
}

App().main(args)
