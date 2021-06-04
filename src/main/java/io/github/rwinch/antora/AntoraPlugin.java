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

import com.github.gradle.node.NodeExtension;
import com.github.gradle.node.NodePlugin;
import com.github.gradle.node.npm.task.NpmTask;
import com.github.gradle.node.npm.task.NpxTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;
import java.util.Arrays;

/**
 * @author Rob Winch
 */
public class AntoraPlugin implements Plugin<Project> {

	public static final String DOWNLOAD_ANTORA_TASK_NAME = "downloadAntora";

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
		node.getDownload().set(true);
//		node.getVersion().set("12.16.2");

		TaskProvider<NpmTask> downloadAntoraSiteGenerator = project.getTasks()
			.register(DOWNLOAD_ANTORA_GENERATOR_TASK_NAME, NpmTask.class, npm -> {
				npm.getArgs().set(Arrays.asList(INSTALL, ANTORA_GENERATOR_PACKAGE_NAME));
			});

		project.getTasks().register(DEFAULT_ANTORA_TASK_NAME, NpxTask.class, a -> {
			a.getCommand().set(ANTORA_CLI_PACKAGE_NAME);
			a.getArgs().set(Arrays.asList( "site.yml"));
			a.dependsOn(downloadAntoraSiteGenerator);
		});
	}

	private Antora config() {
		return (Antora) project.getExtensions().getByName(Antora.ID);
	}
}
