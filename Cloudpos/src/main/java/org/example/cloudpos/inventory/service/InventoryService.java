package org.example.cloudpos.inventory.service;

import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 인벤토리(매장) 도메인의 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 *
 * <p>매장 생성, 매장에 상품 추가/조회/삭제와 같은 기능을 선언하며,
 * 실제 구현은 {@link InventoryServiceImpl} 에서 수행됩니다.</p>
 *
 * <p>서비스 계층은 컨트롤러와 리포지토리 사이의 중간 계층으로서,
 * 트랜잭션 경계 및 도메인 규칙을 담당합니다.</p>
 *
 * @since 1.0
 */
public interface InventoryService {

    /**
     * 신규 매장을 생성합니다.
     *
     * <p>매장은 ULID를 외부 식별자로 사용하며,
     * 생성 시 상품은 포함되지 않습니다.</p>
     *
     * @param req 매장 생성 요청 DTO
     * @return 생성된 매장의 ULID (inventoryId)
     */
    String create(InventoryCreateRequest req);

    /**
     * 매장에 신규 상품을 등록합니다.
     *
     * <p>지정된 매장(inventoryId)에 소속된 상품을 새로 생성하고 등록합니다.
     * 상품은 특정 매장에 종속되며, 매장을 지정하지 않고 단독으로 생성될 수 없습니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param req         상품 생성 요청 정보 (상품명, 가격, 상태, 이미지 등)
     * @return 생성된 매장 상품 정보
     */
    ProductResponse addProduct(String inventoryId, ProductCreateRequest req, MultipartFile image);

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 내 상품 정보를 담은 DTO 목록
     */
    List<InventoryProductResponse> listProducts(String inventoryId);

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId   제거할 상품의 식별자
     */
    void removeProduct(String inventoryId, String productId);
}
