// Modules JS - Handles logic for ERP Modules

$(document).ready(function() {
    
    // --- Procurement Module ---

    // List Orders
    if ($('#procurementTable').length) {
        $('#procurementTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/tr.json'
            }
        });
    }

    // Create Order Form
    if ($('#createPoForm').length) {
        // Add Item Row
        $('#addItemBtn').click(function() {
            const row = $('#itemRowTemplate').html();
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
                // Skip the template row if it's somehow selected (it shouldn't be as it's outside #itemsContainer)
                // But we are iterating .item-row inside #itemsContainer ideally.
                // The template is outside, but let's be safe.
                if ($(this).parent().attr('id') === 'itemRowTemplate') return;

                const itemId = $(this).find('[name="itemId"]').val();
                const quantity = $(this).find('[name="quantity"]').val();
                const price = $(this).find('[name="price"]').val();

                if (itemId && quantity && price) {
                    items.push({
                        itemId: itemId,
                        quantity: quantity,
                        price: price
                    });
                }
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
                    alert('Sipariş başarıyla oluşturuldu!');
                    window.location.href = '/procurement';
                },
                error: function(xhr) {
                    alert('Hata: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Bilinmeyen hata'));
                }
            });
        });
    }
                    alert('Error creating order: ' + xhr.responseText);
                }
            });
        });
    }

    // --- Inventory Module ---

    // List Stock
    if ($('#stockTable').length) {
        $('#stockTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/tr.json'
            }
        });
    }

    // Receive Goods Form
    if ($('#receiveGoodsForm').length) {
        $('#addReceiveItemBtn').click(function() {
            const row = $('#receiveItemRowTemplate').html();
            $('#receiveItemsContainer').append(row);
        });

        $('#receiveGoodsForm').submit(function(e) {
            e.preventDefault();
            
            const items = [];
            $('.item-row').each(function() {
                if ($(this).parent().attr('id') === 'receiveItemRowTemplate') return;

                const itemId = $(this).find('[name="itemId"]').val();
                const quantity = $(this).find('[name="quantity"]').val();
                
                if (itemId && quantity) {
                    items.push({
                        itemId: itemId,
                        quantity: quantity
                    });
                }
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
                    alert('Mal kabul işlemi başarıyla tamamlandı!');
                    window.location.href = '/inventory';
                },
                error: function(xhr) {
                    alert('Hata: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Bilinmeyen hata'));
                }
            });
        });
    }

    // --- Manufacturing Module ---

    // List Work Orders
    if ($('#workOrderTable').length) {
        $('#workOrderTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/tr.json'
            }
        });
    }

    // Dummy placeholder to maintain structure
    if (false) {
        var dummy = function(data) {
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
