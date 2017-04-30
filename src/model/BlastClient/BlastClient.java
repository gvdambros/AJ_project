package model.BlastClient;

/**
 * Created by gvdambros on 1/28/17.
 */
        import java.io.*;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.net.URLEncoder;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class BlastClient {
    public enum BlastProgram {megablast, blastn, blastp, rpsblast, blastx, tblastn, tblastx}

    public enum Status {stopped, searching, hitsFound, noHitsFound, failed, unknown, canceled}

    public static boolean verbose = false;
    private final static String baseURL = "https://blast.ncbi.nlm.nih.gov/blast/Blast.cgi";

    private BlastProgram program;
    private String database;
    private String requestId;
    private int estimatedTime;
    private int actualTime;
    private long startTime;

    public BlastClient(String program, String database) {
        this.program = stringToProgram(program);
        this.database = database;
        requestId = null;
        estimatedTime = -1;
        actualTime = -1;
        startTime = -1;
    }

    /**
     * constructor
     */
    public BlastClient() {
        program = BlastProgram.blastp;
        database = "nr";
        requestId = null;
        estimatedTime = -1;
        actualTime = -1;
        startTime = -1;
    }

    /**
     * launch the search
     *
     * @param query
     * @return request Id
     */
    public String startSearch(String query) {
        requestId = null;
        estimatedTime = -1;
        actualTime = -1;
        startTime = System.currentTimeMillis();
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put("CMD", "Put");
            params.put("PROGRAM", program.toString());
            params.put("DATABASE", database);
            params.put("QUERY", query);
            final String response = postRequest(baseURL, params);
            requestId = parseRequestId(response);
            estimatedTime = parseEstimatedTime(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestId;
    }

    /**
     * request the current status
     *
     * @return status
     */
    public Status getStatus() {
        if (requestId == null)
            return Status.failed;
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put("CMD", "Get");
            params.put("FORMAT_OBJECT", "SearchInfo");
            params.put("RID", requestId);
            final String response = getRequest(baseURL, params);

            Status status = Status.unknown;
            boolean thereAreNoHits = false;
            for (String aLine : getLinesBetween(response, "QBlastInfoBegin", "QBlastInfoEnd")) {
                if (aLine.contains("Status=")) {
                    switch (aLine.replaceAll("Status=", "").trim()) {
                        case "WAITING":
                            status = Status.searching;
                            break;
                        case "FAILED":
                            status = Status.failed;
                            break;
                        case "READY":
                            actualTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
                            status = Status.hitsFound; // will check whether hits really found
                            break;
                        default:
                        case "UNKNOWN":
                            status = Status.unknown;
                    }
                } else if (aLine.contains("ThereAreHits=no"))
                    thereAreNoHits = true;
            }
            if (status == Status.hitsFound && thereAreNoHits)
                status = Status.noHitsFound;
            return status;
        } catch (IOException e) {
            e.printStackTrace();
            return Status.unknown;
        }
    }

    /**
     * request the alignments
     *
     * @return gets the alignments
     */
    public List<String> getAlignments() {
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put("CMD", "Get");
            params.put("FORMAT_TYPE", "Text");
            params.put("RID", requestId);
            return getLinesBetween(getRequest(baseURL, params), "<PRE>", null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get the blast program
     *
     * @return blast program
     */
    public BlastProgram getProgram() {
        return program;
    }

    /**
     * set the blast program
     *
     * @param program
     * @return
     */
    public void setProgram(BlastProgram program) {
        this.program = program;
    }

    /**
     * get the database
     *
     * @return database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * set the database
     *
     * @param database
     * @return
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * get estimated time in seconds
     *
     * @return time or -1
     */
    public int getEstimatedTime() {
        return estimatedTime;
    }

    public int getActualTime() {
        return actualTime;
    }

    public String getRequestId() {
        return requestId;
    }

    /**
     * get the request id from a response text
     *
     * @param response
     * @return request id or null
     */
    private static String parseRequestId(String response) {
        for (String aLine : getLinesBetween(response, "QBlastInfoBegin", "QBlastInfoEnd")) {
            if (aLine.contains("    RID = ")) {
                return aLine.replaceAll("    RID = ", "").trim();

            }
        }
        return null;
    }

    /**
     * get the estimated time
     *
     * @param response
     * @return time or -1
     */
    private static Integer parseEstimatedTime(String response) {
        for (String aLine : getLinesBetween(response, "QBlastInfoBegin", "QBlastInfoEnd")) {
            if (aLine.contains("    RTOE = ")) {
                return Integer.parseInt(aLine.replaceAll("    RTOE = ", "").trim());

            }
        }
        return -1;
    }

    /**
     * get a delimited set of lines
     *
     * @param text
     * @param afterLineEndingOnThis  start reporting lines after seeing a line ending on this, or from beginning, if null
     * @param beforeLineStartingWithThis stop reporting lines upon seeing a line starting on this, or a the end, if null
     * @return delimited text
     */
    public static List<String> getLinesBetween(String text, String afterLineEndingOnThis, String beforeLineStartingWithThis) {
        final List<String> lines = new ArrayList<>();
        boolean reporting=(afterLineEndingOnThis == null);
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String aLine;
            while ((aLine = reader.readLine()) != null) {
                if(reporting) {
                    if(beforeLineStartingWithThis!=null && aLine.startsWith(beforeLineStartingWithThis))
                        reporting=false;
                    else
                        lines.add(aLine);
                }
                else if(afterLineEndingOnThis!=null && aLine.endsWith(afterLineEndingOnThis)) {
                    reporting=true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * get request
     *
     * @param parameters
     * @return response
     * @throws IOException
     */
    private static String getRequest(String baseURL, Map<String, Object> parameters) throws IOException {
        final StringBuilder urlString = new StringBuilder();
        urlString.append(baseURL);
        boolean first = true;
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            if (first) {
                urlString.append("?");
                first = false;
            } else
                urlString.append("&");
            urlString.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            urlString.append('=');
            urlString.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        if (verbose) {
            System.err.println("GET " + baseURL);
        }

        final URL url = new URL(urlString.toString());
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("charset", "utf-8");
        connection.setDoOutput(true);
        connection.connect();

        final StringBuilder response = new StringBuilder();
        try (Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            for (int c; (c = in.read()) >= 0; ) {
                response.append((char) c);
            }
        }
        if (verbose)
            System.err.println("Response " + response.toString());
        return response.toString();
    }

    /**
     * post request
     *
     * @param parameters
     * @return response
     * @throws IOException
     */
    private static String postRequest(String baseURL, Map<String, Object> parameters) throws IOException {
        final StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        if (verbose) {
            System.err.println("POST " + baseURL);
            System.err.println(postData.toString());
        }

        final URL url = new URL(baseURL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(postDataBytes);

        final StringBuilder response = new StringBuilder();
        try (Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            for (int c; (c = in.read()) >= 0; ) {
                response.append((char) c);
            }
        }
        if (verbose)
            System.err.println("Response " + response.toString());
        return response.toString();
    }

    private BlastProgram stringToProgram(String program){
        switch(program){
            case("megablast"):
                    return BlastProgram.megablast;
            case("blastn"):
                return BlastProgram.blastn;
            case("blastp"):
                return BlastProgram.blastp;
            case("rpsblast"):
                return BlastProgram.rpsblast;
            case("blastx"):
                return BlastProgram.blastx;
            case("tblastn"):
                return BlastProgram.tblastn;
            case("tblastx"):
                return BlastProgram.tblastx;
            default:
                return null;
        }
    }


    public static void main(String[] args) throws InterruptedException {
        String sequence = "TAATTAGCCATAAAGAAAAACTGGCGCTGGAGAAAGATATTCTCTGGAGCGTCGGGCGAGCGATAATTCAGCTGATTATTGTCGGCTATGTGCTGAAGTAT";

        final BlastClient remoteBlastClient = new BlastClient();
        remoteBlastClient.setProgram(BlastProgram.blastx);
        remoteBlastClient.setDatabase("nr");

        remoteBlastClient.startSearch(sequence);

        System.err.println("Request id: " + remoteBlastClient.getRequestId());
        System.err.println("Estimated time: " + remoteBlastClient.getEstimatedTime() + "s");

        Status status = null;
        do {
            if (status != null)
                Thread.sleep(5000);
            status = remoteBlastClient.getStatus();
        }
        while (status == Status.searching);

        switch (status) {
            case hitsFound:
                for (String line : remoteBlastClient.getAlignments()) {
                    System.out.println(line);
                }
                break;
            case noHitsFound:
                System.err.println("No hits");
                break;
            default:
                System.err.println("Status: " + status);
        }

        System.err.println("Actual time: " + remoteBlastClient.getActualTime() + "s");
    }
}
