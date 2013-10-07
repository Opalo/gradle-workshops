
$(function() {

    $.fn.serializeObject = function() {
       var o = {};
       var a = this.serializeArray();
       $.each(a, function() {
           if (o[this.name] !== undefined) {
               if (!o[this.name].push) {
                   o[this.name] = [o[this.name]];
               }
               o[this.name].push(this.value || '');
           } else {
               o[this.name] = this.value || '';
           }
       });
       return o;
    };

    $('#ca-list-container').hide();
    $('#ca-form-container').hide();

    $('#btn-list').click(function() {
        console.log('btn-list clicked')
        $('#ca-list-container').hide();
        $('#ca-form-container').hide();

         $.ajax({
            type : 'GET',
            dataType : 'json',
            contentType : 'application/json',
            url : '@backend@/rest/credit/list',
            success : function(r) {
                console.log('CA list response', r);
                if(r.length > 0) {
                    var tr;
                    for (var i = 0; i < r.length; i++) {
                        console.log('CA',r[i])
                        tr = $('<tr/>');
                        tr.append("<td>" + r[i].firstName + "</td>");
                        tr.append("<td>" + r[i].lastName + "</td>");
                        tr.append("<td>" + r[i].email + "</td>");
                        tr.append("<td>" + r[i].amount + "</td>");
                        tr.append("<td>" + r[i].purpose + "</td>");
                        $('#ca-list-table').append(tr);
                    }
                    $('#ca-list-container').show();
                }
            },
            error : function(e) {
                console.log('CA list error', e);
            }
         });
    });

    $('#btn-new-ca').click(function() {
        $('#ca-list-container').hide();
        $('#ca-form-container').hide();
        $('#ca-form-container').show();

        console.log('btn-new-ca clicked')
    });

    $('#ca-form-submit').click(function(e) {
        console.log('#ca-form-submit clicked')
        e.preventDefault();
        var formJSON = JSON.stringify($('#ca-form').serializeObject());
        console.log('JSON', formJSON);
        $.ajax({
            type : 'POST',
            dataType : 'json',
            contentType : 'application/json',
            url : '@backend@/rest/credit/save',
            data : formJSON,
            success : function(r) {
                console.log('CA save response', r);
                alert('Added: ' + r['id']);
                $('#ca-form').get(0).reset();
                $('#ca-form-container').hide();
            },
            error : function(e) {
                console.log('CA save error',e);
            }
        });
    });
});