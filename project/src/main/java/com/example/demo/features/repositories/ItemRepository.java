package com.example.demo.features.repositories;

import com.example.demo.features.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUser_Username(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE items SET google_cal_event_id =:gcalEventId where id =:itemId", nativeQuery = true)
    int updateItemSetGoogleCalEventId(@Param("gcalEventId") String googleCalEventId, @Param("itemId") Long itemId);

}
