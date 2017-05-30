package es.amplia.odm.devices.digi.connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnection.class);
	
	private String remoteAddress;
	private int remotePort;
	private String remoteBaseUri;
	private String deviceId;
	private String apiKey;
	private HttpClient httpClient;
	
	public HttpConnection(String remoteAddress, int remotePort, String remoteBaseUri, String deviceId, String apiKey) {
		this(remoteAddress, remotePort, remoteBaseUri, deviceId, apiKey, null);
	}
	
	public HttpConnection(String remoteAddress, int remotePort, InetAddress localAddr) {
		this(remoteAddress, remotePort,null , null, null, localAddr);
	}
	
	public HttpConnection(String remoteAddress, int remotePort, String remoteBaseUri, String deviceId, String apiKey, InetAddress localAddr) {
		super();
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		if (remoteBaseUri != null) {
			this.remoteBaseUri = remoteBaseUri.startsWith("/")? remoteBaseUri:"/" + remoteBaseUri;
			this.remoteBaseUri = remoteBaseUri.endsWith("/")? this.remoteBaseUri:this.remoteBaseUri + "/";
		}
		this.deviceId = deviceId;
		this.apiKey = apiKey;
		httpClient = new HttpClient();
		httpClient.setAddressResolutionTimeout(10000);
		if (localAddr != null) httpClient.setBindAddress(new InetSocketAddress(localAddr, 0));
		httpClient.setFollowRedirects(true);
	}

	public void sendCollectData (String data, String dataType) throws Throwable {
		sendData(data, "/collect/" + dataType);
	}
	
	public void sendResponse (String data) throws Throwable {
		sendData(data, "/operation/response");
	}
	
	public void sendData (String data, String uri) throws Throwable {
		httpClient.start();
		try {
			Request post = httpClient.POST("http://" + remoteAddress + ( (this.remotePort!=0)?(":" + this.remotePort):"" ) + remoteBaseUri + deviceId + uri);
			post.header("X-ApiKey", this.apiKey);
			post.header(HttpHeader.CONTENT_TYPE, "application/json");
			post.content(new StringContentProvider(data), "application/json");
			ContentResponse response = post.send();
			if (response.getStatus() != HttpStatus.CREATED_201) LOGGER.error("Received response form server: " + response.getStatus());
		} catch (Throwable e) {
			throw e;
		} finally {
			httpClient.stop();
		}
	}
	
	public int sendTextData (String data, String uri) throws Throwable {
		httpClient.start();
		int ret = HttpStatus.OK_200;
		try {
			Request post = httpClient.POST("http://" + remoteAddress + ( (this.remotePort!=0)?(":" + this.remotePort):"" ) + uri);
			post.header(HttpHeader.CONTENT_TYPE, "text/plain");
			post.content(new StringContentProvider(data), "text/plain");
			ContentResponse response = post.send();
			if (response.getStatus() != HttpStatus.OK_200) {
				LOGGER.error("Received response form server: " + response.getStatus());
				ret = response.getStatus();
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			httpClient.stop();
		}
		return ret;
	}
	
	public String getData (String uri) throws Throwable {
		httpClient.start();
		try {
			ContentResponse response = httpClient.GET("http://" + remoteAddress + ( (this.remotePort!=0)?(":" + this.remotePort):"" ) + uri);
			if (response.getStatus() != HttpStatus.OK_200) LOGGER.error("Received response form server: " + response.getStatus());
			return response.getContentAsString();
		} catch (Throwable e) {
			throw e;
		} finally {
			httpClient.stop();
		}
	}
	
	/*public boolean getFile (String downloadUri, final String localPath, final String filename) throws IOException, InterruptedException, ExecutionException {
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            httpclient.start();
            
            HttpGet prepareGet = new HttpGet(downloadUri);
            prepareGet.addHeader("X-ApiKey", this.apiKey);
            HttpAsyncRequestProducer get = HttpAsyncMethods.create(prepareGet);
            File file = new File(localPath + File.separator + filename);
            
            Future<File> future = httpclient.execute(get, new ZeroCopyConsumer<File>(file) {

                @Override
                protected File process(
                        final HttpResponse response, 
                        final File file,
                        final ContentType contentType) throws Exception {
                	if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                		LOGGER.error("Error receiving when downloading file: " + file, response.getStatusLine().getStatusCode());
                		return null;
                	}
                	if (LOGGER.isDebugEnabled())
                		LOGGER.debug("Successful download of file: " + file);
                    return file;
                }

            }, null);
            
            return (future.get() != null);
        } finally {
            httpclient.close();
        }
	}*/
}
