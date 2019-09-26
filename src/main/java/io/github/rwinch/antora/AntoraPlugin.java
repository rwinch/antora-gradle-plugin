/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.rwinch.antora;

import com.moowork.gradle.node.NodeExtension;
import com.moowork.gradle.node.NodePlugin;
import com.moowork.gradle.node.npm.NpmTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;
import java.util.Arrays;

/**
 * @author Rob Winch
 */
public class AntoraPlugin implements Plugin<Project> {

	public static final String DOWNLOAD_ANTORA_TASK_NAME = "downloadAntora";

	private static final String DOWNLOAD_ANTORA_CLI_TASK_NAME = "downloadAntoraCli";

	private static final String DOWNLOAD_ANTORA_GENERATOR_TASK_NAME = "downloadAntoraSiteGenerator";

	private static final String DEFAULT_ANTORA_TASK_NAME = "antora";

	private static final String ANTORA_CLI_PACKAGE_NAME = "@antora/cli";

	private static final String ANTORA_GENERATOR_PACKAGE_NAME = "@antora/site-generator-default";

	private static final String INSTALL = "install";

	private Project project;

	@Override
	public void apply(Project project) {
		this.project = project;
		project.getExtensions().create(Antora.ID, Antora.class);
		project.getPlugins().apply(NodePlugin.class);

		NodeExtension node = NodeExtension.get(project);
		node.setDownload(true);
		node.setVersion("8.11.4");
		final File nodeModulesDir = project.file(new File(project.getBuildDir(), "/modules"));
		node.setNodeModulesDir(nodeModulesDir);

		registerSetupTasks();

		registerDefaultTasks();

		registerAntoraVersionOverrideHandler();
	}

	private void registerSetupTasks() {
		NpmTask downloadAntoraCli = project.getTasks()
				.create(DOWNLOAD_ANTORA_CLI_TASK_NAME, NpmTask.class);
		downloadAntoraCli.setArgs(Arrays.asList(INSTALL, ANTORA_CLI_PACKAGE_NAME));

		NpmTask downloadAntoraSiteGenerator = project.getTasks()
				.create(DOWNLOAD_ANTORA_GENERATOR_TASK_NAME, NpmTask.class);
		downloadAntoraSiteGenerator.setArgs(Arrays.asList(INSTALL, ANTORA_GENERATOR_PACKAGE_NAME));

		Task downloadAntora = project.getTasks()
				.create(DOWNLOAD_ANTORA_TASK_NAME)
				.dependsOn(downloadAntoraCli, downloadAntoraSiteGenerator);
	}

	private void registerDefaultTasks() {
		project.getTasks().register(DEFAULT_ANTORA_TASK_NAME, AntoraTask.class);
	}


	private void registerAntoraVersionOverrideHandler() {
		project.afterEvaluate(evaluatedProject -> {
			if (config().getAntoraVersion().isPresent()) {
				final String antoraVersion = config().getAntoraVersion().get();
				project.getTasks()
						.named(DOWNLOAD_ANTORA_CLI_TASK_NAME, NpmTask.class)
						.configure(downloadAntoraCli -> {
							downloadAntoraCli.setArgs(Arrays.asList(INSTALL, ANTORA_CLI_PACKAGE_NAME + "@" + antoraVersion));
						});
				project.getTasks()
						.named(DOWNLOAD_ANTORA_GENERATOR_TASK_NAME, NpmTask.class)
						.configure(downloadAntoraGenerator -> {
							downloadAntoraGenerator.setArgs(Arrays.asList(INSTALL, ANTORA_GENERATOR_PACKAGE_NAME + "@" + antoraVersion));
						});
			}
		});
	}

	private String antoraCommand() {
		String command = "build/modules/node_modules/.bin/antora";
		return OperatingSystem.current().isWindows() ? command + ".cmd" : command;
	}

	private Antora config() {
		return (Antora) project.getExtensions().getByName(Antora.ID);
	}
}
