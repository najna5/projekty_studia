package client_Package;

import java.util.List;


public class NfzApiClient {
    private ApiParameters parameters;
    private final ApiRequestHandler apiRequestHandler;
    private int currentPage = 1;

    public NfzApiClient() {
        this.apiRequestHandler = new ApiRequestHandler();
    }

    public void updateParams(String province, String city, String benefit, Boolean forChildren) {
        this.parameters = new ApiParameters(province, city, benefit, forChildren);
        resetPages();
    }

    private void resetPages() {
        this.currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return apiRequestHandler.getTotalPages();
    }

    public void setCurrentPage(int page) {
        if (page >= 1 && page <= getTotalPages()) {
            this.currentPage = page;
        }
    }

    //sends API request for current page
    public List<String[]> fetchCurrentPage() {
        if (parameters == null) {
            throw new IllegalStateException("parametry żądania nie są podane");
        }
        List<String[]> data = apiRequestHandler.apiRequest(parameters, currentPage);
        return data;
    }
}

