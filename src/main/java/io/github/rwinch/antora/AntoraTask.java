/*
 * Copyright 2019 the original author or authors.
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

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.os.OperatingSystem;

import java.util.Collections;

public class AntoraTask extends DefaultTask {

	private static final String DEFAULT_PLAYBOOK_FILENAME = "site.yml";

	@Input
	private Property<String> playbookFilename = getProject().getObjects().property(String.class).value(DEFAULT_PLAYBOOK_FILENAME);

	public AntoraTask() {
		super();
		setGroup("Docs");
		setDescription("Installs and runs antora");
		setDependsOn(Collections.singleton(getProject().getTasks().named(AntoraPlugin.DOWNLOAD_ANTORA_TASK_NAME)));
	}

	public Property<String> getPlaybookFilename() {
		return playbookFilename;
	}

	public void playbookFilename(final String playbookFilename) {
		this.playbookFilename.set(playbookFilename);
	}

	@TaskAction
	public void runAntora() {
		getLogger().info("Running antora using playbook " + playbookFilename.get());
		getProject().exec(execSpec -> execSpec.setCommandLine(antoraCommand(), playbookFilename.get()));
	}

	private String antoraCommand() {
		String command = "build/modules/node_modules/.bin/antora";
		return OperatingSystem.current().isWindows() ? command + ".cmd" : command;
	}
}
