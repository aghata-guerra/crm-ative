package com.base.CRM.consult;

import com.base.CRM.entity.dto.EmpresaCreateDto;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ConsultApiCnpj {


  public EmpresaCreateDto consultCnpj(String cnpj) throws IOException, InterruptedException {
    String url = "https://api.opencnpj.org/" + cnpj;

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();

    return mapper.readValue(response.body(), EmpresaCreateDto.class);

  }

}
