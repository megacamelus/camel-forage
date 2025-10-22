package org.apache.camel.forage.maven.catalog;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class CodeScanner {
    private final Log log;

    public CodeScanner(Log log) {
        this.log = log;
    }

    /**
     * Finds the source directory for the given artifact within the project structure.
     */
    private Path findSourceDirectory(Artifact artifact, MavenProject rootProject) {
        // Try multiple possible locations for the source directory

        Path projectBase = Paths.get(rootProject.getBasedir().getAbsolutePath());
        log.debug("Project base directory: " + projectBase);

        // Check if we're in the root project and the artifact is a module
        String artifactId = artifact.getArtifactId();
        log.debug("Looking for source directory for artifact: " + artifactId);

        try (Stream<Path> paths = Files.walk(projectBase)) {
            final Optional<Path> first = paths.filter(path -> Files.isDirectory(path) && path.endsWith(artifactId))
                    .findFirst();

            return first.orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Scans the source code for @ForageFactory annotations in the given artifact.
     */
    public List<FactoryInfo> scanForForageFactories(Artifact artifact, MavenProject rootProject) {
        List<FactoryInfo> factories = new ArrayList<>();

        // Find the source directory for this artifact
        Path sourceDir = findSourceDirectory(artifact, rootProject);
        if (sourceDir == null || !Files.exists(sourceDir)) {
            log.debug("No source directory found for artifact: " + artifact.getArtifactId());
            return factories;
        }

        try {
            log.debug("Scanning source directory: " + sourceDir);

            // Walk through all Java files in the source directory
            try (Stream<Path> paths = Files.walk(sourceDir)) {
                paths.filter(path -> path.toString().endsWith(".java")).forEach(javaFile -> {
                    try {
                        scanJavaFileForForageFactories(javaFile, factories);
                    } catch (Exception e) {
                        log.warn("Failed to parse Java file: " + javaFile + " - " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            log.warn("Failed to scan source directory: " + sourceDir + " - " + e.getMessage());
        }

        log.debug("Found " + factories.size() + " ForageFactory annotations in " + artifact.getArtifactId());
        return factories;
    }

    /**
     * Scans the source code for @ForageBean annotations in the given artifact.
     */
    public List<ForgeBeanInfo> scanForForageBeans(Artifact artifact, MavenProject rootProject) {
        List<ForgeBeanInfo> beans = new ArrayList<>();

        // Find the source directory for this artifact
        Path sourceDir = findSourceDirectory(artifact, rootProject);
        if (sourceDir == null || !Files.exists(sourceDir)) {
            log.debug("No source directory found for artifact: " + artifact.getArtifactId());
            return beans;
        }

        try {
            log.debug("Scanning source directory: " + sourceDir);

            // Walk through all Java files in the source directory
            try (Stream<Path> paths = Files.walk(sourceDir)) {
                paths.filter(path -> path.toString().endsWith(".java")).forEach(javaFile -> {
                    try {
                        scanJavaFileForForgeBeans(javaFile, beans);
                    } catch (Exception e) {
                        log.warn("Failed to parse Java file: " + javaFile + " - " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            log.warn("Failed to scan source directory: " + sourceDir + " - " + e.getMessage());
        }

        log.debug("Found " + beans.size() + " ForageBean annotations in " + artifact.getArtifactId());
        return beans;
    }

    /**
     * Scans a single Java file for @ForageFactory annotations.
     */
    private void scanJavaFileForForageFactories(Path javaFile, List<FactoryInfo> factories) {
        try {
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> parseResult = parser.parse(javaFile).getResult();

            if (parseResult.isEmpty()) {
                log.debug("Failed to parse Java file: " + javaFile);
                return;
            }

            CompilationUnit cu = parseResult.get();

            // Find all class declarations with @ForageFactory annotation
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                classDecl.getAnnotations().forEach(annotation -> {
                    if (isForageFactoryAnnotation(annotation)) {
                        FactoryInfo factoryInfo = extractForageFactoryInfo(annotation, classDecl, cu);
                        if (factoryInfo != null) {
                            factories.add(factoryInfo);
                            log.debug("Found ForageFactory: " + factoryInfo);
                        }
                    }
                });
            });

        } catch (Exception e) {
            log.warn("Error parsing Java file: " + javaFile + " - " + e.getMessage());
        }
    }

    /**
     * Scans a single Java file for @ForageBean annotations.
     */
    private void scanJavaFileForForgeBeans(Path javaFile, List<ForgeBeanInfo> beans) {
        try {
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> parseResult = parser.parse(javaFile).getResult();

            if (parseResult.isEmpty()) {
                log.debug("Failed to parse Java file: " + javaFile);
                return;
            }

            CompilationUnit cu = parseResult.get();

            // Find all class declarations with @ForageBean annotation
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                classDecl.getAnnotations().forEach(annotation -> {
                    if (isForageBeanAnnotation(annotation)) {
                        ForgeBeanInfo beanInfo = extractForgeBeanInfo(annotation, classDecl, cu);
                        if (beanInfo != null) {
                            beans.add(beanInfo);
                            log.debug("Found ForageBean: " + beanInfo);
                        }
                    }
                });
            });

        } catch (Exception e) {
            log.warn("Error parsing Java file: " + javaFile + " - " + e.getMessage());
        }
    }

    /**
     * Checks if the annotation is a @ForageFactory annotation.
     */
    private boolean isForageFactoryAnnotation(AnnotationExpr annotation) {
        String name = annotation.getNameAsString();
        return "ForageFactory".equals(name) || "org.apache.camel.forage.core.annotations.ForageFactory".equals(name);
    }

    /**
     * Checks if the annotation is a @ForageBean annotation.
     */
    private boolean isForageBeanAnnotation(AnnotationExpr annotation) {
        String name = annotation.getNameAsString();
        return "ForageBean".equals(name) || "org.apache.camel.forage.core.annotations.ForageBean".equals(name);
    }

    /**
     * Extracts ForageFactory information from the annotation.
     */
    private FactoryInfo extractForageFactoryInfo(
            AnnotationExpr annotation, ClassOrInterfaceDeclaration classDecl, CompilationUnit cu) {
        String name = "";
        List<String> components = new ArrayList<>();
        String description = "";
        String factoryType = "";
        boolean autowired = false;

        // Extract annotation values
        if (annotation instanceof SingleMemberAnnotationExpr) {
            SingleMemberAnnotationExpr singleMember = (SingleMemberAnnotationExpr) annotation;
            if (singleMember.getMemberValue() instanceof StringLiteralExpr) {
                name = ((StringLiteralExpr) singleMember.getMemberValue()).asString();
            }
        } else if (annotation instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotation;
            for (MemberValuePair pair : normalAnnotation.getPairs()) {
                String pairName = pair.getNameAsString();
                Expression value = pair.getValue();

                switch (pairName) {
                    case "value":
                        if (value instanceof StringLiteralExpr) {
                            name = ((StringLiteralExpr) value).asString();
                        }
                        break;
                    case "components":
                        components = extractStringArrayValue(value);
                        break;
                    case "description":
                        if (value instanceof StringLiteralExpr) {
                            description = ((StringLiteralExpr) value).asString();
                        }
                        break;
                    case "factoryType":
                        if (value instanceof StringLiteralExpr) {
                            factoryType = ((StringLiteralExpr) value).asString();
                        }
                        break;
                    case "autowired":
                        if (value instanceof BooleanLiteralExpr) {
                            autowired = ((BooleanLiteralExpr) value).getValue();
                        }
                        break;
                }
            }
        }

        // Get the fully qualified class name
        String className = getFullyQualifiedClassName(classDecl, cu);

        return new FactoryInfo(name, components, description, factoryType, className, autowired);
    }

    /**
     * Extracts ForageBean information from the annotation.
     */
    private ForgeBeanInfo extractForgeBeanInfo(
            AnnotationExpr annotation, ClassOrInterfaceDeclaration classDecl, CompilationUnit cu) {
        String name = "";
        List<String> components = new ArrayList<>();
        String description = "";
        String feature = "";

        // Extract annotation values
        if (annotation instanceof SingleMemberAnnotationExpr) {
            SingleMemberAnnotationExpr singleMember = (SingleMemberAnnotationExpr) annotation;
            if (singleMember.getMemberValue() instanceof StringLiteralExpr) {
                name = ((StringLiteralExpr) singleMember.getMemberValue()).asString();
            }
        } else if (annotation instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotation;
            for (MemberValuePair pair : normalAnnotation.getPairs()) {
                String pairName = pair.getNameAsString();
                Expression value = pair.getValue();

                switch (pairName) {
                    case "value":
                        if (value instanceof StringLiteralExpr) {
                            name = ((StringLiteralExpr) value).asString();
                        }
                        break;
                    case "components":
                        components = extractStringArrayValue(value);
                        break;
                    case "description":
                        if (value instanceof StringLiteralExpr) {
                            description = ((StringLiteralExpr) value).asString();
                        }
                        break;
                    case "feature":
                        if (value instanceof StringLiteralExpr) {
                            feature = ((StringLiteralExpr) value).asString();
                        }
                        break;
                }
            }
        }

        // Get the fully qualified class name
        String className = getFullyQualifiedClassName(classDecl, cu);

        return new ForgeBeanInfo(name, components, description, className, feature);
    }

    /**
     * Scans the source code for ConfigEntries classes and extracts configuration properties.
     */
    public List<ConfigurationProperty> scanForConfigurationProperties(Artifact artifact, MavenProject rootProject) {
        List<ConfigurationProperty> configProperties = new ArrayList<>();

        // Find the source directory for this artifact
        Path sourceDir = findSourceDirectory(artifact, rootProject);
        if (sourceDir == null || !Files.exists(sourceDir)) {
            log.debug("No source directory found for artifact: " + artifact.getArtifactId());
            return configProperties;
        }

        try {
            log.debug("Scanning source directory for ConfigEntries classes: " + sourceDir);

            // Walk through all Java files in the source directory
            try (Stream<Path> paths = Files.walk(sourceDir)) {
                paths.filter(path -> path.toString().endsWith(".java")).forEach(javaFile -> {
                    try {
                        scanJavaFileForConfigEntries(javaFile, configProperties);
                    } catch (Exception e) {
                        log.warn("Failed to parse Java file for ConfigEntries: " + javaFile + " - " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            log.warn("Failed to scan source directory for ConfigEntries: " + sourceDir + " - " + e.getMessage());
        }

        log.debug("Found " + configProperties.size() + " configuration properties in " + artifact.getArtifactId());
        return configProperties;
    }

    /**
     * Scans a single Java file for ConfigEntries classes and extracts ConfigModule fields.
     */
    private void scanJavaFileForConfigEntries(Path javaFile, List<ConfigurationProperty> configProperties) {
        try {
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> parseResult = parser.parse(javaFile).getResult();

            if (parseResult.isEmpty()) {
                log.debug("Failed to parse Java file: " + javaFile);
                return;
            }

            CompilationUnit cu = parseResult.get();

            // Find all class declarations that extend ConfigEntries
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                if (extendsConfigEntries(classDecl)) {
                    log.debug("Found ConfigEntries class: " + classDecl.getNameAsString());
                    extractConfigModuleFields(classDecl, configProperties);
                }
            });

        } catch (Exception e) {
            log.warn("Error parsing Java file for ConfigEntries: " + javaFile + " - " + e.getMessage());
        }
    }

    /**
     * Checks if a class declaration extends ConfigEntries.
     */
    private boolean extendsConfigEntries(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getExtendedTypes().stream()
                .anyMatch(extendedType -> "ConfigEntries".equals(extendedType.getNameAsString()));
    }

    /**
     * Extracts ConfigModule fields from a ConfigEntries class.
     */
    private void extractConfigModuleFields(
            ClassOrInterfaceDeclaration classDecl, List<ConfigurationProperty> configProperties) {
        // Find all static final ConfigModule fields
        classDecl.findAll(FieldDeclaration.class).forEach(field -> {
            if (field.isStatic() && field.isFinal()) {
                field.getVariables().forEach(variable -> {
                    if (isConfigModuleField(field, variable)) {
                        String configKey = extractConfigModuleKey(variable);
                        if (configKey != null && !configKey.isEmpty()) {
                            ConfigurationProperty configProp = new ConfigurationProperty();
                            configProp.setName(configKey);
                            configProp.setType("String"); // Most config properties are strings
                            configProp.setDescription(configKey);
                            configProp.setRequired(false); // Default to not required

                            log.debug("Extracted configuration property: " + configKey);
                            configProperties.add(configProp);
                        }
                    }
                });
            }
        });
    }

    /**
     * Checks if a field is a ConfigModule field.
     */
    private boolean isConfigModuleField(FieldDeclaration field, VariableDeclarator variable) {
        // Check if the field type is ConfigModule
        return field.getElementType().asString().equals("ConfigModule")
                || field.getElementType().asString().endsWith(".ConfigModule");
    }

    /**
     * Extracts the configuration key from a ConfigModule field initialization.
     * Expects patterns like: ConfigModule.of(SomeConfig.class, "config.key.name")
     */
    private String extractConfigModuleKey(VariableDeclarator variable) {
        if (variable.getInitializer().isPresent()) {
            var initializer = variable.getInitializer().get();

            // Look for ConfigModule.of() method call
            if (initializer instanceof MethodCallExpr methodCall) {

                // Check if it's a call to ConfigModule.of()
                if (isConfigModuleOfCall(methodCall)) {
                    // Extract the second argument (the config key string)
                    if (methodCall.getArguments().size() >= 2) {
                        var secondArg = methodCall.getArguments().get(1);
                        if (secondArg instanceof StringLiteralExpr) {
                            return ((StringLiteralExpr) secondArg).asString();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a method call is ConfigModule.of().
     */
    private boolean isConfigModuleOfCall(MethodCallExpr methodCall) {
        return methodCall.getNameAsString().equals("of")
                && methodCall.getScope().isPresent()
                && (methodCall.getScope().get().toString().equals("ConfigModule")
                        || methodCall.getScope().get().toString().endsWith(".ConfigModule"));
    }

    /**
     * Extracts a list of strings from an annotation value that can be either a single string or an array of strings.
     */
    private List<String> extractStringArrayValue(Expression value) {
        List<String> result = new ArrayList<>();

        ArrayInitializerExpr arrayExpr = (ArrayInitializerExpr) value;
        for (Expression element : arrayExpr.getValues()) {
            if (element instanceof StringLiteralExpr) {
                result.add(((StringLiteralExpr) element).asString());
            }
        }

        return result;
    }

    /**
     * Gets the fully qualified class name from the compilation unit.
     */
    private String getFullyQualifiedClassName(ClassOrInterfaceDeclaration classDecl, CompilationUnit cu) {
        Optional<String> packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString());
        String className = classDecl.getNameAsString();

        if (packageName.isPresent()) {
            return packageName.get() + "." + className;
        } else {
            return className;
        }
    }
}
