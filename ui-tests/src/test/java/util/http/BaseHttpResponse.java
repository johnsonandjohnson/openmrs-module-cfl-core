package util.http;

import java.util.List;

public class BaseHttpResponse {

    private List<ResponseResult> results;

    public BaseHttpResponse() { }

    public List<ResponseResult> getResults() {
        return results;
    }

    public void setResults(List<ResponseResult> results) {
        this.results = results;
    }
}
