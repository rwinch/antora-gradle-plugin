package io.github.rwinch.antora;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class Antora {
	public static final String ID = "antora";

	private Property<String> antoraVersion;

	@Inject
	public Antora( ObjectFactory objects ) {
		antoraVersion = objects.property( String.class );
	}

	public Property<String> getAntoraVersion() {
		return antoraVersion;
	}

	public void antoraVersion( String antoraVersion ) {
		this.antoraVersion.set( antoraVersion );
	}
}
