package net.lumynity.true_end.config.serializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public abstract class ConfigSerializer {
	public final File configFile;
	private final String extension;

	public ConfigSerializer(String configFileName, String extension) {
		this.extension = extension;
		extension = "." + extension;
		this.configFile = getConfigFile(configFileName, extension);
	}

	private File getConfigFile(String configFileName, String extension) {
		if (!configFileName.endsWith(extension)) {
			configFileName += extension;
		}
		return Paths.get("config", configFileName).toFile();
	}

	protected File getConfigFile() {
		return this.configFile;
	}

	abstract public void serialize(Map<String, Object> entries);

	abstract public Map<String, Object> deserialize();

	protected void createConfigFile() {
		try {
			this.getConfigFile().createNewFile();
		} catch (IOException ignored) {}
	}

	public String getMessage() {
		return "Loaded " + this.extension.toUpperCase() + " config file " + this.getConfigFile().toPath();
	}
}