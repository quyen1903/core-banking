package com.quinnbank.core.cif.architecture;

import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CifArchitectureTest {
    private static final Path CIF_SOURCE = Path.of("src", "main", "java", "com", "quinnbank", "core", "cif");

    @Test
    void domainCompilesWithoutApplicationAdaptersOrFrameworks() throws IOException {
        compileWithJdkOnly("domain", javaFiles(CIF_SOURCE.resolve("domain")));
    }

    @Test
    void applicationAndDomainCompileWithoutAdaptersOrFrameworks() throws IOException {
        List<Path> coreSources = Stream.concat(javaFiles(CIF_SOURCE.resolve("domain")).stream(),
                javaFiles(CIF_SOURCE.resolve("application")).stream()).toList();

        compileWithJdkOnly("core", coreSources);
    }

    @Test
    void readPortExposesOnlyAReadSnapshotAndNoAggregateOrWriteMethods() throws ReflectiveOperationException {
        assertThat(CustomerReadPort.class.getDeclaredMethods())
                .extracting(java.lang.reflect.Method::getName).containsExactly("findById");
        Type returnType = CustomerReadPort.class.getMethod("findById", UUID.class).getGenericReturnType();

        assertThat(returnType).isInstanceOf(ParameterizedType.class);
        ParameterizedType optionalSnapshot = (ParameterizedType) returnType;
        assertThat(optionalSnapshot.getRawType()).isEqualTo(Optional.class);
        assertThat(optionalSnapshot.getActualTypeArguments()).containsExactly(GetCustomerByIdResult.class);
        assertThat(GetCustomerByIdResult.class.isRecord()).isTrue();
        assertThat(GetCustomerByIdResult.class.getRecordComponents())
                .allSatisfy(component -> assertThat(component.getType().getPackageName()).startsWith("java."));
    }

    private static List<Path> javaFiles(Path sourceDirectory) throws IOException {
        try (Stream<Path> paths = Files.walk(sourceDirectory)) {
            return paths.filter(path -> path.toString().endsWith(".java")).sorted().toList();
        }
    }

    private static void compileWithJdkOnly(String layer, List<Path> sourceFiles) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).as("Architecture checks require the configured JDK, not a JRE").isNotNull();
        assertThat(sourceFiles).as("Sources to check for %s", layer).isNotEmpty();
        Path output = Files.createDirectories(Path.of("build", "cif-architecture-tests", layer));
        Path emptyClasspath = Files.createDirectories(Path.of("build", "cif-architecture-tests", "empty"));
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromPaths(sourceFiles);
            List<String> options = List.of("-proc:none", "-classpath", emptyClasspath.toString(),
                    "-sourcepath", emptyClasspath.toString(), "-d", output.toString());
            boolean compiled = compiler.getTask(null, fileManager, diagnostics, options, null, sources).call();

            assertThat(compiled).as("%s must depend only on the JDK and its included inner layers: %s",
                    layer, diagnostics.getDiagnostics()).isTrue();
        }
    }
}
