package com.example.demo.features.services;

import com.example.demo.auth.models.User;
import com.example.demo.auth.repositories.UserRepository;
import com.example.demo.features.models.Item;
import com.example.demo.features.repositories.ItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExpiryService {
    private static final Logger logger = LogManager.getLogger(ExpiryService.class);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;

    public boolean addItem(String username, String itemName, String imageKey, String fileName, String remarks, Date expiryDate) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            // should not happen
            logger.error("addItem failed because there is no user associated with " + username + "!");
            return false;
        }
        Item item = new Item();
        item.setItemName(itemName);
        item.setExpiryDate(expiryDate);
        item.setRemarks(remarks);
        item.setUser(user.get());
        item.setImageUrl(imageKey);
        item.setFileName(fileName);
        item = itemRepository.save(item);
        return itemRepository.existsById(item.getId());

    }

    public List<Item> getItems(String username) {
        return itemRepository.findAllByUser_Username(username);
    }

    public boolean deleteItem(Item item) {
        Long id = item.getId();
        // delete from s3
        if (item.getImageUrl() != null) {
            s3Service.deleteImg(item.getImageUrl());
        }
        // delete from db
        itemRepository.deleteById(id);
        return !itemRepository.existsById(id);
    }
}
