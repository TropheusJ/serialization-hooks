package io.github.tropheusj.serialization_hooks;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class SerializationHooksPreLaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		MixinExtrasBootstrap.init();
	}
}
