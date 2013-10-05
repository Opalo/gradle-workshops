$(function() {
    $('#btn-list').click(function() {
        console.log('btn-list clicked')
         $.ajax({
            type : 'GET',
            dataType : 'json',
            contentType : 'application/json',
            url : '@backend@/rest/credit/list',
            success : function(r) {
                console.log('bolo');
                console.log(r);
            }
         });
    });

    $('#btn-new-ca').click(function() {
        console.log('btn-new-ca clicked')
    });
});