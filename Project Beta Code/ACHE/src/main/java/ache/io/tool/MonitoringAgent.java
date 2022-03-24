package ache.io.tool;

import ache.ACHEContext;
import ache.io.config.ACHEConfig;
import ache.io.config.ArgumentConfig;
import ache.io.exception.ExceptionHandler;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Log4j2
public class MonitoringAgent implements ACHEContext {

    public synchronized void addPlatformUserCount(String platform, int roomUserCount) {
        if(userCountMap.containsKey(platform)) {
            userCountMap.put(platform, userCountMap.get(platform).intValue() + 1);
        } else {
            userCountMap.put(platform, 1);
        }
        platformUserTotalCount++;
    }

    public synchronized void removePlatformUserCount(String platform, int roomUserCount) {
        if(userCountMap.containsKey(platform)) {
            if(userCountMap.get(platform).intValue()  <= 0) {
                log.error("UserCountMap is Minus Value!! - {}", platform);
            } else {
                userCountMap.put(platform, userCountMap.get(platform).intValue() - 1);
            }
        } else {
            log.error("UserCountMap is Not Contains!! - {}", platform);
        }
        platformUserTotalCount--;
    }

    // FIXME: 3초마다 CPU 모니터링과 함께 체크: Cloud, Viewer, Platform User Count
    public synchronized void checkTotalUserCount(int roomUserCount) {
        AtomicInteger currentUserMapCount = new AtomicInteger();
        if(roomUserCount != platformUserTotalCount) {
            userCountMap.forEach( (p, c) -> {
                currentUserMapCount.addAndGet(c.intValue());
            });
            String msg = "Total Member Count is Not Valid - UserMap: "+ roomUserCount +" vs UserCountMap:" + currentUserMapCount;
            log.warn(msg);
        }
    }

    private int platformUserTotalCount = 0;
    private Map<String, Integer> userCountMap = new ConcurrentHashMap<>();



    public String sendRegister() throws Exception {
        registerParameters.add(new BasicNameValuePair(PARM_PORT, Integer.toString(this.acheConfig.getPlayPort())));
        registerParameters.add(new BasicNameValuePair(PARM_SHOP, this.argumentConfig.getProxyInfo_shop()));
        registerParameters.add(new BasicNameValuePair(PARM_DMC, this.argumentConfig.getProxyInfo_dmc()));
        registerParameters.add(new BasicNameValuePair(PARM_DEVICE, this.argumentConfig.getProxyInfo_device()));
        registerParameters.add(new BasicNameValuePair(PARM_MAX, Integer.toString(this.argumentConfig.getProxyInfo_max())));
        registerParameters.add(new BasicNameValuePair(PARM_LIMIT, Integer.toString(this.argumentConfig.getProxyInfo_limit())));
        registerParameters.add(new BasicNameValuePair(PARM_ENV, this.acheConfig.getActive()));

        registerPostRequest = new HttpPost(new URIBuilder(URL_ENGINE_REGISTER).addParameters(registerParameters).build());
        registerPostRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        registerPostRequest.setEntity(new UrlEncodedFormEntity(registerParameters, ENCODE_FORMAT_UTF_8));

        jsonParser.reset();
        registerPostRequest.setConfig(RequestConfig.custom().build());
        log.info("{} Register Request {}", NET_PROXY, registerPostRequest.getRequestLine());
        JSONObject jsonObject = (JSONObject) jsonParser.parse(sendPostData(httpclient, registerPostRequest));
        String resultCode = jsonObject.get(RESULT_CODE).toString();

        log.info("{} Register Response {}", NET_PROXY, jsonObject);

        if(resultCode.equalsIgnoreCase(RESULT_SUCCESS) || resultCode.equalsIgnoreCase(RESULT_ENGINE_EXIST)) {
            engineName = jsonObject.get("engineName").toString();
            log.info("{} {} Engine!! - {} - {}", NET_PROXY, (resultCode.equalsIgnoreCase(RESULT_SUCCESS)  ? "New" : "Exist"), engineName,  jsonObject);
        } else {
            throw new Exception("Engine Register Fail!! - " + resultCode);
        }
        return engineName;
    }

    public void sendUserCount(CloseableHttpClient httpclient) {
        try {
            String count = Integer.toString(userCount);
            setCountParameters.remove(userValuePair);
            userValuePair = new BasicNameValuePair(PARM_COUNT, count);
            setCountParameters.add(userValuePair);

            setCountURI.setParameters(setCountParameters);

            setCountPostRequest = new HttpPost(setCountURI.build());
            setCountPostRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            setCountPostRequest.setEntity(new UrlEncodedFormEntity(setCountParameters, ENCODE_FORMAT_UTF_8));

//            if (userCount == 0) { // NOTE: Address 출력
//                log.info("{} Send User Count {}", NET_PROXY, setCountPostRequest.getRequestLine());
//            }

            if(userCount >= ROOM_MAX_USER_COUNT) {
                log.error("User Count is Not Valid - {}", userCount);
            }

            jsonParser.reset();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(sendPostData(httpclient, setCountPostRequest));
            String resultCode = jsonObject.get(RESULT_CODE).toString();

            if (resultCode.equalsIgnoreCase(RESULT_SUCCESS)) {
                log.info("{} Send User Count [ {} ] Complete!! - {}", NET_PROXY, count, jsonObject);
            } else {
                throw new Exception(NET_PROXY + "Send User Count FAIL!! - " + resultCode);
            }
        } catch (Exception e) {
            log.error("SendUserCount Fail", e);
        }
    }

