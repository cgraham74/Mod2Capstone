package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
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


    public HttpEntity makeEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<String>(headers);
    }
    public HttpEntity makeEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }

    //Request money from another user (NOT SELF)
    public Transfer requestMoney(int id, BigDecimal amountToReq){

        return new Transfer();
    }

    //Send Money to another user
    public Transfer transferMoney(int accountfrom, int accountto, BigDecimal amount){
        Transfer transfer = new Transfer(2, 2, accountto, accountfrom, amount);
        HttpEntity<Transfer> entity = makeEntity(transfer);
        Transfer newTransfer = null;

        try{
            newTransfer= restTemplate.postForObject(API_BASE_URL + "transfer/transferfunds", entity, Transfer.class);

        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
       return newTransfer;
    }


    public List<User> displayRegisteredUsers(String username){
        List<User> userList = new ArrayList<>();
        try{
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "transferlist?username=" + username, HttpMethod.GET, makeEntity(), User[].class);
            userList = Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return userList;
    }

    //Get list of Transfers from currentUser
    public List<Transfer> transferFromList(Long id) {
        List<Transfer>list = new ArrayList<>();
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfer/transferfrom?id=" + id, HttpMethod.GET, makeEntity(), Transfer[].class );
            list = Arrays.asList((Transfer[])Objects.requireNonNull((Transfer[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    //Get a list of transfers to currentUser
    public List<Transfer> transferToList(Long id) {
        List<Transfer>list = new ArrayList<>();
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfer/transferto?id=" + id, HttpMethod.GET, makeEntity(), Transfer[].class );
            list = Arrays.asList((Transfer[])Objects.requireNonNull((Transfer[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    public List<Transfer> pendingTransfers(Long id){
        List<Transfer> list = new ArrayList<>();
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfer/pending?id=" + id , HttpMethod.GET, makeEntity(), Transfer[].class);
            list = Arrays.asList((Transfer[])Objects.requireNonNull((Transfer[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

}