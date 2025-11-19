package org.example.cloudpos.discount.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.discount.domain.Discount;
import org.example.cloudpos.discount.dto.owner.create.DiscountCreateIdRequest;
import org.example.cloudpos.discount.dto.kiosk.DiscountKioskResponse;
import org.example.cloudpos.discount.dto.kiosk.select.DiscountSelectResponse;
import org.example.cloudpos.discount.dto.owner.DiscountOwnerResponse;
import org.example.cloudpos.discount.dto.owner.delete.DiscountDeleteRequest;
import org.example.cloudpos.discount.dto.owner.update.DiscountUpdateRequest;
import org.example.cloudpos.discount.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    /**
     * 할인 데이터를 관리하는 JPA Repository.
     */
    private final DiscountRepository discountRepository;

    @Transactional(readOnly = true)
    public List<DiscountKioskResponse> kioskFindAll(String inventoryId) {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findInventoryCustomerDiscount(inventoryId, now);
    }
    @Transactional(readOnly = true)
    public List<DiscountOwnerResponse>  ownerFindAll(String inventoryId) {

        return discountRepository.findInventoryOwnerDiscount(inventoryId);
    }


    public String createDiscount(DiscountCreateIdRequest req){
        String ulid = UlidCreator.getUlid().toString();
        Discount discount = new Discount(
                ulid,
                req.inventoryId(),
                req.productId(),
                req.name(),
                req.amount(),
                req.discountStart(),
                req.discountEnd()
        );
        discountRepository.save(discount);
        return ulid;
    }
    public DiscountSelectResponse discountSelect(String productId,String inventoryId, String customerDiscountId) {
        LocalDateTime now = LocalDateTime.now();

        // 1. DB에서 할인 정보 조회(유효기간 포함)
        Discount discount = discountRepository
                .selectKioskDiscount(productId, inventoryId, customerDiscountId, now)
                .orElseThrow(() -> new IllegalArgumentException("해당 할인 없음"));

        return new DiscountSelectResponse(
                discount.getProductId(),
                discount.getInventoryId(),
                discount.getDiscountId(),
                discount.getName(),
                discount.getAmount()
        );
    }

    // 수정
    public void discountUpdate(String discountId, DiscountUpdateRequest req){
        Discount discount = discountRepository.findByDiscountId(discountId)
                .orElseThrow(() -> new IllegalArgumentException("해당 할인 없음"));

        if(req.name() != null) {
            discount.setName(req.name());
        }
        if(req.amount() != null){
            discount.setAmount(req.amount());
        }
        if(req.discountStart() != null){
            discount.setDiscountStart(req.discountStart());
        }
        if(req.discountEnd() != null){
            discount.setDiscountEnd(req.discountEnd());
        }

        discountRepository.save(discount);
    }

    // 삭제
    public void discountDelete(DiscountDeleteRequest req){
        Discount discount = discountRepository.disocuntSearch(req.discountId(), req.inventoryId(), req.productId())
                .orElseThrow(() -> new IllegalArgumentException("해당 할인 정보가 존재하지 않음"));

        discountRepository.delete(discount);
    }
}
