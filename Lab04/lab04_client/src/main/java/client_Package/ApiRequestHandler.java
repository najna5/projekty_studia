package client_Package;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiRequestHandler {
    private int totalPages = 1;

    public int getTotalPages() {
        return totalPages;
    }

    public String apiUrl(ApiParameters params, int page) {
        StringBuilder httpUrl = new StringBuilder("https://api.nfz.gov.pl/app-itl-api/queues?page=" + page);
        httpUrl.append("&limit=25&format=json&case=1")
                .append("&province=").append(params.getProvince());
        if (!params.getBenefit().isEmpty()) {
            httpUrl.append("&benefit=").append(params.getBenefit());
        }
        httpUrl.append("&benefitForChildren=").append(params.getForChildren());
        if (!params.getCity().isEmpty()){
            httpUrl.append("&locality=").append(params.getCity());
        }
        httpUrl.append("&api-version=1.3");
        return httpUrl.toString();
    }

    //sends request to api
    public List<String[]> apiRequest(ApiParameters params, int page) {
        List<String[]> tableData = new ArrayList<>();
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl(params, page)))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                QueueResponse queueResponse = parseJson(response.body());
                tableData.addAll(extractTableData(queueResponse));

                if(queueResponse.links !=null){
                    totalPages = extractPageNumber(queueResponse.links.last);
                }
            } else {
                System.err.println("Błąd podczas wykonywania żądania: " + response.statusCode());
            }

        }catch (IOException | InterruptedException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
        return tableData;
    }

    //uses gson to handle json file, returns List
    private QueueResponse parseJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<QueueResponse>() {
        }.getType());
    }

    private List<String[]> extractTableData(QueueResponse response) {
        List<String[]> rows = new ArrayList<>();

        if(response.data != null) {
            for (QueueData queue : response.data) {
                if (queue.attributes != null) {
                    String benefit = queue.attributes.benefit != null ? queue.attributes.benefit : "N/A";
                    String address = queue.attributes.address != null ? queue.attributes.address : "N/A";
                    String locality = queue.attributes.locality != null ? queue.attributes.locality : "N/A";
                    String date = queue.attributes.dates != null && queue.attributes.dates.date != null
                            ? queue.attributes.dates.date : "N/A";

                    rows.add(new String[]{benefit, address, locality, date});
                }
            }
        }
        return rows;
    }


    private int extractPageNumber(String link) {
        if (link != null && link.contains("page=")) {
            String pageParam = link.split("page=")[1].split("&")[0];
            return Integer.parseInt(pageParam);
        }
        return 1;
    }

}


/**************classes used by parseJson()************/
class Dates {
    String date;
}

//elements I find important in results
class Attributes {
    String benefit;
    String address;
    String locality;
    Dates dates;
}

class QueueData {
    Attributes attributes;
}

class Links{
    String last;
}

class QueueResponse {
    List<QueueData> data;
    Links links;
}
/***************************************************/


