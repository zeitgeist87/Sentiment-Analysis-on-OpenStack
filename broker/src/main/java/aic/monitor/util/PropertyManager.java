package aic.monitor.util;

import java.util.Properties;

import aic.monitor.ui.StartUpMonitor;

public class PropertyManager {
	private Properties properties = null;

	private PropertyManager() {
		properties = new Properties();
		try {
			properties.loadFromXML((StartUpMonitor.class.getResourceAsStream("properties.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	private static PropertyManager manager = null;

	public static PropertyManager getInstance() {
		if (manager == null) {
			manager = new PropertyManager();
		}

		return manager;
	}
}
