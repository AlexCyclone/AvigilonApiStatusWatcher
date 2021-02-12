package icu.cyclone.avigilon.services;

import icu.cyclone.avigilon.entities.Camera;
import icu.cyclone.avigilon.exception.CommunicationException;
import icu.cyclone.avigilon.utils.ArgumentParser;
import icu.cyclone.avigilon.utils.FileUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class CliService {
    private static final PropertyService props = PropertyService.getInstance();
    private static final String DATE_FORMAT = props.getProperty("date.format");
    private static final String FILE_PATH = props.getProperty("state.file.path");
    private static final String DELIMITER = props.getProperty("result.value.delimiter");
    private static final String EQUALS = "=";

    private static final String UNDEFINED = "undefined";
    private static final String UNAVAILABLE = "UNAVAILABLE";

    private static final List<Map.Entry<String, Function<Camera, String>>> RESULT_VALUES = new ArrayList<>(6);

    static {
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("Date", CliService::apply));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("Site", camera -> camera.getServer() != null && camera.getServer().getSite() != null
                ? camera.getServer().getSite().getName() : UNDEFINED));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("Server", camera -> camera.getServer() != null
                ? camera.getServer().getName() : UNDEFINED));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("ServerAddress", camera -> camera.getServer() != null
                ? camera.getServer().getIpAddress() : UNDEFINED));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("Camera", Camera::getName));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("Record", camera -> String.valueOf(camera.isRecordedData())));
        RESULT_VALUES.add(new AbstractMap.SimpleEntry<>("State", Camera::getConnectionState));
    }

    private final AvigilonService avigilonService;
    boolean debug;
    private final PrintStream printStream;

    public CliService(String[] args, PrintStream printStream) {
        this.printStream = printStream;
        avigilonService = getService(args);
    }

    private static String apply(Camera camera) {
        return CliService.getDateString();
    }

    private static String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    private AvigilonService getService(String[] args) {
        ArgumentParser parser = new ArgumentParser(args);
        if (parser.isHelp()) {
            return null;
        }
        debug = parser.isDebug();
        return new AvigilonService(parser.getUrlString(), parser.getNetworkPrefix(),
                parser.getUsername(), parser.getPassword(), parser.getUserNonce(), parser.getUserKey());
    }

    public void start() {
        if (avigilonService == null) {
            printHelp();
        } else if (debug) {
            debug();
        } else {
            process();
        }
    }

    private void printHelp() {
        printStream.println(ArgumentParser.getHelp());
    }

    private void process() {
        List<Camera> cameras = getSavedCameras();
        try {
            avigilonService.login();
            cameras = avigilonService.getCameras();
        } catch (CommunicationException | IllegalArgumentException e) {
            updateUnavailableState(cameras);
        } finally {
            avigilonService.logout();
        }
        cameras.forEach(this::printCamera);
        saveCameras(cameras);
    }

    private void updateUnavailableState(List<Camera> cameras) {
        cameras.forEach(camera -> {
            camera.setAvailable(false);
            camera.setConnected(false);
            camera.setConnectionState(UNAVAILABLE);
        });
    }

    private List<Camera> getSavedCameras() {
        try {
            return FileUtils.readList(FILE_PATH);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private void saveCameras(List<Camera> state) {
        try {
            FileUtils.saveList(FILE_PATH, state);
        } catch (IOException ignored) {
        }
    }

    private void printCamera(final Camera camera) {
        printStream.println(RESULT_VALUES
                .stream()
                .map(e -> e.getKey() + EQUALS + e.getValue().apply(camera))
                .collect(Collectors.joining(DELIMITER)));
    }

    private void debug() {
        try {
            avigilonService.login();
            printDebugData("HOST INFO", avigilonService::getServerHost);
            printDebugData("SESSION INFO", avigilonService::getSession);
            printDebugData("CAMERAS INFO", avigilonService::getListCameraObject);
            printDebugData("ENTITIES INFO", avigilonService::getListEntityObject);
            printDebugData("SERVERS INFO", avigilonService::getListServerObject);
            printDebugData("SITES INFO", avigilonService::getListSiteObject);
        } catch (CommunicationException | IllegalArgumentException e) {
            printStream.println(e.getMessage());
        } finally {
            avigilonService.logout();
        }
    }

    private void printDebugData(String title, Supplier<Object> supplier) {
        printStream.println(title);
        try {
            printStream.println(supplier.get());
        } catch (CommunicationException | IllegalArgumentException e) {
            printStream.println(e.getMessage());
        }
        printStream.println();
    }
}
