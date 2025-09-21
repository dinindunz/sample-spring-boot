# sample-spring-boot

A sample Spring Boot e-commerce application demonstrating order management and workflow.

## Order Workflow

The application supports the following order statuses in sequence:

1. **PENDING** - Initial status when order is created
2. **CONFIRMED** - Order has been confirmed and payment processed
3. **PREPARING** - Order is being prepared for shipment (items being picked, packed, etc.)
4. **PROCESSING** - Order is being processed (legacy status, maintained for compatibility)
5. **SHIPPED** - Order has been shipped with tracking information
6. **DELIVERED** - Order has been delivered to customer
7. **CANCELLED** - Order has been cancelled
8. **RETURNED** - Order has been returned by customer

### Order Status Transitions

The typical order flow follows this sequence:
```
PENDING → CONFIRMED → PREPARING → SHIPPED → DELIVERED
```

Alternative flows:
- Orders can be **CANCELLED** from any status before **SHIPPED**
- Orders can be **RETURNED** after **DELIVERED**
- **PROCESSING** status is maintained for backward compatibility

### Helper Methods

The `Order` entity provides convenient methods for status transitions:
- `markAsPreparing()` - Transitions order to PREPARING status
- `markAsShipped(trackingNumber)` - Transitions to SHIPPED with tracking info
- `markAsDelivered()` - Transitions to DELIVERED status
