def fileContent = project.properties.findAll { key, value ->
    key.endsWith('container.image')
}.collect { key, value ->
    "${key}=${value}"
}.join('\n')

File testClasses = new File("${project.build.testOutputDirectory}")
if (testClasses.exists()) {
    File metaInfDir = new File("${testClasses.absolutePath}/META-INF")
    metaInfDir.mkdir()

    File file = new File("${metaInfDir.absolutePath}/microprofile-config.properties")
    file.write(fileContent)
}