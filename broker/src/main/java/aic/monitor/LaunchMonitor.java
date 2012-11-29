package aic.monitor;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Properties;

import org.openstack.keystone.KeystoneClient;
import org.openstack.keystone.api.Authenticate;
import org.openstack.keystone.api.ListTenants;
import org.openstack.keystone.model.Access;
import org.openstack.keystone.model.Authentication;
import org.openstack.keystone.model.Authentication.PasswordCredentials;
import org.openstack.keystone.model.Authentication.Token;
import org.openstack.keystone.model.Tenants;
import org.openstack.keystone.utils.KeystoneUtils;
import org.openstack.nova.NovaClient;
import org.openstack.nova.api.FlavorsCore;
import org.openstack.nova.api.ImagesCore;
import org.openstack.nova.api.ServersCore;
import org.openstack.nova.api.extensions.SnapshotsExtension;
import org.openstack.nova.model.Flavor;
import org.openstack.nova.model.Flavors;
import org.openstack.nova.model.Image;
import org.openstack.nova.model.Images;
import org.openstack.nova.model.Server;
import org.openstack.nova.model.ServerForCreate;
import org.openstack.nova.model.Servers;
import org.openstack.nova.model.Snapshots;

public class LaunchMonitor {

	private String KEYSTONE_AUTH_URL;
	private String KEYSTONE_USERNAME;
	private String KEYSTONE_PASSWORD;
	private String KEY_NAME;
	private String SECURITY_GROUP_NAME;
	private Access serverAccess = null;
	
	public LaunchMonitor(Properties p){
		this.KEYSTONE_AUTH_URL = p.getProperty("openstack_url");
		this.KEYSTONE_USERNAME = p.getProperty("openstack_username");
		this.KEYSTONE_PASSWORD = p.getProperty("openstack_password");
		this.KEY_NAME = p.getProperty("openstack_key");
		this.SECURITY_GROUP_NAME = p.getProperty("openstack_security_group");
	}
	
	public LaunchMonitor(String url,
			String username,
			String password,
			String key,
			String security_group){
		this.KEYSTONE_AUTH_URL = url;
		this.KEYSTONE_USERNAME = username;
		this.KEYSTONE_PASSWORD = password;
		this.KEY_NAME = key;
		this.SECURITY_GROUP_NAME = security_group;
	}

	public Server createServer(String serverName, String flavorRef,
			String imgRef) {
		// define server instance
		ServerForCreate serverForCreate = new ServerForCreate();
		serverForCreate.setName(serverName);
		serverForCreate.setFlavorRef(flavorRef);
		serverForCreate.setImageRef(imgRef);
		serverForCreate.setKeyName(KEY_NAME);
		serverForCreate.getSecurityGroups().add(
				new ServerForCreate.SecurityGroup(SECURITY_GROUP_NAME));

		// create server
		Server server = this.getNovaClient().execute(
				ServersCore.createServer(serverForCreate));

		System.out.println(server);

		return server;
	}

	public void terminateServer(String id) {
		this.getNovaClient().execute(ServersCore.deleteServer(id));
	}

	public Flavors getFlavors() {
		return this.getNovaClient().execute(FlavorsCore.listFlavors());
	}

	public Servers getServers() {
		return this.getNovaClient().execute(ServersCore.listServers(true));
	}

	public Images getImages() {
		return this.getNovaClient().execute(ImagesCore.listImages());
	}
	
	public Snapshots getSnapshots(){
		return this.getNovaClient().execute(SnapshotsExtension.listSnapshots());
	}

	/**
	 * @param id Server id
	 * @return Returns Server with @id, if it exists. Otherwise null.
	 */
	public Server getServer(String id) {
		for (Server server : this.getServers()) {
			if (server.getId().equals(id)) {
				System.out.println(server);
				return server;
			}
		}

		return null;
	}

	private Boolean isTokenExpired() {
		return Calendar.getInstance().getTime()
				.compareTo(serverAccess.getToken().getExpires().getTime()) == 1;
	}

	/**
	 * Returns NovaClient instance.
	 * 
	 * Checks whether is there access to server and token isn't expired yet.
	 * Otherwise auth on server through KeystoneClient with default credentials.
	 * 
	 * @return Instance of NovaClient
	 */
	private NovaClient getNovaClient() {
		if (serverAccess == null || isTokenExpired()) {
			KeystoneClient keystone = new KeystoneClient(KEYSTONE_AUTH_URL);
			Authentication authentication = new Authentication();
			PasswordCredentials passwordCredentials = new PasswordCredentials();
			passwordCredentials.setUsername(KEYSTONE_USERNAME);
			passwordCredentials.setPassword(KEYSTONE_PASSWORD);
			authentication.setPasswordCredentials(passwordCredentials);

			// access with unscoped token
			serverAccess = keystone.execute(new Authenticate(authentication));

			// use the token in the following requests
			keystone.setToken(serverAccess.getToken().getId());

			Tenants tenants = keystone.execute(new ListTenants());

			// try to exchange token using the first tenant
			if (tenants.getList().size() > 0) {

				authentication = new Authentication();
				Token token = new Token();
				token.setId(serverAccess.getToken().getId());
				authentication.setToken(token);
				authentication.setTenantId(tenants.getList().get(0).getId());

				serverAccess = keystone
						.execute(new Authenticate(authentication));

			} else {
				System.out.println("No tenants found!");
				return null;
			}
		}

		return new NovaClient(KeystoneUtils.findEndpointURL(
				serverAccess.getServiceCatalog(), "compute", null, "public"),
				serverAccess.getToken().getId());
	}

	/**
	 * It's not a usable method. It's just example,
	 * how to use LaunchMonitor.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream("properties.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// TODO Auto-generated method stub
		LaunchMonitor monitor = new LaunchMonitor(properties);

		String flavorRef = null;
		String imgRef = null;

		// print all flavors
		for (Flavor flavor : monitor.getFlavors()) {
			if (flavor.getName().equals("m1.tiny.win"))
				flavorRef = flavor.getLinks().get(0).getHref();
			System.out.println(flavor);
		}

		// print all images
		for (Image image : monitor.getImages()) {
			if (image.getName().equals("mongo-fresh")) {
				imgRef = image.getLinks().get(0).getHref();
			}
			System.out.println(image);
		}

		// print all instances
		for (Server server : monitor.getServers()) {
			System.out.println(server);
		}

		if (!(imgRef == null && flavorRef == null)) {
			// start instance
			Server server = monitor.createServer("new-server-from-java",
					flavorRef, imgRef);
			// waiting for ACTIVE state
			Boolean isActive = false;
			while (!isActive) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isActive = monitor.getServer(server.getId()).getStatus()
						.equals("ACTIVE");
			}
			// terminate instance
			monitor.terminateServer(server.getId());
		}

		// stop started instance

	}
}
