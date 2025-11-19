package org.example.cloudpos.discount.service;

import org.example.cloudpos.discount.domain.Discount;
import org.example.cloudpos.discount.dto.owner.create.DiscountCreateIdRequest;
import org.example.cloudpos.discount.dto.kiosk.DiscountKioskResponse;
import org.example.cloudpos.discount.dto.kiosk.select.DiscountSelectResponse;
import org.example.cloudpos.discount.dto.owner.DiscountOwnerResponse;
import org.example.cloudpos.discount.dto.owner.delete.DiscountDeleteRequest;
import org.example.cloudpos.discount.dto.owner.update.DiscountUpdateRequest;

import java.util.List;
public interface DiscountService {

    List<DiscountKioskResponse> kioskFindAll(String inventoryId);

    List<DiscountOwnerResponse>  ownerFindAll(String inventoryId);

    DiscountSelectResponse discountSelect(String productId,String inventoryId, String discountId);

    String createDiscount(DiscountCreateIdRequest req);

    void discountUpdate(String discountId, DiscountUpdateRequest req);

    void discountDelete(DiscountDeleteRequest req);
    List<Discount> getDiscountList(List<String> productIdList);
}
