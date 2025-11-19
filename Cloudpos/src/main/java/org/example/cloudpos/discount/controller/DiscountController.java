package org.example.cloudpos.discount.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.discount.dto.owner.create.DiscountCreateIdRequest;
import org.example.cloudpos.discount.dto.kiosk.DiscountKioskResponse;
import org.example.cloudpos.discount.dto.kiosk.select.DiscountSelectRequest;
import org.example.cloudpos.discount.dto.kiosk.select.DiscountSelectResponse;
import org.example.cloudpos.discount.dto.owner.DiscountOwnerResponse;
import org.example.cloudpos.discount.dto.owner.delete.DiscountDeleteRequest;
import org.example.cloudpos.discount.dto.owner.delete.DiscountDeleteResponse;
import org.example.cloudpos.discount.dto.owner.update.DiscountUpdateRequest;
import org.example.cloudpos.discount.dto.owner.update.DiscountUpdateResponse;
import org.example.cloudpos.discount.service.DiscountServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountServiceImpl disCountService;

    // 1. discountId ULID생성 + 할인생성
    @PostMapping("/owner/create")
    public ResponseEntity<String> createDiscountId(@Valid @RequestBody DiscountCreateIdRequest discountCreateIdRequest){
        String discountId = disCountService.createDiscount(discountCreateIdRequest);
        return ResponseEntity.created(URI.create("/api/discounts/" + discountId)).body(discountId);
    }


    // 2. 키오스크에 효시될 모든 할인
    @GetMapping("/kiosk")
    @Operation(
            summary = "전체 할인 조회",
            description = "등록 된 모든 할인 정보 반환"
    )
    @ApiResponse(
            responseCode = "200",
            description = "모든 할인 정보 반환 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiscountKioskResponse.class)
            )
    )
    public ResponseEntity<List<DiscountKioskResponse>> getKioskDiscount(@RequestParam String inventoryId) {
        return ResponseEntity.ok(disCountService.kioskFindAll(inventoryId));
    }

    // 3. 점주에게 보여질 할인 리스트
    @GetMapping("/owner")
    public ResponseEntity<List<DiscountOwnerResponse>> getOwnerDiscount(@RequestParam String inventoryId){
        return ResponseEntity.ok(disCountService.ownerFindAll(inventoryId));
    }



    // 4. 할인 선택
    @GetMapping("/kiosk/select")
    @Operation(
            summary = "단일 할인 선택",
            description = "단일 할인 정보 반환"
    )
    @ApiResponse(
            responseCode = "200",
            description = "단일 정보 반환 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiscountSelectRequest.class)
            )
    )
    public ResponseEntity<DiscountSelectResponse> getDiscountSelect(@ModelAttribute DiscountSelectRequest selectRequest){
        DiscountSelectResponse response = disCountService.discountSelect(
                selectRequest.productId(),
                selectRequest.inventoryId(),
                selectRequest.discountId()
        );
        return ResponseEntity.ok(response);
    }

    // 5. 할인 수정
    @PatchMapping("/owner/{discountId}")
    public ResponseEntity<DiscountUpdateResponse> updateDiscount(@PathVariable String discountId,
                                                                 @RequestBody DiscountUpdateRequest request){
        disCountService.discountUpdate(discountId, request);
        return ResponseEntity.noContent().build();
    }

    // 6. 할인 삭제
    @DeleteMapping("owner/{discountId}")
    public ResponseEntity<DiscountDeleteResponse> deleteDiscount(@PathVariable String discountId,
                                                                 @RequestBody DiscountDeleteRequest request){
        disCountService.discountDelete(request);
        return ResponseEntity.noContent().build();
    }

}