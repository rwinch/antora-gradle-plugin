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
