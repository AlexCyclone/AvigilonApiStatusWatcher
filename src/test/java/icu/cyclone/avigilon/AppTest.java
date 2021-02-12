package icu.cyclone.avigilon;

import icu.cyclone.avigilon.services.CliService;
import icu.cyclone.avigilon.utils.HttpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class AppTest {
    private static final String PARAM_URL = "--url";
    private static final String URL_VALUE = "https://testhost";


    @PrepareForTest({HttpUtils.class})
    @Test
    public void integrationTestSingleCamera() {
        OutputHolder outputHolder = new OutputHolder();
        prepareHttpUtils();
        new CliService(new String[]{PARAM_URL, URL_VALUE}, outputHolder.getPrintStream()).start();
        System.out.println("SINGLE CAMERA RESULT:");
        System.out.println(outputHolder.getOutput());
        Assert.assertEquals("Incorrect result lines count", 1, outputHolder.getNotEmptyOutputLines().size());
    }

    private void prepareHttpUtils() {
        PowerMockito.mockStatic(HttpUtils.class);
        final AvigilonResponseGenerator responseGenerator = new AvigilonResponseGenerator(URL_VALUE, "single-camera");
        PowerMockito
                .when(HttpUtils.sendRequest(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(invocationOnMock -> responseGenerator.getResponse(invocationOnMock.getArgument(0)));
    }
}