    public String sendPostData(CloseableHttpClient httpclient , HttpPost post) throws Exception {
//        log.info("Executing request " + post.getRequestLine());
        return httpclient.execute(post, responseHandler);
    }

    public String sendPostDataWithTimeout(HttpPost post) throws Exception {
        RequestConfig.Builder builder = RequestConfig.custom();

        try {
            RequestConfig config = builder.build();
            post.setConfig(config);
            return sendPostData(httpclient, post);
        } catch (Exception e) {
            throw e;
        }
    }

    private void createResponseHandler() {
        // Create a custom response handler
        responseHandler = response -> {
            responseStatus = response.getStatusLine().getStatusCode();
            if (responseStatus >= HttpURLConnection.HTTP_OK && responseStatus < HttpURLConnection.HTTP_MULT_CHOICE) {
                responseStr = "";
                if (response.getEntity() != null) {
                    responseStr = EntityUtils.toString(response.getEntity());
                }
                if(responseStatus != 200) {
                    log.warn("{} {}{}", NET_PROXY, "Proxy Server Response Err: ", responseStatus);
                    ExceptionHandler.fireWarningMsg("Proxy Server Err");
                }
                return responseStr;
            } else {
                throw new ClientProtocolException(NET_PROXY + "Unexpected response status: " + responseStatus);
            }
        };
    }

    public void startThread() throws Exception {
        poolingConnManager.setDefaultMaxPerRoute(5);
        poolingConnManager.setMaxTotal(5);
        threads = new MultiHttpClientConnThread[1];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MultiHttpClientConnThread(httpclient);
        }
        for (MultiHttpClientConnThread thread : threads) {
            thread.start();
        }
    }

    class MultiHttpClientConnThread extends Thread {
        public void run() {
            isAlive = true;
            while (isAlive) {
                // FIXME: 향후 하드웨어 사용양을 검출해주면 삭제
                //  너무 많이 발생해 임시로 막음
//                if (Monitoring.traceCPU(CPU_AVERAGE_LIMIT)) {
//                    Monitoring.traceHeapMemory();
//                    Monitoring.tracePhysicalMemory();
//                }
                // FIXME: 향후 유저카운트를 플랫폼별로 남기도록 작업되면 삭제
                sendUserCount(this.client);
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    log.error("MultiHttpClientConnThread Error", e);
                }
            }
        }

        public void destroy() {
            isAlive = false;
            interrupt();
        }

        protected MultiHttpClientConnThread(CloseableHttpClient client) {
            this.client = client;
            sleepTime = argumentConfig.getProxyInfo_pingInterval();
        }

        private long sleepTime;

        private boolean isAlive = false;
        private CloseableHttpClient client;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public void start() throws Exception {
        /**
         * Create main request executor
         * We copy the instance fields to avoid changing them, and rename to avoid accidental use of the wrong version
         */
        poolingConnManager = new PoolingHttpClientConnectionManager();
        httpclient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        builder = RequestConfig.custom();

        createResponseHandler();

        engineName = sendRegister();
        setCountURI = new URIBuilder(URL_ENGINE_SET_COUNT);
        setCountParameters.add(new BasicNameValuePair(ENGINE_NAME, engineName));
        sendUserCount(httpclient);

        startThread();
    }

    public void destroy() {
        if(threads == null) return;
        for (MultiHttpClientConnThread thread : threads) {
            thread.destroy();
        }
    }

    private MonitoringAgent(ACHEConfig acheConfig, ArgumentConfig argumentConfig) {
        this.acheConfig = acheConfig;
        this.argumentConfig = argumentConfig;
        URL_DEFAULT = this.argumentConfig.getProxyInfo_url();
        URL_ENGINE_REGISTER = URL_DEFAULT+"register/";
        URL_ENGINE_SET_COUNT = URL_DEFAULT+"setcount/";
    }

    private final ArgumentConfig argumentConfig;
    private final ACHEConfig acheConfig;

    private final static String RESULT_SUCCESS = "0";
    private final static String RESULT_ENGINE_EXIST = "101";
    private final static String RESULT_CODE = "code";

    private URIBuilder setCountURI;
    private String engineName = null;
    private int userCount;
    private BasicNameValuePair userValuePair;

    private MultiHttpClientConnThread[] threads;
    private PoolingHttpClientConnectionManager poolingConnManager;
    private CloseableHttpClient httpclient = null;
    private RequestConfig.Builder builder;
    private HttpClient client = HttpClientBuilder.create().build();
    private String URL_DEFAULT, URL_ENGINE_REGISTER, URL_ENGINE_SET_COUNT;
    private ArrayList<NameValuePair> registerParameters = new ArrayList<>();
    private ArrayList<NameValuePair> setCountParameters = new ArrayList<>();
    private HttpPost registerPostRequest;
    private HttpPost setCountPostRequest;
    private JSONParser jsonParser = new JSONParser();
    private String ENCODE_FORMAT_UTF_8 = "UTF-8";

    private int responseStatus;
    private String responseStr;
    private ResponseHandler<String> responseHandler;

    protected final static int TIMEOUT_DEFAULT = 4000;
    protected final static int TIMEOUT_KEEP_ALIVE = -1;

    private final String ENGINE_NAME = "engineName";
    private final String CONTENT_TYPE = "Content-Type";
    private final String APPLICATION_JSON = "application/json";
    private final String PARM_COUNT = "count";
    private final String PARM_PORT = "port";
    private final String PARM_SHOP = "shop";
    private final String PARM_DMC = "dmc";
    private final String PARM_DEVICE = "device";
    private final String PARM_MAX = "max";
    private final String PARM_LIMIT = "limit";
    private final String PARM_ENV = "env";

}
