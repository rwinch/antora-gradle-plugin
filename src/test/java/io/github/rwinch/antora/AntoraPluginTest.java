package io.github.rwinch.antora;

import com.github.gradle.node.npm.task.NpxTask;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.testng.annotations.Parameters;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.gradle.util.GradleVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Parameters
class AntoraPluginTest {
	@TempDir
	File projectDirParent;

	@ValueSource
	public static List<GradleVersion> gradleVersions() {
		return Arrays.asList("6.9.0", "7.0.0", "7.0.1", "7.0.2").stream()
				.map(GradleVersion::version)
				.collect(Collectors.toList());
	}

	@Test
	void antoraWhenCustomArguments() throws Exception {
		Project project = ProjectBuilder.builder()
				.build();
		project.getPlugins().apply(AntoraPlugin.class);
		AntoraExtension antora = project.getExtensions().getByType(AntoraExtension.class);
		antora.getArguments().set(Arrays.asList("--fetch"));

		NpxTask antoraTask = (NpxTask) project.getTasks().getByPath(":antora");
		assertThat(antoraTask.getArgs().get()).containsOnly("antora-playbook.yml", "--fetch");
	}

	@Test
	void antoraWhenCustomAntoraVersion() throws Exception {
		File projectDir = tempProjectDirFromResource(projectDirParent, "/custom-antora-version");
		BuildResult gradle = GradleRunner.create()
				.forwardOutput()
				.withProjectDir(projectDir)
				.withArguments("antora")
				.withPluginClasspath()
				.build();

		Path packageJson = projectDir.toPath().resolve("node_modules/@antora/asciidoc-loader/package.json");
		String packageJsonContent = Files.readString(packageJson);
		assertThat(packageJsonContent).contains("@antora/asciidoc-loader@2.3.3");
	}

	@ParameterizedTest
	@MethodSource("gradleVersions")
	void antoraWhenDefaultsThenSuccess(GradleVersion gradleVersion) throws Exception {
		File projectDir = tempProjectDirFromResource(projectDirParent, "/demo");
		BuildResult gradle = GradleRunner.create()
			.forwardOutput()
			.withProjectDir(projectDir)
			.withArguments("antora")
			.withPluginClasspath()
			.build();

		assertThat(gradle.task(":antora").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(projectDir.toPath().resolve("build/site/index.html")).exists();
	}

	@ParameterizedTest
	@MethodSource("gradleVersions")
	void antoraWhenCustomPlaybookThenSuccess(GradleVersion gradleVersion) throws Exception {
		File projectDir = tempProjectDirFromResource(projectDirParent, "/custom-playbook");
		BuildResult gradle = GradleRunner.create()
				.forwardOutput()
				.withProjectDir(projectDir)
				.withArguments("antora")
				.withPluginClasspath()
				.build();

		assertThat(gradle.task(":antora").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(projectDir.toPath().resolve("build/site/index.html")).exists();
	}

	private static File tempProjectDirFromResource(File testProjectDir, String resourceName) throws IOException, URISyntaxException {
		Path rootProjectPath = testProjectDir.toPath().resolve("root");
		URI demo = AntoraPluginTest.class.getResource(resourceName).toURI();
		copyRecursive(Paths.get(demo), rootProjectPath);
		return rootProjectPath.toFile();
	}

	private static void copyRecursive(Path source, Path target, CopyOption... options)
			throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException {
				Files.createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				Files.copy(file, target.resolve(source.relativize(file)), options);
				return FileVisitResult.CONTINUE;
			}
		});
	}

}