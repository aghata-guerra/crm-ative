package com.base.CRM.consult;

import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.exception.CnpjNotFoundException;
import com.base.CRM.exception.ServerApiErrorException;
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




    if(response.statusCode() == 200){

      if(response.body().isBlank() || response.body() == null){
        throw new RuntimeException("Resposta da API vazia!");
      }

      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(response.body(), EmpresaCreateDto.class);

    }

    switch (response.statusCode()){
      case 404:
        throw new CnpjNotFoundException();
      case 500:
        throw new ServerApiErrorException("Erro no servidor status code: 500" );
      case 502:
        throw new ServerApiErrorException("Erro no servidor status code: 502" );
      case 503:
        throw new ServerApiErrorException("Erro no servidor status code: 503" );
      case 504:
        throw new ServerApiErrorException("Erro no servidor status code: 504" );
      default:
        throw new ServerApiErrorException("Erro status code: " + response.statusCode());
    }



  }

}
