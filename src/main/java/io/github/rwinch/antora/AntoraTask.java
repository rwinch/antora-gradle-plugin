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
