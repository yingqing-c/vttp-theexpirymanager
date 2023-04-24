package com.example.demo.features.controllers;

import java.io.IOException;
import java.util.*;

import com.example.demo.features.models.Item;
import com.example.demo.features.repositories.ItemRepository;
import com.example.demo.features.services.GCalService;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

@CrossOrigin(origins = "*")
@Controller
public class GoogleCalController {

    private final static Log logger = LogFactory.getLog(GoogleCalController.class);
    private static final String APPLICATION_NAME = "TheExpiryManager";
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar service;
    private static com.google.api.services.calendar.model.Calendar appCalendar;

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    @Value("${google.client.client-id}")
    private String clientId;
    @Value("${google.client.client-secret}")
    private String clientSecret;
    @Value("${google.client.redirectUri}")
    private String redirectURI;

    @Autowired
    GCalService gCalService;
    @Autowired
    ItemRepository itemRepository;

    @RequestMapping(value = "/login/oauth2/google", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        return new RedirectView(authorize());
    }

    @PostMapping(value = "/google/addToCalendar", params = "code")
    public ResponseEntity<String> addToCalendar(@RequestParam(value = "code") String code, @RequestBody Item[] items) {
        oauth2Callback(code);
        Set<Event> events = new HashSet<>(items.length);
        for (Item item : items) {
            Optional<Item> itemFromDb = itemRepository.findById(item.getId());
            if (itemFromDb.isEmpty()) {
                logger.info("This item no longer exists");
                continue;
            }
            if (itemFromDb.get().getGoogleCalEventId() == null) {
                // TODO: Future improvement, check if event still exists using event id
                Event event = gCalService.createEvent(item.getId(), item.getItemName(), item.getRemarks(), item.getExpiryDate());
                events.add(event);
                try {
                    event = service.events().insert("primary", event).execute();
                    logger.info("Event created:" + event.getHtmlLink());
                    String eventId = event.getHtmlLink().split("eid=")[1];
                    itemRepository.updateItemSetGoogleCalEventId(eventId, item.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/login/oauth2/google", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            service = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
        }
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    private String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            Details web = new Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        System.out.println("authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }
}