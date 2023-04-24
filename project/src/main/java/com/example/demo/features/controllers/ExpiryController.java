package com.example.demo.features.controllers;

import com.example.demo.features.models.Item;
import com.example.demo.features.repositories.ItemRepository;
import com.example.demo.features.services.ExpiryService;
import com.example.demo.features.services.S3Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/expiry")
public class ExpiryController {

    private static final Logger logger = LogManager.getLogger(ExpiryController.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ExpiryService expiryService;

    @PostMapping(path="/addItem",
            consumes= MediaType.MULTIPART_FORM_DATA_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addItem(
            Principal principal,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart String itemName,
            @RequestPart String expiryStr,
            @RequestPart(required = false) String remarks) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date expiry;
        try {
            expiry = formatter.parse(expiryStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid date format: Could not parse date");
        }

        String username = principal.getName();
        if (image == null) {
            expiryService.addItem(username, itemName, null, null, remarks, expiry);
            return ResponseEntity.ok("");
        }

        String imageKey = "";
        try {
            imageKey = s3Service.upload(image, username, itemName);
        } catch (IOException e) {
            logger.error("Could not upload image", e);
            return ResponseEntity.internalServerError().body("Could not upload image");
        }
        boolean success = expiryService.addItem(username, itemName, imageKey, image.getOriginalFilename(), remarks, expiry);
        if (!success) {
            return ResponseEntity.internalServerError().body("Failed to add item");
        }
        return ResponseEntity.ok("Successfully added item");
    }

    @GetMapping(path="/getItems")
    public List<Item> getItems(Principal principal) {
        String username = principal.getName();
        return expiryService.getItems(username);
    }

    @DeleteMapping(path="/deleteItem/{id}")
    public ResponseEntity<String> deleteItem(Principal principal, @PathVariable Long id) {
        String username = principal.getName();
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            return ResponseEntity.ok("This item does not exist");
        }
        if (!item.get().getUser().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This item does not belong to the user making the request!");
        }
        boolean success = expiryService.deleteItem(item.get());
        if (!success) {
            return ResponseEntity.internalServerError().body("Failed to delete item");
        }
        return ResponseEntity.ok("Successfully deleted item with id [" + id + "]");
    }
}
