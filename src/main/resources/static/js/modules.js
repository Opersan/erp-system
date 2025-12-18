// Modules JS - Handles logic for ERP Modules

$(document).ready(function() {
    
    // --- Procurement Module ---

    // List Orders
    if ($('#procurementTable').length) {
        $('#procurementTable').DataTable({
            ajax: {
                url: '/api/procurement/orders',
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                { data: 'supplier.name', defaultContent: 'N/A' }, // Assuming Supplier entity has name
                { 
                    data: 'status',
                    render: function(data) {
                        let badgeClass = 'bg-secondary';
                        if (data === 'APPROVED') badgeClass = 'bg-success';
                        if (data === 'DRAFT') badgeClass = 'bg-warning';
                        return `<span class="badge ${badgeClass}">${data}</span>`;
                    }
                },
                { 
                    data: 'totalAmount',
                    render: function(data) {
                        return '$' + (data ? data.toFixed(2) : '0.00');
                    }
                },
                {
                    data: 'id',
                    render: function(data) {
                        return `
                            <a href="#" class="btn btn-info btn-sm btn-circle"><i class="fas fa-eye"></i></a>
                            <button onclick="approveOrder(${data})" class="btn btn-success btn-sm btn-circle" title="Approve"><i class="fas fa-check"></i></button>
                        `;
                    }
                }
            ]
        });
    }

    // Create Order Form
    if ($('#createPoForm').length) {
        // Add Item Row
        $('#addItemBtn').click(function() {
            const row = `
                <div class="row mb-2 item-row">
                    <div class="col-md-4">
                        <input type="number" class="form-control" placeholder="Item ID" name="itemId" required>
                    </div>
                    <div class="col-md-3">
                        <input type="number" class="form-control" placeholder="Quantity" name="quantity" required>
                    </div>
                    <div class="col-md-3">
                        <input type="number" class="form-control" placeholder="Price" name="price" step="0.01" required>
                    </div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-danger btn-sm remove-item"><i class="fas fa-trash"></i></button>
                    </div>
                </div>
            `;
            $('#itemsContainer').append(row);
        });

        // Remove Item Row
        $(document).on('click', '.remove-item', function() {
            $(this).closest('.item-row').remove();
        });

        // Submit Form
        $('#createPoForm').submit(function(e) {
            e.preventDefault();
            
            const items = [];
            $('.item-row').each(function() {
                items.push({
                    itemId: $(this).find('input[name="itemId"]').val(),
                    quantity: $(this).find('input[name="quantity"]').val(),
                    price: $(this).find('input[name="price"]').val()
                });
            });

            const data = {
                supplierId: $('#supplierId').val(),
                items: items
            };

            $.ajax({
                url: '/api/procurement/orders',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    alert('Order created successfully!');
                    window.location.href = '/procurement';
                },
                error: function(xhr) {
                    alert('Error creating order: ' + xhr.responseText);
                }
            });
        });
    }

    // --- Inventory Module ---

    // List Stock
    if ($('#stockTable').length) {
        $('#stockTable').DataTable({
            ajax: {
                url: '/api/inventory/stock',
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                { data: 'item.name', defaultContent: 'Item #' + 'item.id' }, // Fallback if name missing
                { data: 'warehouse.name', defaultContent: 'Warehouse #' + 'warehouse.id' },
                { data: 'onHand' }
            ]
        });
    }

    // Receive Goods Form
    if ($('#receiveGoodsForm').length) {
        $('#addReceiveItemBtn').click(function() {
            const row = `
                <div class="row mb-2 item-row">
                    <div class="col-md-6">
                        <input type="number" class="form-control" placeholder="Item ID" name="itemId" required>
                    </div>
                    <div class="col-md-4">
                        <input type="number" class="form-control" placeholder="Quantity" name="quantity" required>
                    </div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-danger btn-sm remove-item"><i class="fas fa-trash"></i></button>
                    </div>
                </div>
            `;
            $('#receiveItemsContainer').append(row);
        });

        $('#receiveGoodsForm').submit(function(e) {
            e.preventDefault();
            
            const items = [];
            $('.item-row').each(function() {
                items.push({
                    itemId: $(this).find('input[name="itemId"]').val(),
                    quantity: $(this).find('input[name="quantity"]').val()
                });
            });

            const data = {
                purchaseOrderId: $('#purchaseOrderId').val(),
                warehouseId: $('#warehouseId').val(),
                items: items
            };

            $.ajax({
                url: '/api/inventory/receipts',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    alert('Goods received successfully!');
                    window.location.href = '/inventory';
                },
                error: function(xhr) {
                    alert('Error receiving goods: ' + xhr.responseText);
                }
            });
        });
    }

    // --- Manufacturing Module ---

    // List Work Orders
    if ($('#workOrderTable').length) {
        $('#workOrderTable').DataTable({
            ajax: {
                url: '/api/manufacturing/work-orders',
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                { data: 'item.name', defaultContent: 'Item #' + 'item.id' },
                { data: 'quantity' },
                { 
                    data: 'status',
                    render: function(data) {
                        let badgeClass = 'bg-secondary';
                        if (data === 'COMPLETED') badgeClass = 'bg-success';
                        if (data === 'IN_PROGRESS') badgeClass = 'bg-primary';
                        if (data === 'PLANNED') badgeClass = 'bg-info';
                        return `<span class="badge ${badgeClass}">${data}</span>`;
                    }
                },
                { data: 'startDate', defaultContent: '-' },
                { data: 'endDate', defaultContent: '-' },
                {
                    data: 'id',
                    render: function(data) {
                        return `
                            <button onclick="updateWoStatus(${data}, 'RELEASED')" class="btn btn-primary btn-sm" title="Release">Release</button>
                            <button onclick="updateWoStatus(${data}, 'COMPLETED')" class="btn btn-success btn-sm" title="Complete">Complete</button>
                        `;
                    }
                }
            ]
        });
    }

    // Create Work Order Form
    if ($('#createWorkOrderForm').length) {
        $('#createWorkOrderForm').submit(function(e) {
            e.preventDefault();
            
            const data = {
                itemId: $('#itemId').val(),
                quantity: $('#quantity').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val()
            };

            $.ajax({
                url: '/api/manufacturing/work-orders',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    alert('Work Order created successfully!');
                    window.location.href = '/manufacturing';
                },
                error: function(xhr) {
                    alert('Error creating work order: ' + xhr.responseText);
                }
            });
        });
    }

    // --- MRP Module ---

    // List MRP Runs
    if ($('#mrpRunsTable').length) {
        $('#mrpRunsTable').DataTable({
            ajax: {
                url: '/api/mrp/runs',
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                { data: 'runDate' },
                { data: 'horizonDays' },
                {
                    data: 'id',
                    render: function(data) {
                        return `<a href="#" class="btn btn-info btn-sm">View Planned Orders</a>`;
                    }
                }
            ]
        });
    }

    // Run MRP Form
    if ($('#runMrpForm').length) {
        $('#runMrpForm').submit(function(e) {
            e.preventDefault();
            
            const horizonDays = $('#horizonDays').val();

            $.ajax({
                url: '/api/mrp/run?horizonDays=' + horizonDays,
                type: 'POST',
                success: function(response) {
                    alert('MRP Run completed successfully!');
                    window.location.href = '/mrp';
                },
                error: function(xhr) {
                    alert('Error running MRP: ' + xhr.responseText);
                }
            });
        });
    }

});

// Global Functions (outside document.ready to be accessible)

function approveOrder(id) {
    if(confirm('Are you sure you want to approve this order?')) {
        $.ajax({
            url: '/api/procurement/orders/' + id + '/approve',
            type: 'POST',
            success: function(response) {
                alert('Order approved!');
                $('#procurementTable').DataTable().ajax.reload();
            },
            error: function(xhr) {
                alert('Error approving order: ' + xhr.responseText);
            }
        });
    }
}

function updateWoStatus(id, status) {
    if(confirm('Update status to ' + status + '?')) {
        $.ajax({
            url: '/api/manufacturing/work-orders/' + id + '/status?status=' + status,
            type: 'PUT',
            success: function(response) {
                alert('Status updated!');
                $('#workOrderTable').DataTable().ajax.reload();
            },
            error: function(xhr) {
                alert('Error updating status: ' + xhr.responseText);
            }
        });
    }
}
