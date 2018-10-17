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
import org.gradle.api.tasks.Exec;

import java.io.File;
import java.util.Arrays;

/**
 * @author Rob Winch
 */
public class AntoraPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPlugins().apply(NodePlugin.class);

		NodeExtension node = project.getExtensions().getByType(NodeExtension.class);
		node.setDownload(true);
		node.setVersion("8.11.4");
		node.setNodeModulesDir(project.file(new File(project.getBuildDir(), "/modules")));

		NpmTask downloadAntoraCli = project.getTasks()
				.create("downloadAntoraCli", NpmTask.class);
		downloadAntoraCli.setArgs(Arrays.asList("install", "@antora/cli"));

		NpmTask downloadAntoraSiteGenerator = project.getTasks()
				.create("downloadAntoraSiteGenerator", NpmTask.class);
		downloadAntoraSiteGenerator.setArgs(Arrays.asList("install", "@antora/site-generator-default"));

		Task downloadAntora = project.getTasks().create("downloadAntora");
		downloadAntora.dependsOn(downloadAntoraCli, downloadAntoraSiteGenerator);

		Exec antora = project.getTasks().create("antora", Exec.class);
		antora.setGroup("Docs");
		antora.setDescription("Installs and runs antora site.yml");
		antora.dependsOn(downloadAntora);
		antora.setCommandLine("build/modules/node_modules/@antora/cli/bin/antora", "site.yml");
	}
}
