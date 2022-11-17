// Scripts

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});

function getMainContent(page) {
    isLogged();
    if (page === null) page = 'dashboard';
    if (event) event.preventDefault();
    window.history.replaceState("","Loaded Page",page);
    var url = '/view/' + page.toLowerCase();
    $.post(url,null,function(data, textStatus, jqXHR) {
        $('#replacedMain').html(data);
        delete table;
        if($('#simpleTable').length){
          table = $('#simpleTable').DataTable({
           responsive: true
          });
        }
    }).fail(function(jqXHR){
       showAlert("Error!", jqXHR.responseJSON.error, jqXHR.status);
    });
};

function getJsonFromForm(formId) {
  var params = {};
  $('#'+formId +' input, #' + formId + ' select, #'+ formId +' textarea, #' + formId +' checkbox').each(
      function(index){
          var input = $(this);
          if (input[0].type === "checkbox" && input[0].checked) {
            params[input.attr('name')] = true;
            return;
          }
          params[input.attr('name')] = input.val();
      }
  );
  return JSON.stringify(params);
}

function hideModal(){
  $('.modal-backdrop').remove();
  $('.modal').modal('hide');
}

function showAlert(msgTitle, msgBody, msgType) {
    $('#alertMsg').find('strong').html(msgTitle + " Status: " + msgType);
    $('#alertMsg').find('p').html(msgBody);
    $('#alertMsg').show();
}

/* User operations */

async function isLogged() {
    let url = '/authorize';
    let response = await fetch(url);
    if (response.status === 200) {
     let data = await response.text();
     if (data == 'false') {
        window.location.href = "/login?expired";
     }
    }
}

/* ServiceUrl operations */

function addServiceModal(){
  $('#serviceUrlForm' ).trigger("reset");
  $('#operation').val('post');
  $('#serviceModal').modal('show');
};

function sendServiceForm(){
  event.preventDefault();
  var formData = getJsonFromForm('serviceUrlForm');
    $.ajax({
        type: $('#operation').val(),
        crossDomain: true,
        url: "/api/service/",
        data: formData,
        contentType: "application/json"
    })
    .done(function(data, textStatus, jqXHR){
      hideModal();
      getMainContent('services');
    })
    .fail(function(jqXHR, error, message){
       hideModal();
       showAlert(error, jqXHR.responseJSON.message, jqXHR.status);
    });
   };

async function getServiceUrl(id) {
    event.preventDefault();
    let url = '/api/service/' + id;
    let response = await fetch(url);
    if (response.status === 200) {
        let data = await response.json();
        $('#id').val(data['id']);
        $('#operation').val('put');
        $('#urlAddress').val(data['urlAddress']);
        $('#responseType').val(data['responseType'].toString()).change();
        $('#requestSchedule').val(data['requestSchedule'].toString()).change();
       // $('#enebled').val(data['enebled'].toString()).change();
        $('#serviceModal').modal('show');
    }
};

function deleteServiceUrl(id) {
    event.preventDefault();
    bootbox.confirm("Confirm delete operation?", function(result) {
     if(result){
        let url = '/api/service/' + id;
        fetch(url,{
           method: 'DELETE',
           headers: {
             'Content-Type': 'application/json'
           },
           body: null
           })
            .then((response) => {
              if (response.ok) {
                return getMainContent('services');
              }
              return Promise.reject(response);
            })
            .catch((response) => {
                response.json().then((json) => {
                    showAlert('Error', json.message, json.code);
                })
            });
        }
    });
};
