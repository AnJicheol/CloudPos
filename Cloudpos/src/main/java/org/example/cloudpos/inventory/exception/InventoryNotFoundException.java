package org.example.cloudpos.inventory.exception;

/**
 * 주어진 {@code inventoryId}에 해당하는 매장이 존재하지 않을 때 발생하는 예외입니다.
 *
 * <p>매장 관련 작업(상품 추가, 상품 조회 등) 수행 시
 * 지정된 ULID(매장 외부 식별자)에 해당하는 매장이 데이터베이스에 존재하지 않으면
 * {@link org.example.cloudpos.inventory.service.InventoryService} 계층에서 이 예외를 던집니다.</p>
 *
 * <p>컨트롤러 단에서는 {@code 404 Not Found} HTTP 상태 코드로 변환되어 반환됩니다.</p>
 *
 * <h2>예외 발생 조건</h2>
 * <ul>
 *   <li>{@code inventoryRepo.findFirstByInventoryId(...)} 결과가 비어 있는 경우</li>
 *   <li>요청된 매장이 아직 생성되지 않은 경우</li>
 * </ul>
 *
 * @see org.example.cloudpos.inventory.service.InventoryService
 * @since 1.0
 */
public class InventoryNotFoundException extends RuntimeException {

    /**
     * 지정된 매장 ID에 해당하는 매장이 존재하지 않을 때 예외를 생성합니다.
     *
     * @param inventoryId 존재하지 않는 매장의 외부 식별자 (ULID)
     */
    public InventoryNotFoundException(String inventoryId) {
        super("해당 매장이 존재하지 않습니다. inventoryId=" + inventoryId);
    }
}
