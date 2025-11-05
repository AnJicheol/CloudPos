package org.example.cloudpos.inventory.exception;

/**
 * 주어진 inventoryId에 해당하는 매장이 존재하지 않을 때 발생하는 예외입니다.
 */
public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String inventoryId) {
        super("해당 매장이 존재하지 않습니다. inventoryId=" + inventoryId);
    }
}
