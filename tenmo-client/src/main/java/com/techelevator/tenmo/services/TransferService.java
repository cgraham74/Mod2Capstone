package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private AccountService accountService;
    private Transfer transfer;
    private String authToken = null;
    private TransferType transferType;
    private TransferStatus transferStatus;
    private ConsoleService consoleService;

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public HttpEntity makeEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<String>(headers);
    }
    public HttpEntity makeEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    //Request money from another user (NOT SELF)
    public Transfer requestMoney(BigDecimal amount, int accountto, int accountfrom){
        Transfer transfer = new Transfer(1,1, accountto, accountfrom,amount);
        Transfer newTransfer = null;
        try{
            newTransfer = restTemplate.postForObject(API_BASE_URL + "transfer/request", makeEntity(transfer), Transfer.class);
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

        return newTransfer;
    }

    //Get a single transfer by its id
    public TransferDTO getTransferById(int id){
        TransferDTO transfer = null;
        try {
            ResponseEntity<TransferDTO> response = restTemplate.exchange(API_BASE_URL + "transfer/transfer?id=" + id, HttpMethod.GET, makeEntity(), TransferDTO.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    //Send Money to another user
    public Transfer transferMoney(int typeid, int accountfrom, int accountto, BigDecimal amount){
        Transfer transfer = new Transfer(2, typeid, accountto, accountfrom, amount);
        Transfer newTransfer = null;
        try{
            newTransfer= restTemplate.postForObject(API_BASE_URL + "transfer/transferfunds", makeEntity(transfer), Transfer.class);

        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
       return newTransfer;
    }

    //Should get a list of transfers that belong to ONE USER!
    public List<TransferDTO>getHistory(Long id){
        List<TransferDTO>list = new ArrayList<>();
        try{
            ResponseEntity<TransferDTO[]> response = restTemplate.exchange(API_BASE_URL + "transfer/history?id=" + id, HttpMethod.GET, makeEntity(), TransferDTO[].class);
            list = Arrays.asList((TransferDTO[])Objects.requireNonNull((TransferDTO[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    //Get list of Transfers with a pending status from the server
    public List<Transfer> pendingTransfers(Long id){
        List<Transfer> list = new ArrayList<>();
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfer/pending?accountfrom=" + id , HttpMethod.GET, makeEntity(), Transfer[].class);
            list = Arrays.asList((Transfer[])Objects.requireNonNull((Transfer[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    public Transfer singlePendingTransfer(Long id){
        Transfer transfer = new Transfer();
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer/pendingbyid?id=" + id, HttpMethod.GET, makeEntity(), Transfer.class);
            transfer = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException ex){
            BasicLogger.log(ex.getMessage());
        }
        return transfer;
    }

    public boolean update(Transfer transfer) {
        boolean success = false;
        try{
            restTemplate.put(API_BASE_URL + "transfer/update?statusid=" + transfer.getTransferstatusid() + "&transferid=" + transfer.getId(), makeEntity(transfer));
            success = true;
        }catch (RestClientResponseException | ResourceAccessException ex){
            BasicLogger.log(ex.getMessage());
        }
        return success;
    }

}
